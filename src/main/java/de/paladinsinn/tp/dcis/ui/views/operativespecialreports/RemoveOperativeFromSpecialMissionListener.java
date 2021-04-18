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

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.UUID;

/**
 * RemoveOperativeFromSpecialMissionListener --
 *
 * @author klenkes74 {literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-04-11
 */
@Service
@AllArgsConstructor
public class RemoveOperativeFromSpecialMissionListener implements ComponentEventListener<RemoveOperativeFromSpecialMissionEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(RemoveOperativeFromSpecialMissionListener.class);

    private final SpecialMissionRepository specialMissionRepository;
    private final OperativeSpecialReportRepository operativeSpecialReportRepository;
    private final OperativeRepository operativeRepository;

    @Override
    @Transactional
    public void onComponentEvent(RemoveOperativeFromSpecialMissionEvent event) {
        OperativeSpecialReport report = event.getOperativeReport();

        SpecialMission mission = report.getSpecialMission();
        Operative operative = report.getOperative();

        String missionId = report.getId().toString();
        UUID missionCode = report.getSpecialMission().getCode();
        String operativeId = report.getOperative().getId().toString();
        String operativeName = report.getOperative().getName();

        LOG.info("Deleting operative report. report={}, execution={}, mission='{}', operative='{}'",
                report.getId(), missionId, missionCode, operativeName);

        report.setOperative(null);
        report.setSpecialMission(null);

        try {
            operativeRepository.save(operative);
            specialMissionRepository.save(mission);
            operativeSpecialReportRepository.delete(report);

            new TorgNotification(
                    "missionreport.remove-operative.success",
                    null,
                    null,
                    Arrays.asList(
                            report.getId().toString(), missionId, operativeId,
                            missionCode.toString(), operativeName
                    )
            ).open();
        } catch (DataAccessException e) {
            new TorgNotification(
                    "missionreport.remove-operative.failed",
                    null,
                    null,
                    Arrays.asList(
                            report.getId().toString(), missionId, operativeId,
                            missionCode.toString(), operativeName
                    )
            ).open();
        }
    }
}
