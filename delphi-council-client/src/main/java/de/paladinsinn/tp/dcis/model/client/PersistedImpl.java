/*
 * Copyright (c) 2023. Roland T. Lichti
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.paladinsinn.tp.dcis.model.client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import de.kaiserpfalzedv.commons.core.resources.Persisted;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;


/**
 * PersistedImpl -- Default implementation for holding persistence data of the resource.
 *
 * @author klenkes {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2023-01-06
 */
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public abstract class PersistedImpl implements Persisted {
    @ToString.Include
    @EqualsAndHashCode.Include
    @NotNull
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @ToString.Include
    @EqualsAndHashCode.Include
    @NotNull
    @Builder.Default
    private Integer version = 0;

    @Builder.Default
    @NotNull
    private OffsetDateTime created = OffsetDateTime.now(ZoneOffset.UTC);

    @ToString.Include
    private OffsetDateTime modified;

    private OffsetDateTime revisioned;

    @SneakyThrows
    @Override
    @JsonIgnore
    @Schema(hidden = true)
    public PersistedImpl clone() {
        PersistedImpl result = (PersistedImpl) super.clone();

        result.id = id;
        result.version = version;
        result.created = created;
        result.modified = modified;
        result.revisioned = revisioned;

        return result;
    }
}
