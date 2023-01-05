/*
 * Copyright (c) 2022. Roland T. Lichti
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.paladinsinn.torganized.core.common;

import de.kaiserpfalzedv.commons.core.resources.HasName;

/**
 * HasDisplayNames -- this resource has displayable names for the resource.
 *
 * The display name is generated from the name and the code of the resource. The code may be the database ID or a
 * second business key.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2023-01-05
 */
public interface HasDisplayNames extends HasName, HasCode {
    static final int MAX_LENGTH = 17;

    /**
     * Returns the full name consisting of the name and the code of the resource.
     *
     * @return The full name (the name may be longer than 20 characters).
     */
    default String getFullName() {
        return (getName() == null ? "" : getName()) + (getCode() != null ? " (" + getCode() + ")" : "");
    }

    /**
     * Returns the short name consisting of the first 17 chracters and the code of the resource.
     *
     * @return The (shortened) display name.
     */
    default String getShortName() {
        if (getName() == null || getName().length() <= MAX_LENGTH + 3) {
            return getFullName();
        }

        return getName().substring(0, MAX_LENGTH) + (getCode() != null ? "... (" + getCode() + ")" : "");
    }
}
