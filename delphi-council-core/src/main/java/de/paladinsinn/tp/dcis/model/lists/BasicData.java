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
import de.kaiserpfalzedv.commons.core.resources.HasId;
import de.kaiserpfalzedv.commons.core.resources.HasName;
import de.paladinsinn.tp.dcis.model.components.HasCode;
import de.paladinsinn.tp.dcis.model.components.HasDescription;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(description = "Any data as list.")
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class BasicData implements HasId, HasName, HasDescription, HasCode {
    @ToString.Include
    @EqualsAndHashCode.Include
    @Schema(description = "Unique ID of the resource.", example = "06eb5cc7-ab04-4abd-9ca8-e8d9d1d49e39")
    private UUID id;

    @Schema(description = "Creation date of this resource", example = "2023-01-01T01:00:00Z")
    private OffsetDateTime created;

    @Schema(description = "Modification date of this resource", example = "2023-01-01T01:00:00Z")
    private OffsetDateTime modified;


    @ToString.Include
    @Schema(description = "Human readable code of this resource", example="DE-LL-001")
    private String code;

    @ToString.Include
    @Schema(description = "Name of this resource", example="Quinn Sebastian")
    private String name;

    @Schema(description = "Description of this resource", example = "A short description of the resource.")
    private String description;

    @Schema(description = "A URL for a small image for the resource", example = "http://localhost/image.jpg")
    private String url;
}
