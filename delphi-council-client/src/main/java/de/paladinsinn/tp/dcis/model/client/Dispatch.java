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
import de.paladinsinn.tp.dcis.common.Language;
import lombok.*;
import lombok.experimental.SuperBuilder;

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
public class Dispatch extends PersistedImpl implements de.paladinsinn.tp.dcis.model.Dispatch {

    @ToString.Include
    private Language language;

    @ToString.Include
    private String code;

    private String image;

    @ToString.Include
    private String name;

    private Clearance clearance;

    private String description;

    private int xp;

    private int payment;

    private String objectivesSuccess;

    private String objectivesGood;

    private String objectivesOutstanding;

    private String publication;


    public static Dispatch copyData(final de.paladinsinn.tp.dcis.model.Dispatch orig) {
        if (Dispatch.class.isAssignableFrom(orig.getClass())) {
            return (Dispatch) orig;
        }

        return Dispatch.builder()
                .id(orig.getId())
                .version(orig.getVersion())
                .created(orig.getCreated())
                .modified(orig.getModified())
                .revisioned(orig.getRevisioned())

                .code(orig.getCode())
                .image(orig.getImage())
                .name(orig.getName())
                .clearance(orig.getClearance())
                .description(orig.getDescription())

                .xp(orig.getXp())
                .payment(orig.getPayment())
                .objectivesSuccess(orig.getObjectivesSuccess())
                .objectivesGood(orig.getObjectivesGood())
                .objectivesOutstanding(orig.getObjectivesOutstanding())

                .publication(orig.getPublication())
                .build();
    }


    @SneakyThrows
    @Override
    public Dispatch clone() {
        return toBuilder().build();
    }
}
