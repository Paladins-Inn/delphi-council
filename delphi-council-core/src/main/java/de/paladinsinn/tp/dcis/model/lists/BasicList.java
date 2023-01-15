/*
 * Copyright (c) 2023. Roland T. Lichti
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.paladinsinn.tp.dcis.model.lists;


import com.fasterxml.jackson.annotation.JsonInclude;
import de.kaiserpfalzedv.commons.core.api.Paging;
import de.kaiserpfalzedv.commons.core.resources.HasKind;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * DispatchmentFile contains all data to a single dispatchment
 *
 * @author klenkes {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2023-01-06
 */
@Schema(description = "A basic list for overviews.")
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(onlyExplicitlyIncluded = true)
public class BasicList implements HasKind, Serializable {
    @Size(min = 3, max = 100, message = "The length of the string must be between 3 and 100 characters long.")
    @Pattern(
            regexp = "^[a-zA-Z][-a-zA-Z0-9]{1,61}(.[a-zA-Z][-a-zA-Z0-9]{1,61}){0,4}$",
            message = "The string must match the pattern '^[a-zA-Z][-a-zA-Z0-9]{1,61}(.[a-zA-Z][-a-zA-Z0-9]{1,61}){0,4}$'"
    )
    @Schema(description = "The type of list", required = true)
    private String kind;

    @ToString.Include
    @NotNull
    @Schema(description = "The pagination of this result set.", required = true)
    private Paging page;

    @Builder.Default
    @NotNull
    @Schema(description = "The data itself", required = true, minItems = 0, maxItems = 2147483647)
    final List<BasicData> data = new ArrayList<>();
}
