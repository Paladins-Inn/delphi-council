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

import javax.ws.rs.*;
import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface StandardClient<T extends Serializable, ID extends Serializable> {
    @POST
    @Path("/")
    T create(final T data);

    @GET
    @Path("/count")
    int count();

    @GET
    @Path("/")
    List<T> retrieve();

    @GET
    @Path("/")
    List<T> retrieve(final String query);

    @GET
    @Path("/")
    List<T> retrieve(final String query, final int page, final int size);

    @GET
    @Path("/")
    List<T> retrieve(final String query, final int page, final int size, final Set<String> sort);


    @PUT
    @Path("/{id}")
    T update(@PathParam("id") final ID id, final T data);


    @DELETE
    @Path("/{id}")
    void delete(@PathParam("id") final ID id);
}
