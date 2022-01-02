package de.paladinsinn.tp.dcis.services;

import de.kaiserpfalzedv.commons.core.resources.Metadata;
import de.paladinsinn.tp.dcis.configuration.Paging;
import de.paladinsinn.tp.dcis.model.About;
import de.paladinsinn.tp.dcis.model.files.File;
import de.paladinsinn.tp.dcis.model.files.FileData;
import de.paladinsinn.tp.dcis.model.files.FileResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.util.MimeTypeUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;

/**
 * FileService -- Save a file or retrieve it.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 2.0.0  2021-12-31
 * @since 2.0.0  2021-12-31
 */
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@Slf4j
@Path("/api/v1/files")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FileService {
    private final FileRepository repository;
    private final Paging paging;

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
                    example = MimeTypeUtils.IMAGE_GIF_VALUE
            )
            @QueryParam("mediaType") final String mediaType,
            @QueryParam("owner") final String owner,
            @QueryParam("limit") final int limit,
            @QueryParam("offset") final int offset,
            @QueryParam("direction") final String direction,
            @QueryParam("sort") final List<String> sort
    ) {
        log.info(
                "List files. namespace='{}', mediaType='{}', limit={}, offset={}, direction='{}', sort[]={}",
                nameSpace, mediaType, limit, offset, direction, sort
        );

        Page<File> data = repository.findAll(
                Example.of(File.builder()
                    .withOwner(owner)
                    .withNameSpace(nameSpace)
                    .withFile(FileData.builder()
                        .withMediaType(mediaType)
                        .build()
                    ).build()
                ),
                paging.calculatePage(limit, offset, direction, sort.toArray(new String[0]))
        );

        log.debug("Found entries. count={}", data.stream().count());

        return data.get()
                .map(d -> FileResource.builder()
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
}
