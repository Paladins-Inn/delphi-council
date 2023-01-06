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
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Operative -- The Storm Knight.
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
public class OperativeReport extends PersistedImpl implements de.paladinsinn.tp.dcis.model.OperativeReport {
    private MissionReport report;

    private Operative operative;

    private String achievements;
    private String notes;


    public static OperativeReport copyData(de.paladinsinn.tp.dcis.model.OperativeReport orig) {
        if (OperativeReport.class.isAssignableFrom(orig.getClass())) {
            return (OperativeReport) orig;
        }

        return OperativeReport.builder()
                .id(orig.getId())
                .version(orig.getVersion())
                .created(orig.getCreated())
                .modified(orig.getModified())
                .revisioned(orig.getRevisioned())

                .report(MissionReport.copyData(orig.getReport()))
                .operative(Operative.copyData(orig.getOperative()))
                .achievements(orig.getAchievements())
                .notes(orig.getNotes())
                .build();
    }

    @SneakyThrows
    @Override
    public OperativeReport clone() {
        return toBuilder().build();
    }
}
