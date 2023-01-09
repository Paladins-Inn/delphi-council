/*
 * Copyright (c) 2022-2023. Roland T. Lichti
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.paladinsinn.tp.dcis.client;

import de.paladinsinn.tp.dcis.model.lists.BasicList;
import io.quarkus.oidc.token.propagation.reactive.AccessTokenRequestReactiveFilter;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@RegisterRestClient(configKey = "mission-report-api")
@RegisterProvider(value = AccessTokenRequestReactiveFilter.class, priority = 5000)
@Path("/api/v1/missions/special")
public interface MissionReportClient {
    /**
     * Retrieves a page of data specified by the first element and the size of page.
     *
     * @param start the first element to retrieve.
     * @param size the size of the page to retrieve.
     * @return A list containing the paging data and the elements retrieved in standard format.
     */
    @GET
    @Path("/")
    BasicList retrieve(
            @QueryParam("start") long start,
            @QueryParam("size") long size
    );

    /**
     * Retrieves all elements of the type.
     * @return A list containing all elements of the store of this type.
     */
    @GET
    @Path("/")
    BasicList retrieve();
}
