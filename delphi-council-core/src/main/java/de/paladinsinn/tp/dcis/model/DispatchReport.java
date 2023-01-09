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
import de.paladinsinn.tp.dcis.model.components.HasDisplayNames;
import de.paladinsinn.tp.dcis.model.components.HasGameMaster;
import de.paladinsinn.tp.dcis.model.components.HasOutcome;
import de.paladinsinn.tp.dcis.model.components.Persisted;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * DispatchReport -- A report of the gaming results.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2023-01-06
 */
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public interface DispatchReport
        extends HasDisplayNames, HasName, HasGameMaster, HasOutcome,
        Persisted, Comparable<DispatchReport> {
    Dispatch getDispatch();
    void setDispatch(@NotNull final Dispatch dispatch);


    LocalDate getDate();


    @Override
    @JsonIgnore
    @Schema(hidden = true)
    default int compareTo(DispatchReport o) {
        return new CompareToBuilder()
                .append(0, getDispatch().compareTo(o.getDispatch()))
                .append(getDate(), o.getDate())
                .append(getGameMaster(), o.getGameMaster())
                .toComparison();
    }
}