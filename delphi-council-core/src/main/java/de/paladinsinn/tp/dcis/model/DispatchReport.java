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
import de.paladinsinn.tp.dcis.model.components.HasGameMaster;
import de.paladinsinn.tp.dcis.model.components.HasOutcome;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * <p>DispatchReport -- Outcome of a {@link Dispatch}.</p>
 *
 * <p>Every dispatch needs a report so the campaign can keep track of the development of the world and the Storm Knights
 * in the Torganized Play. The report belongs either to one mission or is one of many reports for an {@link Operation}.
 * The outcomes of all DispatchReports of an {@link Operation} will be analyzed and determine the development of the
 * shared campaign world.</p>
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2023-01-06
 */
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public interface DispatchReport
        extends HasGameMaster, HasOutcome,
                Persisted, Comparable<DispatchReport> {
    /**
     * @return The date of the deployment.
     */
    OffsetDateTime getDate();

    /**
     * @return The ID of the {@link Operation} describing the deployment.
     */
    Optional<UUID> getOperation();

    /**
     * @return The ID of the {@link Mission} describing the deployment.
     */
    Optional<UUID> getMission();

    /**
     *
     * @return The Storm Knights of this report.
     */
    Set<UUID> getOperatives();


    @Override
    @JsonIgnore
    @Schema(hidden = true)
    default int compareTo(DispatchReport o) {
        CompareToBuilder result = new CompareToBuilder();

        return result
                .append(getOperation(), o.getOperation())
                .append(getMission(), o.getMission())
                .append(getDate(), o.getDate())
                .append(getGameMaster(), o.getGameMaster())
                .toComparison();
    }
}
