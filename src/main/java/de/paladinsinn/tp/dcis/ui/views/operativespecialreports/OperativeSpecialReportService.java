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

package de.paladinsinn.tp.dcis.ui.views.operativespecialreports;

import com.vaadin.flow.component.ComponentEventListener;
import de.paladinsinn.tp.dcis.data.operative.OperativeReport;
import de.paladinsinn.tp.dcis.data.operative.OperativeSpecialReport;
import de.paladinsinn.tp.dcis.data.operative.OperativeSpecialReportRepository;
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
public class OperativeSpecialReportService extends CrudService<OperativeSpecialReport, UUID> implements ComponentEventListener<OperativeSpecialReportSaveEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(OperativeSpecialReportService.class);

    private static final String DATA_TYPE_TITLE = "mission.editor.caption";

    private final OperativeSpecialReportRepository repository;

    public OperativeSpecialReportService(@Autowired OperativeSpecialReportRepository repository) {
        this.repository = repository;
    }

    @Override
    protected OperativeSpecialReportRepository getRepository() {
        return repository;
    }

    @Override
    public void onComponentEvent(OperativeSpecialReportSaveEvent event) {
        OperativeSpecialReport data = event.getReport();

        try {
            OperativeSpecialReport saved = repository.saveAndFlush(data);
            LOG.info("Saved {}. data={}", getClass().getSimpleName(), data);

            event.getSource().setData(saved);

            new TorgNotification("input.data.saved.success",
                    null,
                    Collections.singletonList(DATA_TYPE_TITLE),
                    Arrays.asList(
                            data.getId().toString(),
                            data.getSpecialMission().getCode().toString()
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
                            data.getSpecialMission().getCode().toString()
                    )
            ).open();
        }
    }
}
