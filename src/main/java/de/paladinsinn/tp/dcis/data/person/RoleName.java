/*
 * Copyright (c) 2021 Kaiserpfalz EDV-Service, Roland T. Lichti
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.paladinsinn.tp.dcis.data.person;

import com.sun.istack.NotNull;
import org.apache.commons.compress.utils.Sets;

import java.util.HashSet;
import java.util.Set;

public enum RoleName {
    /**
     * Torg player.
     */
    PERSON,
    /**
     * Torg GM.
     */
    GM,
    /**
     * Campaign judge.
     */
    JUDGE,
    /**
     * Campaign orga team.
     */
    ORGA,
    /**
     * Campaign admin team.
     */
    ADMIN;

    public Set<String> getRoleNames() {
        return Sets.newHashSet(
                PERSON.name(),
                GM.name(),
                JUDGE.name(),
                ORGA.name(),
                ADMIN.name()
        );
    }

    /**
     * Special method for PersonForm.
     *
     * @return All roles but GM.
     */
    public Set<String> getRoleNamesWithoutGM() {
        return Sets.newHashSet(
                PERSON.name(),
                JUDGE.name(),
                ORGA.name(),
                ADMIN.name()
        );
    }

    public Set<String> getActiveRoleNames(@NotNull final Person person) {
        HashSet<String> result = new HashSet<>(person.getRoles().size());

        for (Role r : person.getRoles()) {
            result.add(r.getAuthority());
        }

        return result;
    }
}
