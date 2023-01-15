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

import de.paladinsinn.tp.dcis.model.client.DispatchReport;
import de.paladinsinn.tp.dcis.model.client.Operation;
import de.paladinsinn.tp.dcis.model.client.Outcome;
import de.paladinsinn.tp.dcis.model.lists.BasicList;
import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheResult;
import io.quarkus.oidc.token.propagation.reactive.AccessTokenRequestReactiveFilter;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.UUID;

@RegisterRestClient(configKey = "engagements-api")
@RegisterProvider(value = AccessTokenRequestReactiveFilter.class, priority = 5000)
@Path("/api/v1/engagements")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface EngagementClient {
    String CACHE_NAME = "dc-engagements";
    long CACHE_LOCK_TIMEOUT = 10L;


    /**
     * Creates a new engagment for a given dispatch.
     *
     * @param dispatch The dispatch to create the engagement for.
     * @param data The engagement data.
     * @return The data persisted.
     */
    @POST
    @Path("/{dispatch}")
    @CacheResult(cacheName = CACHE_NAME, lockTimeout = CACHE_LOCK_TIMEOUT)
    Operation create(
            @PathParam("dispatch") final UUID dispatch,
            final DispatchReport data
    );


    /**
     * Retrieves a page of data specified by the first element and the size of page.
     *
     * @param start the first element to retrieve.
     * @param size the size of the page to retrieve.
     * @return A list containing the paging data and the elements retrieved in standard format.
     */
    @GET
    @Path("/{dispatch}")
    @CacheResult(cacheName = CACHE_NAME, lockTimeout = CACHE_LOCK_TIMEOUT)
    BasicList retrieve(
            @PathParam("dispatch") final UUID dispatch,
            @QueryParam("start") final long start,
            @QueryParam("size") final long size
    );

    /**
     * Retrieves all elements of the type.
     * @return A list containing all elements of the store of this type.
     */
    @GET
    @Path("/{dispatch}")
    @CacheResult(cacheName = CACHE_NAME, lockTimeout = CACHE_LOCK_TIMEOUT)
    BasicList retrieve(@PathParam("dispatch") final UUID dispatch);

    @GET
    @Path("/{dispatch}/{engagement}")
    @CacheResult(cacheName = CACHE_NAME, lockTimeout = CACHE_LOCK_TIMEOUT)
    DispatchReport retrieve(@PathParam("dispatch") final UUID dispatch, @PathParam("engagement") final UUID engagement);


    @GET
    @Path("/{dispatch}/gm/{id}")
    @CacheResult(cacheName = CACHE_NAME, lockTimeout = CACHE_LOCK_TIMEOUT)
    BasicList retrieveByGM(
            @PathParam("dispatch") final UUID dispatch,
            @PathParam("id") final String gm,
            @QueryParam("start") final int start,
            @QueryParam("size") final int size
    );

    @GET
    @Path("/{dispatch}/operative/{id}")
    @CacheResult(cacheName = CACHE_NAME, lockTimeout = CACHE_LOCK_TIMEOUT)
    BasicList retrieveByOperative(
            @PathParam("dispatch") final UUID dispatch,
            @PathParam("id") final String operative,
            @QueryParam("start") final int start,
            @QueryParam("size") final int size
    );



    @PUT
    @Path("/{dispatch}/{engagement}/operative/{operative}")
    @CacheInvalidate(cacheName = CACHE_NAME)
    void addOperative(
            @PathParam("dispatch") final UUID dispatch,
            @PathParam("engagement") final UUID engagement,
            @PathParam("operative") final UUID operative,
            Outcome outcome
    );

    @POST
    @Path("/{dispatch}/{engagement}/operative/{operative}")
    @CacheInvalidate(cacheName = CACHE_NAME)
    void updateOperative(
            @PathParam("dispatch") final UUID dispatch,
            @PathParam("engagement") final UUID engagement,
            @PathParam("operative") final UUID operative,
            Outcome outcome
    );


    @DELETE
    @Path("/{dispatch}/{engagement}/operative/{operative}")
    @CacheInvalidate(cacheName = CACHE_NAME)
    void removeOperative(
            @PathParam("dispatch") final UUID dispatch,
            @PathParam("operative") final UUID operative
    );

    @PUT
    @Path("/{dispatch}/{engagement}")
    @CacheInvalidate(cacheName = CACHE_NAME)
    void update(
            @PathParam("dispatch") final UUID dispatch,
            @PathParam("engagement") final UUID engagement,
            final Operation data
    );


    @DELETE
    @Path("/{dispatch}/{engagement}")
    @CacheInvalidate(cacheName = CACHE_NAME)
    void delete(
            @PathParam("dispatch") final UUID dispatch,
            @PathParam("engagement") final UUID engagement
    );
}
