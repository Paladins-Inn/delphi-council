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

import com.vaadin.flow.component.ComponentEventListener;
import de.paladinsinn.tp.dcis.events.PersonSaveEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * PersonService -- Handling persistence for {@link PersonService}.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-04-05
 */
@Service
public class PersonService extends CrudService<Person, UUID> implements ComponentEventListener<PersonSaveEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(PersonService.class);

    private final PersonRepository repository;

    public PersonService(@Autowired PersonRepository repository) {
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
            repository.saveAndFlush(data);
            LOG.info("Saved {}. data={}", getClass().getSimpleName(), data);

            event.getSource().displayNote(
                    "input.data.saved.success",
                    "person.editor.title",
                    data.getId().toString()
            );
        } catch (Exception e) {
            LOG.error("Could not " + getClass().getSimpleName() + ". data=" + data, e);

            event.getSource().displayNote(
                    "input.data.saved.failed",
                    "person.editor.title",
                    data.getId().toString(),
                    e.getLocalizedMessage()
            );
        }
    }

    public Person findByUsername(String username) {
        return repository.findByUsername(username);
    }

    public Person findByRoles(Role role) {
        return repository.findByRoles(role);
    }

    public List<Person> findAll() {
        return repository.findAll();
    }

    public List<Person> findAll(Sort sort) {
        return repository.findAll(sort);
    }

    public List<Person> findAllById(Iterable<UUID> uuids) {
        return repository.findAllById(uuids);
    }

    public <S extends Person> List<S> saveAll(Iterable<S> entities) {
        return repository.saveAll(entities);
    }

    public void flush() {
        repository.flush();
    }

    public <S extends Person> S saveAndFlush(S entity) {
        return repository.saveAndFlush(entity);
    }

    public void deleteInBatch(Iterable<Person> entities) {
        repository.deleteInBatch(entities);
    }

    public void deleteAllInBatch() {
        repository.deleteAllInBatch();
    }

    public Person getOne(UUID uuid) {
        return repository.getOne(uuid);
    }

    public <S extends Person> List<S> findAll(Example<S> example) {
        return repository.findAll(example);
    }

    public <S extends Person> List<S> findAll(Example<S> example, Sort sort) {
        return repository.findAll(example, sort);
    }

    public Page<Person> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public <S extends Person> S save(S entity) {
        return repository.save(entity);
    }

    public Optional<Person> findById(UUID uuid) {
        return repository.findById(uuid);
    }

    public boolean existsById(UUID uuid) {
        return repository.existsById(uuid);
    }

    public void deleteById(UUID uuid) {
        repository.deleteById(uuid);
    }

    public void delete(Person entity) {
        repository.delete(entity);
    }

    public void deleteAll(Iterable<? extends Person> entities) {
        repository.deleteAll(entities);
    }

    public void deleteAll() {
        repository.deleteAll();
    }

    public <S extends Person> Optional<S> findOne(Example<S> example) {
        return repository.findOne(example);
    }

    public <S extends Person> Page<S> findAll(Example<S> example, Pageable pageable) {
        return repository.findAll(example, pageable);
    }

    public <S extends Person> long count(Example<S> example) {
        return repository.count(example);
    }

    public <S extends Person> boolean exists(Example<S> example) {
        return repository.exists(example);
    }
}
