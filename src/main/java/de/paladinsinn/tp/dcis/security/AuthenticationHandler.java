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

package de.paladinsinn.tp.dcis.security;

import com.sun.istack.NotNull;
import com.vaadin.flow.server.VaadinSession;
import de.paladinsinn.tp.dcis.data.person.Person;
import de.paladinsinn.tp.dcis.data.person.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
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
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-04-05
 */
@Service
@Slf4j
public class AuthenticationHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private final PersonRepository repository;
    private final String defaultLocale;

    /**
     * @param repository    the repository to request person data from.
     * @param defaultLocale the default locale if no other is selected.
     */
    @Autowired
    public AuthenticationHandler(
            @NotNull final PersonRepository repository,
            @NotNull @Value("${spring.web.locale:de}") final String defaultLocale
    ) {
        this.repository = repository;
        this.defaultLocale = defaultLocale;
    }

    /**
     * @return the logged in user.
     */
    @Bean
    @Scope("prototype")
    public LoggedInUser createUser() {
        return new LoggedInUser(repository, defaultLocale);
    }

    @Override
    @Transactional
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        Person user = repository.findByUsername(authentication.getName());
        if (user == null) {
            log.error("Authentication problem. User not found. authenticationName='{}'", authentication.getName());
            return;
        }

        user.getStatus().setLastLogin(OffsetDateTime.now(UTC));

        repository.save(user);

        log.debug("Saved login. user='{}', lastLogin={}", authentication.getName(), user.getStatus().getLastLogin());

        VaadinSession.getCurrent().setLocale(user.getLocale());
        log.debug("Set locale. locale={}", user.getLocale());

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
