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
import de.kaiserpfalzedv.commons.core.resources.HasName;
import de.paladinsinn.tp.dcis.model.components.HasNotes;
import de.paladinsinn.tp.dcis.model.components.HasOperative;
import de.paladinsinn.tp.dcis.model.components.Persisted;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * OperativeSpecialReport -- The special report for a local table game.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.3.0  2021-04-18
 */
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public interface OperativeMissionReport
        extends HasName, HasOperative, HasNotes,
        Persisted, Comparable<OperativeMissionReport> {

    Mission getMission();


    @Override
    @JsonIgnore
    default String getName() {
        return getOperative().getName();
    }



    @Override
    @JsonIgnore
    @Schema(hidden = true)
    default int compareTo(OperativeMissionReport o) {
        return new CompareToBuilder()
                .append(getMission(), o.getMission())
                .append(getOperative(), o.getOperative())
                .toComparison();
    }
}
