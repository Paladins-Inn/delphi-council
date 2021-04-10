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
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.UUID;

/**
 * PersonService --
 *
 * @author klenkes74 {literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-03-29
 */
@Service
public class PersonService implements UserDetailsService {
    private static final Logger LOG = LoggerFactory.getLogger(PersonService.class);

    private final String mailFromName;
    private final String mailFromAddress;

    private final PersonRepository personRepository;
    private final ConfirmationTokenService confirmationTokenRepository;

    private final EmailSenderService emailSender;
    private final Translator translator;

    @Autowired
    public PersonService(
            @Value("${MAIL_FROM_NAME: Registration}") @NotNull final String mailFromName,
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

    public ConfirmationToken signUp(@NotNull Person person) {
        person.getStatus().setEnabled(false);
        Person saved = personRepository.save(person);

        ConfirmationToken token = new ConfirmationToken(saved);

        LOG.info("Created user confirmation token. token={}", token);
        return confirmationTokenRepository.save(token);
    }

    public void sendConfirmationMail(@NotNull final String email, @NotNull final UUID token) {
        final SimpleMailMessage message = new SimpleMailMessage();

        Locale locale = VaadinSession.getCurrent().getLocale();

        message.setTo(email);
        message.setFrom(mailFromName + " <" + mailFromAddress + ">");
        message.setSubject(translator.getTranslation("mail.confirmation.subject", locale, email, token));
        message.setText(translator.getTranslation("mail.confirmation.subject", locale, email, token));

        emailSender.send(message);
    }

    public Person confirmUser(@NotNull ConfirmationToken token) {
        Person person = token.getPerson();
        person.getStatus().setEnabled(true);
        confirmationTokenRepository.delete(token.getId());

        person = personRepository.save(person);

        LOG.info("User confirmed. token={}", token);
        return person;
    }

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
