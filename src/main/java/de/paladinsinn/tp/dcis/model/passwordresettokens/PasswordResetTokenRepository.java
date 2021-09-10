/*
 * Copyright (c) 2021 Kaiserpfalz EDV-Service, Roland T. Lichti
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package de.paladinsinn.tp.dcis.model.passwordresettokens;

import de.paladinsinn.tp.dcis.model.person.Person;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

/**
 * PasswordResetTokenRepository -- The generic repository to access {@link PasswordResetToken}.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.1.0  2021-05-07
 */
public interface PasswordResetTokenRepository extends PanacheRepositoryBase<PasswordResetToken, UUID> {
    /**
     * Retrieve all token for a single person.
     *
     * @param person Person to find all tokens for.
     * @return List with all tokens for the given person.
     */
    List<PasswordResetToken> findAllByPerson(@NotNull Person person);
}