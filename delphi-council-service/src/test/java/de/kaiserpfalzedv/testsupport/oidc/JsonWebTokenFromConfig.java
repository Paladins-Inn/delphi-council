/*
 * Copyright (c) 2022. Roland T. Lichti
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.kaiserpfalzedv.testsupport.oidc;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
@Setter
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@Slf4j
public class JsonWebTokenFromConfig {
    public static final String ISSUER = "https://sso.kaiserpfalz-edv.de/";
    private JwtApplicationConfig config;

    private String name;

    @PostConstruct
    public void init() {
        log.info("Created JsonWebTokenFromConfig. this={}", this);
    }
    @Produces
    public JsonWebToken token() {
        return generateJWT(name);
    }

    private JsonWebToken generateJWT(final String name) {
        return new JsonWebToken() {
            Map<String, Object> claims = createClaims();

            @Override
            public String getName() {
                return name;
            }

            @Override
            public Set<String> getClaimNames() {
                return claims.keySet();
            }

            @Override
            public <T> T getClaim(String s) {
                return (T) claims.get(s);
            }

            private Map<String, Object> createClaims() {
                HashMap<String, Object> result = new HashMap<>();

                result.put("iss", ISSUER);
                result.put("sub", name);
                result.put("exp", Instant.now().plusSeconds(30L).getEpochSecond());
                result.put("roles", generateRoleList());

                return result;
            }

            private Set<String> generateRoleList() {
                log.debug("Generating role list. name='{}', config={}", name, config);

                if (config != null && !config.roles().isEmpty()) {
                    return Arrays.stream(config.roles().get(name).split(",")).collect(Collectors.toSet());
                } else {
                    return Collections.emptySet();
                }
            }
        };
    }
}
