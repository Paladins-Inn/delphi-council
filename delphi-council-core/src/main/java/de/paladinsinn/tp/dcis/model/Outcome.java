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
import de.kaiserpfalzedv.rpg.torg.model.core.SuccessState;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.validation.constraints.Size;
import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_ABSENT)
@Schema(description = "Outcome of a dispatch or mission")
public interface Outcome extends Serializable, Comparable<Outcome> {

    SuccessState getObjectivesMet();

    @Schema(description = "The additional achievements of the outcome.", minLength = 0, maxLength = 4000)
    @Size(min = 0, max = 4000, message = "4000 Characters max.")
    String getAchievements();

    @Schema(description = "Additional notes to the outcome.", minLength = 0, maxLength = 4000)
    @Size(min = 0, max = 4000, message = "4000 Characters max.")
    String getNotes();

    @JsonIgnore
    @Override
    @Schema(hidden = true)
    default int compareTo(final Outcome o) {
        return new CompareToBuilder()
                .append(getObjectivesMet(), o.getObjectivesMet())
                .build();
    }
}
