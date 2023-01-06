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
import de.paladinsinn.tp.dcis.model.components.*;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_ABSENT)
@Schema(description = "Delphi Council Mission")
public interface Mission
        extends HasName, HasCode, HasDescription, HasDisplayNames, HasClearance,
        Persisted, Comparable<Mission> {
    String getImage();

    @Schema(description = "Payment for every storm knight taking this mission.")
    int getPayment();

    @Schema(description = "XP for every storm knight taking this mission.")
    int getXp();

    @Schema(description = "Objectives defined for a successful mission result.", maxLength = 4000, nullable = true)
    String getObjectivesSuccess();

    @Schema(description = "Objectives defined for a good mission result.", maxLength = 40000, nullable = true)
    String getObjectivesGood();

    @Schema(description = "Objectives defined for an outstanding mission result.", maxLength = 4000, nullable = true)
    String getObjectivesOutstanding();

    @Schema(description = "The publication this mission is defined in.", maxLength = 100, nullable = true)
    String getPublication();


    @JsonIgnore
    @Override
    @Schema(hidden = true)
    default int compareTo(final Mission o) {
        return new CompareToBuilder()
                .append(getCode(), o.getCode())
                .toComparison();
    }
}
