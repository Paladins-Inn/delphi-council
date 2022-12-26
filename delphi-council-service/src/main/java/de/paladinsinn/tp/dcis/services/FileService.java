/*
 * Copyright (c) 2022. Roland T. Lichti
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.paladinsinn.tp.dcis.services;

import de.kaiserpfalzedv.commons.core.files.File;
import de.paladinsinn.torganized.core.About;
import io.quarkus.security.Authenticated;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@ApplicationScoped
@Path("/api/v1/files")
@Authenticated
@Consumes("application/json")
@Produces("application/json")
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@Slf4j
public class FileService {
    private de.kaiserpfalzedv.commons.fileserver.services.FileService service;

//    @Inject
//    JsonWebToken jwt;

    @Schema(
                         description = "Index of all files."
                 )
     @GET
     public List<File> index(
             @Context SecurityContext securityContext,
             @Schema(
                                 description = "Namespace to search for.",
                                 defaultValue = About.NAMESPACE,
                                 name = "nameSpace",
                                 example = "us",
                                 maxLength = 50
                         )
             @QueryParam("namespace") final String nameSpace,
             @Schema(
                                 description = "MediaType to look for.",
                                 name = "mediaType",
                                 example = "image/png"
                         )
            @QueryParam("mediaType") final String mediaType,
            @QueryParam("owner") final String owner,
            @QueryParam("limit") final int limit,
            @QueryParam("offset") final int offset,
            @QueryParam("sort") final List<String> sort
    ) {
        log.debug("Loading files. jwt={}, securityContext={}", null, securityContext);
//        Optional<Set<String>> roles = jwt != null ? jwt.claim("roles") : Optional.empty();
        Optional<Set<String>> roles = Optional.empty();

        return service.index(nameSpace, mediaType, owner, limit, offset, sort, null, roles.orElse(Collections.emptySet()));
    }

}
