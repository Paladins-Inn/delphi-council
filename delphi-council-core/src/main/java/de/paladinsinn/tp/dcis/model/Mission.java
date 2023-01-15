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
import de.paladinsinn.tp.dcis.model.components.HasGameMaster;
import de.paladinsinn.tp.dcis.model.components.HasOutcome;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.OffsetDateTime;

/**
 * <p>Mission -- A private table mission.</p>
 *
 * <p>A mission is the registration of a private play with Torganized Play to register the results for the compaign and
 * character development.</p>
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-04-18
 */
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public interface Mission extends Dispatch, HasOutcome, HasGameMaster, Comparable<Mission> {
    OffsetDateTime getDate();

    @Override
    @JsonIgnore
    @Schema(hidden = true)
    default int compareTo(Mission o) {
        return new CompareToBuilder()
                .append(getId(), o.getId())
                .toComparison();
    }
}
