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
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * Role -- The roles of the users.
 *
 * Is also a direct implementation of {@link GrantedAuthority} for usage with spring web security.
 *
 * @author klenkes74 {literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-03-29
 */
@Embeddable
public class Role implements GrantedAuthority {
    @Column(name = "ROLE", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleName storedRole;

    /**
     * @deprecated only used by JPA.
     */
    @Deprecated
    public Role() {
    }

    public Role(@NotNull final RoleName role) {
        storedRole = role;
    }

    @Override
    public String getAuthority() {
        return storedRole.name();
    }
}
