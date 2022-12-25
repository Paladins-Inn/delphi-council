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

import io.smallrye.config.ConfigMapping;

import java.util.Map;

@ConfigMapping(prefix = "quarkus.security.users.embedded")
public interface JwtApplicationConfig {
    boolean enabled();
    boolean plainText();
    String realmName();
    Map<String, String> users();
    Map<String, String> roles();
}
