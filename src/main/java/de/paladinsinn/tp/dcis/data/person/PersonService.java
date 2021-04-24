/*
 * Copyright (c) 2021 Kaiserpfalz EDV-Service, Roland T. Lichti
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.paladinsinn.tp.dcis.data.person;

import com.sun.istack.NotNull;
import com.vaadin.flow.server.VaadinSession;
import de.paladinsinn.tp.dcis.mail.EmailSenderService;
import de.paladinsinn.tp.dcis.ui.i18n.Translator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataAccessException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.Locale;
import java.util.UUID;

/**
 * PersonService --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-03-29
 */
@Service
@Transactional
public class PersonService implements UserDetailsService {
    private static final Logger LOG = LoggerFactory.getLogger(PersonService.class);

    private final String mailFromName;
    private final String mailFromAddress;

    private final PersonRepository personRepository;
    private final ConfirmationTokenService confirmationTokenRepository;

    private final EmailSenderService emailSender;
    private final Translator translator;

    /**
     * Creates a person service.
     *
     * @param mailFromName                Name for sending emails from.
     * @param mailFromAddress             Address to send emails from.
     * @param personRepository            The repository to request persons from the persistence layer.
     * @param confirmationTokenRepository The repository to request tokens from the persistence layer.
     * @param emailSender                 The service for sending emails.
     * @param translator                  The translater for i18n.
     */
    @Autowired
    public PersonService(
            @Value("${MAIL_FROM_NAME:Registration Clerk}") @NotNull final String mailFromName,
            @Value("${MAIL_FROM_ADDRESS:clerk@delphi-council.org}") @NotNull final String mailFromAddress,
            @NotNull final PersonRepository personRepository,
            @NotNull final ConfirmationTokenService confirmationTokenRepository,
            @NotNull final EmailSenderService emailSender,
            @NotNull final Translator translator
    ) {
        this.mailFromName = mailFromName;
        this.mailFromAddress = mailFromAddress;
        this.personRepository = personRepository;
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.emailSender = emailSender;
        this.translator = translator;
    }

    @Override
    public UserDetails loadUserByUsername(final String userName) {
        Person result = personRepository.findByUsername(userName);
        if (result == null) {
            throw new UsernameNotFoundException(userName);
        }

        return result;
    }

    /**
     * @param person person to create a confirmation token for.
     * @return The confirmation token for the given person.
     */
    public ConfirmationToken signUp(@NotNull Person person) {
        person.getStatus().setEnabled(false);
        Person saved = personRepository.save(person);

        ConfirmationToken token = new ConfirmationToken(saved);
        token = confirmationTokenRepository.save(token);

        sendConfirmationMail(saved.getEmail(), token.getId());

        LOG.info("Created user confirmation token. token={}", token);
        return token;
    }

    /**
     * @param email email address to send the email to.
     * @param token The token for this person.
     */
    public void sendConfirmationMail(@NotNull final String email, @NotNull final UUID token) {
        LOG.info("Sending confirmation request. email='{}', token={}", email, token);

        final SimpleMailMessage message = new SimpleMailMessage();

        Locale locale = VaadinSession.getCurrent().getLocale();

        message.setTo(email);
        message.setFrom(mailFromName + " <" + mailFromAddress + ">");
        message.setSubject(translator.getTranslation("mail.confirmation.subject", locale, email, token.toString()));
        message.setText(translator.getTranslation("mail.confirmation.text", locale, email, token.toString()));

        emailSender.send(message);
    }

    public Person confirmUser(@NotNull ConfirmationToken token) throws IllegalStateException {
        LOG.debug("Confirming user. token={}", token);
        Person person = token.getPerson();

        try {
            person.getStatus().setEnabled(true);
            person.setRoles(Collections.singleton(new Role(RoleName.PERSON)));

            person = personRepository.save(person);

            confirmationTokenRepository.delete(token.getId());
        } catch (DataAccessException e) {
            LOG.error("Can't confirm user.", e);

            throw new IllegalStateException("Can't confirm user with token '" + token.getId() + "'.", e);
        }

        LOG.info("User confirmed. token={}, person={}", token, person);
        return person;
    }

    /**
     * @return the user details for the logged in person.
     */
    @Bean
    public UserDetails getUserDetails() {
        try {
            return (UserDetails) SecurityContextHolder.getContext()
                    .getAuthentication().getDetails();
        } catch (NullPointerException e) {
            LOG.warn("Can't retrieve user details: {}", e.getMessage());
            return null;
        }
    }
}
