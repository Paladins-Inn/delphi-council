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

package de.paladinsinn.tp.dcis.model.person;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * PersonRepository -- The generic repository to access persons.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-03-27
 */
public interface PersonRepository extends PanacheRepositoryBase<Person, UUID> {
    /**
     * Retrieves a person by its username.
     *
     * @param username username to log into the system.
     * @return The person (if found).
     */
    Person findByUsername(@NotNull String username);

    /**
     * Retrieves all persons holding the requested role.
     *
     * @param role role to collect all persons for.
     * @return A list of persons holding the requested role.
     */
    List<Person> findByRoles(@NotNull Role role);

    /**
     * Retrieves the persons with either username or email address as requested.
     *
     * @param username The username of the person.
     * @param email    The email address of the person.
     * @return A list of persons matching the username or the password. It should be only one ...
     */
    List<Person> findByUsernameOrEmail(@NotNull String username, @NotNull String email);
}