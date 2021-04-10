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

package de.paladinsinn.tp.dcis.ui.views.operativereports;

import com.vaadin.flow.component.ComponentEventListener;
import de.paladinsinn.tp.dcis.data.operative.OperativeReport;
import de.paladinsinn.tp.dcis.data.operative.OperativeReportRepository;
import de.paladinsinn.tp.dcis.ui.components.TorgNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

/**
 * OperativeReportService -- A CrudService implementation for {@link OperativeReport}s}.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-03-26
 */
@Service
public class OperativeReportService extends CrudService<OperativeReport, UUID> implements ComponentEventListener<OperativeReportSaveEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(OperativeReportService.class);

    private static final String DATA_TYPE_TITLE = "mission.editor.caption";

    private final OperativeReportRepository repository;

    public OperativeReportService(@Autowired OperativeReportRepository repository) {
        this.repository = repository;
    }

    @Override
    protected OperativeReportRepository getRepository() {
        return repository;
    }

    @Override
    public void onComponentEvent(OperativeReportSaveEvent event) {
        OperativeReport data = event.getReport();

        try {
            OperativeReport saved = repository.saveAndFlush(data);
            LOG.info("Saved {}. data={}", getClass().getSimpleName(), data);

            event.getSource().setData(saved);

            new TorgNotification("input.data.saved.success",
                    null,
                    Collections.singletonList(DATA_TYPE_TITLE),
                    Arrays.asList(
                            data.getId().toString(),
                            data.getReport().getMission().getCode()
                    )
            ).open();

            event.getSource().getUI().ifPresent(ui -> ui.getPage().getHistory().back());
        } catch (Exception e) {
            LOG.error("Could not save " + getClass().getSimpleName() + ". data=" + data, e);

            new TorgNotification("input.data.saved.failed",
                    ev -> {
                    },
                    Collections.singletonList(DATA_TYPE_TITLE),
                    Arrays.asList(
                            data.getId().toString(),
                            data.getReport().getMission().getCode()
                    )
            ).open();
        }
    }
}
