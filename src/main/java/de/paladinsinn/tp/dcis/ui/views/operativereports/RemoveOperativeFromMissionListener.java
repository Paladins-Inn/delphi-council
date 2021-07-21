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
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;

/**
 * RemoveOperativeFromMissionListener --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-04-11
 */
@Service
@AllArgsConstructor
@Slf4j
public class RemoveOperativeFromMissionListener implements ComponentEventListener<RemoveOperativeFromMissionEvent> {
    private final MissionReportRepository missionReportRepository;
    private final OperativeRepository operativeRepository;
    private final OperativeReportRepository operativeReportRepository;

    @Override
    @Transactional
    public void onComponentEvent(RemoveOperativeFromMissionEvent event) {
        OperativeReport report = event.getOperativeReport();

        MissionReport mission = report.getReport();
        Operative operative = report.getOperative();

        String missionId = report.getReport().getId().toString();
        String missionCode = report.getReport().getMission().getCode();
        String operativeId = report.getOperative().getId().toString();
        String operativeName = report.getOperative().getName();

        log.info("Deleting operative report. report={}, execution={}, mission='{}', operative='{}'",
                report.getId(), missionId, missionCode, operativeName);

        report.setOperative(null);
        report.setMissionReport(null);

        try {
            operativeRepository.save(operative);
            missionReportRepository.save(mission);

            operativeReportRepository.delete(report);

            new TorgNotification(
                    "missionreport.remove-operative.success",
                    null,
                    null,
                    Arrays.asList(
                            report.getId().toString(), missionId, operativeId,
                            missionCode, operativeName
                    )
            ).open();
        } catch (DataAccessException e) {
            new TorgNotification(
                    "missionreport.remove-operative.failed",
                    null,
                    null,
                    Arrays.asList(
                            report.getId().toString(), missionId, operativeId,
                            missionCode, operativeName
                    )
            ).open();
        }
    }
}
