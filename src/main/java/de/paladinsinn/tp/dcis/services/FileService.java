/*
 * Copyright (c) &today.year Kaiserpfalz EDV-Service, Roland T. Lichti
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.paladinsinn.tp.dcis.services;

import de.kaiserpfalzedv.commons.core.resources.Metadata;
import de.paladinsinn.tp.dcis.model.About;
import de.paladinsinn.tp.dcis.model.files.File;
import de.paladinsinn.tp.dcis.model.files.FileResource;
import io.quarkus.panache.common.Sort;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jboss.resteasy.annotations.cache.NoCache;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * FileService -- Save a file or retrieve it.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 2.0.0  2021-12-31
 * @since 2.0.0  2021-12-31
 */
@Slf4j
@ApplicationScoped
@Path("/api/v1/file")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FileService {
    @Inject
    FileRepository repository;

    @Inject
    SecurityIdentity identity;

    @PostConstruct
    public void init() {
        log.info("Started file service.");
    }

    @PreDestroy
    public void close() {
        log.info("Closing file service ...");
    }

    @Schema(
            description = "Index of all files."
    )
    @GET
    @Path("/")
    @Authenticated
    @NoCache
    public List<FileResource> index(
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
            @QueryParam("direction") final String direction,
            @QueryParam("sort") List<String> sort
    ) {
        log.info(
                "List files. namespace='{}', mediaType='{}', limit={}, offset={}, direction='{}', sort[]={}",
                nameSpace, mediaType, limit, offset, direction, sort
        );

        String owned = identity.getPrincipal().getName();
        Sort order = calculateSort(sort, direction);

        Stream<File> data = repository.streamAll(order).filter(d -> d.getOwner().equalsIgnoreCase(owned));

        if (owner != null) {
            data = data.filter(d -> d.getOwner().equals(owner));
        }

        log.debug("Found result set. count={}", data.count());

        return data.map(d -> FileResource.builder()
                        .withKind(FileResource.KIND)
                        .withApiVersion(FileResource.VERSION)

                        .withUid(d.getId())
                        .withNamespace(d.getNameSpace())
                        .withName(d.getName())
                        .withGeneration((long) d.getVersion())

                        .withMetadata(
                                Metadata.builder()
                                        .withCreated(d.getCreated())
                                        .build()
                        )
                        .withSpec(d.getFile())
                        .build()
                )
                .collect(Collectors.toList());
    }


    Sort calculateSort(@NotNull List<String> columns, final String direction) {
        boolean ascending = "asc".equalsIgnoreCase(direction);

        if (columns == null || columns.isEmpty()) {
            log.trace("No columns to sort by given. Setting default column order (namespace, owner, name)");

            columns = List.of("nameSpace", "owner", "name");
        }

        log.debug("Setting sort order. direction='{}', columns={}", ascending, columns);
        return ascending
                ? Sort.ascending(columns.toArray(new String[0]))
                : Sort.descending(columns.toArray(new String[0]));

    }
}
