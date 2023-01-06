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
import de.kaiserpfalzedv.rpg.torg.model.actors.Clearance;
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
public class SpecialMission extends PersistedImpl implements de.paladinsinn.tp.dcis.model.SpecialMission {
    private String code;

    private String name;
    private String gameMaster;
    private LocalDate date;

    private String image;

    private Clearance clearance;

    private String description;

    private int xp;

    private int payment;

    private SuccessState objectivesMet;
    private String achievements;
    private String notes;

    private String publication;


    @SneakyThrows
    public static SpecialMission copyData(de.paladinsinn.tp.dcis.model.SpecialMission orig)  {
        if (SpecialMission.class.isAssignableFrom(orig.getClass())) {
            return (SpecialMission) orig;
        }

        return SpecialMission.builder()
                .id(orig.getId())
                .version(orig.getVersion())
                .created(orig.getCreated())
                .modified(orig.getModified())

                .code(orig.getCode())
                .name(orig.getName())
                .date(orig.getDate())
                .image(orig.getImage())
                .clearance(orig.getClearance())
                .description(orig.getDescription())
                .xp(orig.getXp())
                .payment(orig.getPayment())

                .objectivesMet(orig.getObjectivesMet())
                .achievements(orig.getAchievements())
                .notes(orig.getNotes())

                .publication(orig.getPublication())
                .gameMaster(orig.getGameMaster())

                .build();
    }
    @SneakyThrows
    @Override
    public SpecialMission clone() {
        return toBuilder().build();
    }
}
