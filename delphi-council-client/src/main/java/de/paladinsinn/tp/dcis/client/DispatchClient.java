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

import de.paladinsinn.tp.dcis.model.client.Dispatch;
import de.paladinsinn.tp.dcis.model.lists.BasicList;
import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheResult;
import io.quarkus.oidc.token.propagation.reactive.AccessTokenRequestReactiveFilter;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.UUID;

@RegisterRestClient(configKey = "dispatches-api")
@RegisterProvider(value = AccessTokenRequestReactiveFilter.class, priority = 5000)
@Path("/api/v1/dispatches")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface DispatchClient {
    String CACHE_NAME = "dc-dispatches";
    long CACHE_LOCK_TIMEOUT = 10L;


    @POST
    @Path("/")
    @CacheResult(cacheName = CACHE_NAME, lockTimeout = CACHE_LOCK_TIMEOUT)
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
    @CacheResult(cacheName = CACHE_NAME, lockTimeout = CACHE_LOCK_TIMEOUT)
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
    @CacheResult(cacheName = CACHE_NAME, lockTimeout = CACHE_LOCK_TIMEOUT)
    BasicList retrieve();

    @GET
    @Path("/gm/{id}")
    @CacheResult(cacheName = CACHE_NAME, lockTimeout = CACHE_LOCK_TIMEOUT)
    BasicList retrieveByGM(@PathParam("id") final String gm, @QueryParam("start") final int start, @QueryParam("size") final int size);

    @GET
    @Path("/operative/{id}")
    @CacheResult(cacheName = CACHE_NAME, lockTimeout = CACHE_LOCK_TIMEOUT)
    BasicList retrieveByOperative(@PathParam("id") final String gm, @QueryParam("start") final int start, @QueryParam("size") final int size);


    @GET
    @Path("/{id}")
    @CacheResult(cacheName = CACHE_NAME, lockTimeout = CACHE_LOCK_TIMEOUT)
    Dispatch retrieve(@PathParam("id") UUID id);


    @PUT
    @Path("/{id}")
    @CacheInvalidate(cacheName = CACHE_NAME)
    void update(@PathParam("id") final UUID id, final Dispatch data);


    @DELETE
    @Path("/{id}")
    @CacheInvalidate(cacheName = CACHE_NAME)
    void delete(@PathParam("id") final UUID id);
}
