/*
 * Copyright (c) 2022. Roland T. Lichti
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.paladinsinn.tp.dcis.client;

import de.paladinsinn.tp.dcis.model.components.Persisted;
import de.paladinsinn.tp.dcis.model.lists.BasicList;

import javax.ws.rs.*;
import java.util.UUID;

public interface StandardClient<T extends Persisted> {
    @POST
    @Path("/")
    T create(final T data);

    @GET
    @Path("/count")
    int count();

    @GET
    @Path("/")
    BasicList retrieve(@QueryParam("start") long start, @QueryParam("size") long size);

    @GET
    @Path("/")
    BasicList retrieve(@QueryParam("query") final String query);

    @GET
    @Path("/")
    BasicList retrieve(@QueryParam("query")final String query, @QueryParam("start") final int start, @QueryParam("size") final int size);

    @GET
    @Path("/{id}")
    T retrieve(@PathParam("id") UUID id);

    @PUT
    @Path("/{id}")
    T update(@PathParam("id") final UUID id, final T data);


    @DELETE
    @Path("/{id}")
    void delete(@PathParam("id") final UUID id);
}
