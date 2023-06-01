/*
 * Copyright (c) 2022-2023. Roland T. Lichti
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.paladinsinn.tp.dcis.model.components;

import de.kaiserpfalzedv.commons.api.resources.HasId;
import de.paladinsinn.tp.dcis.model.Operative;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.Set;

/**
 * HasMultipleOperative --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2023-01-06
 */
public interface HasMultipleOperatives extends HasId {
    /**
     * @return the operatives of this object.
     */
    @Schema(description = "Die Storm Knights dieser Resource.")
    Set<Operative> getOperatives();
}
