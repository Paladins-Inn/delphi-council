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

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * ConfirmationTokenRepository -- The generic repository to access {@link ConfirmationToken}.
 *
 * @author Kamer Elciar (<a href="https://kamer-dev.medium.com/spring-boot-user-registration-and-login-43a33ea19745">https://kamer-dev.medium.com/spring-boot-user-registration-and-login-43a33ea19745</a>)
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-04-10
 */
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, UUID> {
}