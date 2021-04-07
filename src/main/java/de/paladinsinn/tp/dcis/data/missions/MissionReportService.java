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

package de.paladinsinn.tp.dcis.data.missions;

import com.vaadin.flow.component.ComponentEventListener;
import de.paladinsinn.tp.dcis.events.MissionGroupReportSaveEvent;
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
public class MissionReportService extends CrudService<MissionReport, UUID> implements ComponentEventListener<MissionGroupReportSaveEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(MissionReportService.class);

    private final MissionReportRepository repository;

    public MissionReportService(@Autowired MissionReportRepository repository) {
        this.repository = repository;
    }

    @Override
    protected MissionReportRepository getRepository() {
        return repository;
    }


    @Override
    public void onComponentEvent(MissionGroupReportSaveEvent event) {
        MissionReport data = event.getReport();

        try {
            repository.saveAndFlush(data);
            LOG.info("Saved mission report. data={}", data);

            event.getSource().displayNote(
                    "input.data.saved.success",
                    "missionreport.editor.title",
                    data.getId().toString()
            );
        } catch (Exception e) {
            LOG.error("Could not save mission report. data=" + data, e);

            event.getSource().displayNote(
                    "input.data.saved.failed",
                    "missionreport.editor.title",
                    data.getId().toString(),
                    e.getLocalizedMessage()
            );
        }
    }

    public List<MissionReport> findAll() {
        return repository.findAll();
    }

    public List<MissionReport> findAll(Sort sort) {
        return repository.findAll(sort);
    }

    public List<MissionReport> findAllById(Iterable<UUID> uuids) {
        return repository.findAllById(uuids);
    }

    public <S extends MissionReport> List<S> saveAll(Iterable<S> entities) {
        return repository.saveAll(entities);
    }

    public void flush() {
        repository.flush();
    }

    public <S extends MissionReport> S saveAndFlush(S entity) {
        return repository.saveAndFlush(entity);
    }

    public void deleteInBatch(Iterable<MissionReport> entities) {
        repository.deleteInBatch(entities);
    }

    public void deleteAllInBatch() {
        repository.deleteAllInBatch();
    }

    public MissionReport getOne(UUID uuid) {
        return repository.getOne(uuid);
    }

    public <S extends MissionReport> List<S> findAll(Example<S> example) {
        return repository.findAll(example);
    }

    public <S extends MissionReport> List<S> findAll(Example<S> example, Sort sort) {
        return repository.findAll(example, sort);
    }

    public Page<MissionReport> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public <S extends MissionReport> S save(S entity) {
        return repository.save(entity);
    }

    public Optional<MissionReport> findById(UUID uuid) {
        return repository.findById(uuid);
    }

    public boolean existsById(UUID uuid) {
        return repository.existsById(uuid);
    }

    public void deleteById(UUID uuid) {
        repository.deleteById(uuid);
    }

    public void delete(MissionReport entity) {
        repository.delete(entity);
    }

    public void deleteAll(Iterable<? extends MissionReport> entities) {
        repository.deleteAll(entities);
    }

    public void deleteAll() {
        repository.deleteAll();
    }

    public <S extends MissionReport> Optional<S> findOne(Example<S> example) {
        return repository.findOne(example);
    }

    public <S extends MissionReport> Page<S> findAll(Example<S> example, Pageable pageable) {
        return repository.findAll(example, pageable);
    }

    public <S extends MissionReport> long count(Example<S> example) {
        return repository.count(example);
    }

    public <S extends MissionReport> boolean exists(Example<S> example) {
        return repository.exists(example);
    }
}
