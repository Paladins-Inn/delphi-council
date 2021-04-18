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
import de.paladinsinn.tp.dcis.data.missions.MissionReport;
import de.paladinsinn.tp.dcis.data.missions.MissionReportRepository;
import de.paladinsinn.tp.dcis.data.operative.Operative;
import de.paladinsinn.tp.dcis.data.operative.OperativeReport;
import de.paladinsinn.tp.dcis.data.operative.OperativeReportRepository;
import de.paladinsinn.tp.dcis.data.operative.OperativeRepository;
import de.paladinsinn.tp.dcis.ui.components.TorgNotification;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * AddOperatorToMissionListener --
 *
 * @author klenkes74 {literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-04-11
 */
@Service
@AllArgsConstructor
public class AddOperativeToMissionListener implements ComponentEventListener<AddOperativeToMissionEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(AddOperativeToMissionListener.class);


    private final MissionReportRepository missionReportRepository;
    private final OperativeRepository operativeRepository;
    private final OperativeReportRepository operativeReportRepository;

    @Override
    public void onComponentEvent(AddOperativeToMissionEvent event) {
        Operative operative = event.getOperative();
        MissionReport mission = event.getMission();

        try {
            OperativeReport report = new OperativeReport();
            report.setMissionReport(mission);
            report.setOperative(operative);
            report = operativeReportRepository.save(report);
            LOG.debug("Created new operative report. data={}", report);

            operative.addOperativeReport(report);
            mission.addOperativeReport(report);

            operative = operativeRepository.save(operative);
            LOG.trace("Saved operative report to operative. operative={}", operative);

            mission = missionReportRepository.save(mission);
            LOG.info("Added operative report. report={}, execution={}, mission='{}', operative='{}'",
                    report.getId(), mission.getId(), mission.getMission().getCode(), operative.getName());

            new TorgNotification(
                    "missionreport.add-operatives.success",
                    null,
                    null,
                    Arrays.asList(
                            report.getId().toString(), mission.getId().toString(), operative.getId().toString(),
                            mission.getMission().getCode(), operative.getName()
                    )
            ).open();
        } catch (DataAccessException e) {
            new TorgNotification(
                    "missionreport.add-operatives.failed",
                    null,
                    null,
                    Arrays.asList(
                            "---", mission.getId().toString(), operative.getId().toString(),
                            mission.getMission().getCode(), operative.getName()
                    )
            ).open();

            throw new IllegalStateException("Data could not be changed.", e);
        }
    }
}
