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

package de.paladinsinn.delphicouncil.app.security;

import com.sun.istack.NotNull;
import de.paladinsinn.delphicouncil.data.person.Person;
import de.paladinsinn.delphicouncil.data.person.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.time.OffsetDateTime;

import static java.time.ZoneOffset.UTC;

/**
 * AuthenticationHandler --
 *
 * @author klenkes74 {literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-04-05
 */
@Service
public class AuthenticationHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationHandler.class);

    private final PersonService service;

    @Autowired
    public AuthenticationHandler(@NotNull final PersonService service) {
        this.service = service;
    }

    @Override
    @Transactional
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        super.onAuthenticationSuccess(request, response, authentication);

        Person user = service.findByUsername(authentication.getName());
        if (user == null) {
            LOG.error("Authentication problem. User not found. authenticationName='{}'", authentication.getName());
            return;
        }

        user.getStatus().setLastLogin(OffsetDateTime.now(UTC));

        service.save(user);
        LOG.debug("Saved login. user='{}', lastLogin={}", authentication.getName(), user.getStatus().getLastLogin());
    }
}