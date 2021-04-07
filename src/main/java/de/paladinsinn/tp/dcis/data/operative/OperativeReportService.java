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

package de.paladinsinn.tp.dcis.data.operative;

import com.vaadin.flow.component.ComponentEventListener;
import de.paladinsinn.tp.dcis.events.OperatorReportSaveEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

import java.util.UUID;

@Service
public class OperativeReportService extends CrudService<OperativeReport, UUID> implements ComponentEventListener<OperatorReportSaveEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(OperativeReportService.class);


    private final OperativeReportRepository repository;

    public OperativeReportService(@Autowired OperativeReportRepository repository) {
        this.repository = repository;
    }

    @Override
    protected OperativeReportRepository getRepository() {
        return repository;
    }

    @Override
    public void onComponentEvent(OperatorReportSaveEvent event) {
        OperativeReport data = event.getReport();

        try {
            repository.saveAndFlush(data);
            LOG.info("Saved mission report. data={}", data);

            event.getSource().displayNote(
                    "input.data.saved.success",
                    "operative.editor.title",
                    data.getId().toString()
            );
        } catch (Exception e) {
            LOG.error("Could not save mission report. data=" + data, e);

            event.getSource().displayNote(
                    "input.data.saved.failed",
                    "operative.editor.title",
                    data.getId().toString(),
                    e.getLocalizedMessage()
            );
        }
    }
}
