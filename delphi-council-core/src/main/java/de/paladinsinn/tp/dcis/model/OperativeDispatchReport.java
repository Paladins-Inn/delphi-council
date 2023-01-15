/*
 * Copyright (c) 2022-2023. Roland T. Lichti
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.paladinsinn.tp.dcis.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import de.kaiserpfalzedv.commons.core.resources.Persisted;
import de.paladinsinn.tp.dcis.model.components.HasAchievements;
import de.paladinsinn.tp.dcis.model.components.HasNotes;
import de.paladinsinn.tp.dcis.model.components.HasOperative;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * <p>OperativeDispatchReport -- The report of a single operative for an {@link DispatchReport}.</p>
 *
 * <p>In addition to the {@link DispatchReport} which describes the outcome of a {@link Dispatch} for the whole group,
 * the {@link} is the part of the personal file of the {@link Operative} for this single {@link Dispatch}. These are the
 * personal achievements and notes to a single Storm Knight.</p>
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-04-04
 */
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public interface OperativeDispatchReport
        extends HasOperative, HasAchievements, HasNotes,
                Persisted, Comparable<OperativeDispatchReport> {
    DispatchReport getReport();


    @Override
    @JsonIgnore
    @Schema(hidden = true)
    default int compareTo(OperativeDispatchReport o) {
        return new CompareToBuilder()
                .append(getReport(), o.getReport())
                .append(getOperative(), o.getOperative())
                .toComparison();
    }

}
