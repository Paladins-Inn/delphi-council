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
import de.paladinsinn.tp.dcis.data.person.PersonService;
import de.paladinsinn.tp.dcis.ui.views.login.LoginView;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * PersonRegistrationListener --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-04-10
 */
@Service
@AllArgsConstructor
public class PersonRegistrationListener implements ComponentEventListener<PersonRegistrationEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(PersonRegistrationListener.class);

    private final PersonService personService;

    @Override
    public void onComponentEvent(@NotNull final PersonRegistrationEvent event) {
        LOG.info("User want's to register. event={}", event);

        personService.signUp(event.getPerson());

        event.getSource().getUI().ifPresent(ui -> ui.navigate(LoginView.ROUTE));
    }
}
