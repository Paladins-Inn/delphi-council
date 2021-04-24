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
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * ConfirmationTokenService -- Handles the token for user self registration.
 *
 * @author Kamer Elciar (<a href="https://kamer-dev.medium.com/spring-boot-user-registration-and-login-43a33ea19745">https://kamer-dev.medium.com/spring-boot-user-registration-and-login-43a33ea19745</a>)
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-04-10
 */
@Service
@AllArgsConstructor
public class ConfirmationTokenService {
    private static final Logger LOG = LoggerFactory.getLogger(ConfirmationTokenService.class);

    private final ConfirmationTokenRepository repository;

    /**
     * Saves the token to the persistence.
     *
     * @param token generated token.
     * @return persisted token.
     */
    public ConfirmationToken save(@NotNull final ConfirmationToken token) {
        LOG.debug("Saving confirmation token. data={}", token);

        return repository.saveAndFlush(token);
    }

    /**
     * Delete the confirmation token.
     *
     * @param id The token to remove.
     */
    public void delete(@NotNull final UUID id) {
        LOG.debug("Removing token. id={}", id);

        repository.deleteById(id);
    }

    /**
     * Loads the given token.
     *
     * @param id token.
     * @return The token with person data.
     */
    public Optional<ConfirmationToken> load(@NotNull final UUID id) {
        LOG.debug("Loading token. id={}", id);

        return repository.findById(id);
    }
}
