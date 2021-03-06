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
import de.paladinsinn.tp.dcis.data.person.Person;
import de.paladinsinn.tp.dcis.data.person.PersonRepository;
import de.paladinsinn.tp.dcis.ui.components.TorgNotification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

/**
 * PersonSaveService -- Handling persistence for {@link PersonSaveService}.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-04-05
 */
@Service
@Slf4j
public class PersonSaveService extends CrudService<Person, UUID> implements ComponentEventListener<PersonSaveEvent> {
    private static final String DATA_TYPE_TITLE = "person.editor.caption";

    private final PersonRepository repository;

    public PersonSaveService(@Autowired PersonRepository repository) {
        this.repository = repository;
    }

    @Override
    protected PersonRepository getRepository() {
        return repository;
    }

    @Override
    public void onComponentEvent(PersonSaveEvent event) {
        Person data = event.getPerson();

        try {
            Person saved = repository.saveAndFlush(data);
            log.info("Saved {}. data={}, saved={}", getClass().getSimpleName(), data, saved);

            event.getSource().setData(saved);

            new TorgNotification("input.data.saved.success",
                    null,
                    Collections.singletonList(DATA_TYPE_TITLE),
                    Arrays.asList(data.getId().toString(), data.getName())
            ).open();
        } catch (Exception e) {
            log.error("Could not save " + getClass().getSimpleName() + ". data=" + data, e);

            new TorgNotification("input.data.saved.failed",
                    ev -> {
                    },
                    Collections.singletonList(DATA_TYPE_TITLE),
                    Arrays.asList(data.getId().toString(), data.getName())
            ).open();
        }
    }
}
