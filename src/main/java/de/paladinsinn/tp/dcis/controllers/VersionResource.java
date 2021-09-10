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

package de.paladinsinn.tp.dcis.controllers;

import org.eclipse.microprofile.config.inject.ConfigProperty;


import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * VersionController -- Returns the version.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.3.0  2021-04-24
 */

@ApplicationScoped
@Path("/api/meta")
@Produces(MediaType.TEXT_PLAIN)
public class VersionResource {
    @ConfigProperty(name = "quarkus.application.version")
    private String version;

    @ConfigProperty(name = "application.api.version")
    private String apiVersion;

    @GET
    @Path("version")
    public String getVersion() {
        return String.format("%s %s", version, apiVersion);
    }

    @GET
    @Path("app-version")
    public String getAppVersion() {
        return version;
    }

    @GET
    @Path("api-version")
    public String getApiVersion() {
        return apiVersion;
    }
}
