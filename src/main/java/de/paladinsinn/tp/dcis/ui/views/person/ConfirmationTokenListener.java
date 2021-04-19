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

import com.sun.istack.NotNull;
import com.vaadin.flow.component.ComponentEventListener;
import de.paladinsinn.tp.dcis.data.person.ConfirmationToken;
import de.paladinsinn.tp.dcis.data.person.ConfirmationTokenService;
import de.paladinsinn.tp.dcis.data.person.Person;
import de.paladinsinn.tp.dcis.data.person.PersonService;
import de.paladinsinn.tp.dcis.ui.components.TorgNotification;
import de.paladinsinn.tp.dcis.ui.views.login.LoginView;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

/**
 * PersonRegistrationListener --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-04-10
 */
@Service
@AllArgsConstructor
public class ConfirmationTokenListener implements ComponentEventListener<ConfirmationTokenEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(ConfirmationTokenListener.class);

    private final PersonService personService;
    private final ConfirmationTokenService tokenService;

    @Override
    public void onComponentEvent(@NotNull final ConfirmationTokenEvent event) {
        LOG.info("User wants to confirm registration. event={}", event);

        Optional<ConfirmationToken> token = tokenService.load(event.getToken());

        token.ifPresentOrElse(
                t -> {
                    try {
                        Person user = personService.confirmUser(t);

                        new TorgNotification(
                                "registration.invalid-token.user-confirmed",
                                null,
                                null,
                                Arrays.asList(event.getToken().toString(), user.getName(), user.getUsername())
                        ).open();
                    } catch (IllegalStateException e) {
                        new TorgNotification(
                                "registration.invalid-token.confirmation-failure",
                                null,
                                null,
                                Arrays.asList(event.getToken().toString())
                        ).open();
                    }
                },
                () -> new TorgNotification(
                        "registration.invalid-token.unknown-token",
                        null,
                        null,
                        Arrays.asList(event.getToken().toString())
                ).open()
        );

        event.getSource().getUI().ifPresent(ui -> ui.navigate(LoginView.ROUTE));
    }
}
