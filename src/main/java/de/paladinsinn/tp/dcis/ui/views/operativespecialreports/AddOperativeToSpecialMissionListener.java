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
import de.paladinsinn.tp.dcis.data.operative.Operative;
import de.paladinsinn.tp.dcis.data.operative.OperativeRepository;
import de.paladinsinn.tp.dcis.data.operative.OperativeSpecialReport;
import de.paladinsinn.tp.dcis.data.operative.OperativeSpecialReportRepository;
import de.paladinsinn.tp.dcis.data.specialmissions.SpecialMission;
import de.paladinsinn.tp.dcis.data.specialmissions.SpecialMissionRepository;
import de.paladinsinn.tp.dcis.ui.components.TorgNotification;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * AddOperativeToSpecialMissionListener --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.3.0  2021-04-18
 */
@Service
@AllArgsConstructor
public class AddOperativeToSpecialMissionListener implements ComponentEventListener<AddOperativeToSpecialMissionEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(AddOperativeToSpecialMissionListener.class);


    private final SpecialMissionRepository missionRepository;
    private final OperativeSpecialReportRepository missionReportRepository;
    private final OperativeRepository operativeRepository;

    @Override
    public void onComponentEvent(AddOperativeToSpecialMissionEvent event) {
        Operative operative = event.getOperative();
        SpecialMission mission = event.getMission();

        try {
            OperativeSpecialReport report = new OperativeSpecialReport();
            report.setSpecialMission(mission);
            report.setOperative(operative);
            report = missionReportRepository.save(report);
            LOG.debug("Created new operative report. data={}", report);

            operative.addSpecialReport(report);
            mission.addOperativeReport(report);

            operative = operativeRepository.save(operative);
            LOG.trace("Saved operative report to operative. operative={}", operative);

            mission = missionRepository.save(mission);
            LOG.info("Added operative report. report={}, execution={}, mission='{}', operative='{}'",
                    report.getId(), mission.getId(), mission.getCode(), operative.getName());

            new TorgNotification(
                    "missionreport.add-operatives.success",
                    null,
                    null,
                    Arrays.asList(
                            report.getId().toString(), mission.getId().toString(), operative.getId().toString(),
                            mission.getCode().toString(), operative.getName()
                    )
            ).open();
        } catch (DataAccessException e) {
            new TorgNotification(
                    "missionreport.add-operatives.failed",
                    null,
                    null,
                    Arrays.asList(
                            "---", mission.getId().toString(), operative.getId().toString(),
                            mission.getCode().toString(), operative.getName()
                    )
            ).open();

            throw new IllegalStateException("Data could not be changed.", e);
        }
    }
}
