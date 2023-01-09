/*
 * Copyright (c) 2022-2023. Roland T. Lichti
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.paladinsinn.tp.dcis.missions;

import de.paladinsinn.tp.dcis.model.jpa.Dispatch;
import de.paladinsinn.tp.dcis.model.lists.BasicData;
import de.paladinsinn.tp.dcis.model.lists.BasicList;
import de.paladinsinn.tp.dcis.model.meta.Paging;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.annotation.security.PermitAll;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Collectors;

@Schema(
        name = "Dispatches",
        description = "Dispatch Management Service"
)
@ApplicationScoped
@Path("/api/v1/dispatches")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@Slf4j
public class DispatchResource {
    private static final int DEFAULT_INT_PARAM = -1;
    private static final String DEFAULT_INT_PARAM_STRING = "-1";

    private final DispatchRepository repository;

    @Schema(
            title = "Retrieve all missions",
            description = "Retrieves all missions (or a subpage if requested)."
    )
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Returns a list of missions"),
            @APIResponse(responseCode = "403", description = "Not logged in. Please log in")
    })
    @GET
    public BasicList index(
            @Schema(description = "Start index of the list in total set of missions.",
                    nullable = true)
            @QueryParam("start")
            @DefaultValue(DEFAULT_INT_PARAM_STRING)
            int start,

            @Schema(description = "Size of page to request.", nullable = true)
            @QueryParam("size")
            @DefaultValue(DEFAULT_INT_PARAM_STRING)
            int size
    ) {
        if (start == DEFAULT_INT_PARAM && size == DEFAULT_INT_PARAM) {
            return index();
        }

        Page<Dispatch> raw = repository.findAll(PageRequest.of(start * size, size));
        log.info("Retrieved list of missions. total={}, count={}", raw.getTotalElements(), raw.getNumber());

        log.debug("missions loaded. count={}, total={}, data.size={}",
                raw.getNumber(), raw.getTotalElements(), raw.getSize());


        return BasicList.builder()
                .kind("mission")
                .page(Paging.builder()
                        .start(start)
                        .count(raw.getNumber())
                        .size(size)
                        .total(raw.getTotalElements())
                        .build())
                .data(raw.get().map(this::mapMission).collect(Collectors.toList()))
                .build();
    }


    private BasicList index() {
        Iterator<Dispatch> raw = repository.findAll().iterator();

        ArrayList<BasicData> data = new ArrayList<>();
        while (raw.hasNext()) {
            data.add(mapMission(raw.next()));
        }


        log.debug("missions loaded. count={}, total={}, data.size={}",
                data.size(), data.size(), data.size());

        return BasicList.builder()
                .kind("mission")
                .page(Paging.builder()
                        .start(0)
                        .count(data.size())
                        .total(data.size())
                        .build())
                .data(data)
                .build();
    }

    private BasicData mapMission(Dispatch orig) {
        BasicData result = BasicData.builder()
                .id(orig.getId())
                .created(orig.getCreated())
                .modified(orig.getModified())

                .code(orig.getCode())
                .name(orig.getName())
                .description(orig.getDescription())
                .url(orig.getImage())

                .build();

        log.trace("Dataset converted. result={}", result);
        return result;
    }
}
