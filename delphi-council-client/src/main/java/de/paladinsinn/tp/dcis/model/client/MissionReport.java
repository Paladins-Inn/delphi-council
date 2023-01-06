/*
 * Copyright (c) 2022-2023. Roland T. Lichti
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.paladinsinn.tp.dcis.model.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.kaiserpfalzedv.rpg.torg.model.core.SuccessState;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

/**
 * Mission -- The default implementation of a mission.
 *
 * @author klenkes {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2023-01-06
 */
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class MissionReport extends PersistedImpl implements de.paladinsinn.tp.dcis.model.MissionReport {
    private String code;
    private Mission mission;
    private String gameMaster;
    private LocalDate date;


    private SuccessState objectivesMet;
    private String achievements;
    private String notes;


    public String getName() {
        if (mission != null) {
            return mission + " (" + gameMaster + ", " + date + ")";
        } else {
            return "Mission Dispatch #" + getId();
        }
    }

    @Override
    public void setMission(de.paladinsinn.tp.dcis.model.Mission orig) {
        mission = Mission.copyData(orig);
    }


    public static MissionReport copyData(de.paladinsinn.tp.dcis.model.MissionReport report) {
        if (MissionReport.class.isAssignableFrom(report.getClass())) {
            return (MissionReport) report;
        }

        return MissionReport.builder()
                .mission(Mission.copyData(report.getMission()))
                .date(report.getDate())
                .gameMaster(report.getGameMaster())
                .notes(report.getNotes())
                .achievements(report.getAchievements())
                .build();
    }


    @SneakyThrows
    @Override
    public MissionReport clone() {
        return toBuilder().build();
    }
}
