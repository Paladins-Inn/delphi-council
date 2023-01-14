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
import de.paladinsinn.tp.dcis.model.Engagment;
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
public class Engagement extends PersistedImpl implements Engagment {
    private String code;
    private Dispatch dispatch;
    private String gameMaster;
    private LocalDate date;


    private SuccessState objectivesMet;
    private String achievements;
    private String notes;


    public String getName() {
        if (dispatch != null) {
            return dispatch + " (" + gameMaster + ", " + date + ")";
        } else {
            return "Mission Dispatch #" + getId();
        }
    }

    public void setDispatch(de.paladinsinn.tp.dcis.model.Dispatch orig) {
        dispatch = Dispatch.copyData(orig);
    }


    public static Engagement copyData(Engagment report) {
        if (Engagement.class.isAssignableFrom(report.getClass())) {
            return (Engagement) report;
        }

        return Engagement.builder()
                .dispatch(Dispatch.copyData(report.getDispatch()))
                .date(report.getDate())
                .gameMaster(report.getGameMaster())
                .notes(report.getNotes())
                .achievements(report.getAchievements())
                .build();
    }


    @SneakyThrows
    @Override
    public Engagement clone() {
        return toBuilder().build();
    }
}
