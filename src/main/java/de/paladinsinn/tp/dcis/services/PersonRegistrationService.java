/*
 * Copyright (c) 2021 Kaiserpfalz EDV-Service, Roland T. Lichti
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package de.paladinsinn.tp.dcis.services;

import de.paladinsinn.tp.dcis.model.confirmationtokens.ConfirmationToken;
import de.paladinsinn.tp.dcis.model.person.Person;
import de.paladinsinn.tp.dcis.model.person.Role;
import de.paladinsinn.tp.dcis.model.person.RoleName;
import io.quarkus.mailer.MailTemplate;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.i18n.Message;
import jakarta.validation.constraints.NotNull;
import de.kaiserpfalzedv.commons.core.i18n.Translator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.UUID;

/**
 * PersonService --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-03-29
 */
@ApplicationScoped
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@Transactional
@Slf4j
public class PersonRegistrationService {
    private final Translator translator;

    @CheckedTemplate
    static class Templates {
        public static native MailTemplate confirmation(Person person, String token);
    }


    /**
     * @param person person to create a confirmation token for.
     * @return The confirmation token for the given person.
     */
    public ConfirmationToken signUp(@NotNull Person person) {
        person.disable();
        person.persistAndFlush();

        ConfirmationToken token = new ConfirmationToken(person);
        token.persistAndFlush();

        sendConfirmationMail(person, token.getId());

        log.info("Created user confirmation token. token={}", token);
        return token;
    }

    /**
     * @param person email address to send the email to.
     * @param token The token for this person.
     */
    private void sendConfirmationMail(@NotNull final Person person, @NotNull final UUID token) {
        Templates
                .confirmation(person, token.toString())
                .to(person.getEmail())
                .subject(translator.getTranslation("mail.confirmation.subject", person.getLocale()))
                .send();
    }

    public Person confirmUser(@NotNull ConfirmationToken token) throws IllegalStateException {
        Person person = token.getPerson();
        log.debug("Confirming user. person={}, userName='{}', token='{}'", person.getId(), person.getUsername(), token);

        person.enable();

        person.persist();
        token.delete();

        log.info("User confirmed. token={}, person={}", token, person);
        return person;
    }
}
