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

import de.paladinsinn.tp.dcis.model.Dispatch;
import de.paladinsinn.tp.dcis.model.Outcome;
import de.paladinsinn.tp.dcis.model.lists.BasicList;
import io.quarkus.oidc.token.propagation.reactive.AccessTokenRequestReactiveFilter;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.UUID;

@RegisterRestClient(configKey = "dispatch-api")
@RegisterProvider(value = AccessTokenRequestReactiveFilter.class, priority = 5000)
@Path("/api/v1/dispatches")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface DispatchClient {
    @POST
    @Path("/")
    Dispatch create(final Dispatch data);

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

    @GET
    @Path("/{id}")
    Dispatch retrieve(@PathParam("id") UUID id);


    @GET
    @Path("/gm/{id}")
    BasicList retrieveByGM(@PathParam("id") final String gm, @QueryParam("start") final int start, @QueryParam("size") final int size);

    @GET
    @Path("/operative/{id}")
    BasicList retrieveByOperative(@PathParam("id") final String gm, @QueryParam("start") final int start, @QueryParam("size") final int size);

    @PUT
    @Path("/{mission}/operative/{operative}")
    void addOperative(
            @PathParam("mission") final UUID mission,
            @PathParam("operative") final UUID operative,
            Outcome outcome
    );

    @POST
    @Path("/{mission}/operative/{operative}")
    void updateOperative(
            @PathParam("mission") final UUID mission,
            @PathParam("operative") final UUID operative,
            Outcome outcome
    );


    @DELETE
    @Path("/{mission}/operative/{operative}")
    void removeOperative(
            @PathParam("mission") final UUID mission,
            @PathParam("operative") final UUID operative
    );

    @PUT
    @Path("/{id}")
    Dispatch update(@PathParam("id") final UUID id, final Dispatch data);


    @DELETE
    @Path("/{id}")
    void delete(@PathParam("id") final UUID id);
}
