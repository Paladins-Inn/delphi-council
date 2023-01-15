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
import de.paladinsinn.tp.dcis.model.components.HasClearance;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * <p>Operation -- A pre-defined operation of Torganized Play.</p>
 *
 * <p>Operations are the default games of the Torganized Play. They are defined and GMs play them at conventions in
 * person or online.</p>
 *
 * <p>The results drive the shared campaign.</p>
 *
 * @author klenkes {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2023-01-15
 */
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@Schema(description = "Delphi Council Mission")
public interface Operation extends Dispatch, HasClearance, Comparable<Operation> {
    @Schema(description = "Objectives defined for a successful mission.", maxLength = 40000, nullable = true)
    String getObjectivesSuccess();

    @Schema(description = "Objectives defined for a good mission result.", maxLength = 40000, nullable = true)
    String getObjectivesGood();

    @Schema(description = "Objectives defined for an outstanding mission result.", maxLength = 4000, nullable = true)
    String getObjectivesOutstanding();


    @JsonIgnore
    @Override
    @Schema(hidden = true)
    default int compareTo(final Operation o) {
        return new CompareToBuilder()
                .append(getCode(), o.getCode())
                .toComparison();
    }
}
