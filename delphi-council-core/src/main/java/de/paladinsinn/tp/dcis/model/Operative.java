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
import de.kaiserpfalzedv.commons.core.resources.Persisted;
import de.paladinsinn.tp.dcis.model.components.*;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.OffsetDateTime;

/**
 * <p>Operative -- The Storm Knights.</p>
 *
 * <p>Operatives are the Storm Knights fighting in the Possibility Wars.</p>
 *
 * @author klenkes {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public interface Operative
        extends HasName, HasDisplayNames, HasCosm, HasClearance, HasPlayer, HasAvatar, HasToken,
        Persisted, Comparable<Operative> {
    String getLastName();

    String getFirstName();


    int getXp();

    int getMoney();


    OffsetDateTime getDeleted();


    @Override
    @JsonIgnore
    @Schema(hidden = true)
    default int compareTo(Operative o) {
        return new CompareToBuilder()
                .append(getId(), o.getId())
                .toComparison();
    }
}
