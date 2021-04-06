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

package de.paladinsinn.delphicouncil.data.missions;

import com.vaadin.flow.component.ComponentEventListener;
import de.paladinsinn.delphicouncil.app.events.MissionSaveEvent;
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

@Service
public class MissionService extends CrudService<Mission, UUID> implements ComponentEventListener<MissionSaveEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(MissionService.class);

    private final MissionRepository repository;

    public MissionService(@Autowired MissionRepository repository) {
        this.repository = repository;
    }

    @Override
    protected MissionRepository getRepository() {
        return repository;
    }

    @Override
    public void onComponentEvent(MissionSaveEvent event) {
        Mission data = event.getMission();

        try {
            repository.saveAndFlush(data);
            LOG.info("Saved mission report. data={}", data);

            event.getSource().displayNote(
                    "input.data.saved.success",
                    "mission.editor.title",
                    data.getId().toString()
            );
        } catch (Exception e) {
            LOG.error("Could not save mission report. data=" + data, e);

            event.getSource().displayNote(
                    "input.data.saved.failed",
                    "mission.editor.title",
                    data.getId().toString(),
                    e.getLocalizedMessage()
            );
        }
    }

    public List<Mission> findAll() {
        return repository.findAll();
    }

    public List<Mission> findAll(Sort sort) {
        return repository.findAll(sort);
    }

    public List<Mission> findAllById(Iterable<UUID> uuids) {
        return repository.findAllById(uuids);
    }

    public <S extends Mission> List<S> saveAll(Iterable<S> entities) {
        return repository.saveAll(entities);
    }

    public void flush() {
        repository.flush();
    }

    public <S extends Mission> S saveAndFlush(S entity) {
        return repository.saveAndFlush(entity);
    }

    public void deleteInBatch(Iterable<Mission> entities) {
        repository.deleteInBatch(entities);
    }

    public void deleteAllInBatch() {
        repository.deleteAllInBatch();
    }

    public Mission getOne(UUID uuid) {
        return repository.getOne(uuid);
    }

    public <S extends Mission> List<S> findAll(Example<S> example) {
        return repository.findAll(example);
    }

    public <S extends Mission> List<S> findAll(Example<S> example, Sort sort) {
        return repository.findAll(example, sort);
    }

    public Page<Mission> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public <S extends Mission> S save(S entity) {
        return repository.save(entity);
    }

    public Optional<Mission> findById(UUID uuid) {
        return repository.findById(uuid);
    }

    public boolean existsById(UUID uuid) {
        return repository.existsById(uuid);
    }

    public void deleteById(UUID uuid) {
        repository.deleteById(uuid);
    }

    public void delete(Mission entity) {
        repository.delete(entity);
    }

    public void deleteAll(Iterable<? extends Mission> entities) {
        repository.deleteAll(entities);
    }

    public void deleteAll() {
        repository.deleteAll();
    }

    public <S extends Mission> Optional<S> findOne(Example<S> example) {
        return repository.findOne(example);
    }

    public <S extends Mission> Page<S> findAll(Example<S> example, Pageable pageable) {
        return repository.findAll(example, pageable);
    }

    public <S extends Mission> long count(Example<S> example) {
        return repository.count(example);
    }

    public <S extends Mission> boolean exists(Example<S> example) {
        return repository.exists(example);
    }
}
