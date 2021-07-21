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

package de.paladinsinn.tp.dcis.ui.views.person;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.server.VaadinSession;
import de.paladinsinn.tp.dcis.data.person.PasswordResetToken;
import de.paladinsinn.tp.dcis.data.person.PasswordResetTokenRepository;
import de.paladinsinn.tp.dcis.data.person.Person;
import de.paladinsinn.tp.dcis.data.person.PersonRepository;
import de.paladinsinn.tp.dcis.mail.EmailSenderService;
import de.paladinsinn.tp.dcis.ui.components.TorgNotification;
import de.paladinsinn.tp.dcis.ui.views.login.LoginView;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * StartPasswordResetListener -- Handles the start of password reset process (generating token and sending email).
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.1.0  2021-05-07
 */
@Service
@AllArgsConstructor
@Slf4j
public class StartPasswordResetListener implements ComponentEventListener<StartPasswordResetEvent> {
    private final PersonRepository personRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailSenderService sender;

    @Override
    public void onComponentEvent(@NotNull final StartPasswordResetEvent event) {
        log.info("User want's to reset his password. event={}", event);

        Optional<Person> person = loadPerson(event.getUsername(), event.getEmail());

        person.ifPresentOrElse(
                p -> generateAndSendToken(event, p),
                () -> reportResultAndReturnToLoginView(event, "password-reset.invalid-username-or-email")
        );
    }

    private Optional<Person> loadPerson(String username, String email) {
        List<Person> result = personRepository.findByUsernameOrEmail(username, email);

        if (result.size() == 1) {
            return Optional.of(result.get(0));
        }

        return Optional.empty();
    }

    private void generateAndSendToken(StartPasswordResetEvent event, Person person) {
        PasswordResetToken token = PasswordResetToken.builder()
                .person(person)
                .build();

        token = tokenRepository.saveAndFlush(token);

        String messageKey = sendPasswordResetEmail(person, token);

        reportResultAndReturnToLoginView(event, messageKey);
    }

    private String sendPasswordResetEmail(Person person, PasswordResetToken token) {
        HashMap<String, Object> params = new HashMap<>(2);
        params.put("person", person);
        params.put("token", token.getId());

        ArrayList<Object> subjectParams = new ArrayList<>(1);
        subjectParams.add(person.getUsername());

        String messageKey = "password-reset.send-registration.success";

        try {
            sender.send(
                    person.getEmail(),
                    "mail.password-reset.subject",
                    subjectParams,
                    "password-reset",
                    params,
                    VaadinSession.getCurrent().getLocale()
            );

            log.info("Password reset email sent. user='{}'", person.getUsername());
        } catch (MessagingException e) {
            log.warn("Could not send password reset email.", e);

            messageKey = "password-reset.send-registration.failure";
        }

        return messageKey;
    }

    private void reportResultAndReturnToLoginView(@NotNull StartPasswordResetEvent event, String s) {
        new TorgNotification(
                s,
                null,
                null,
                Arrays.asList(event.getUsername(), event.getEmail())
        ).open();

        event.getSource().getUI().ifPresent(ui -> ui.navigate(LoginView.ROUTE));
    }
}
