/*
 * Copyright (c) 2022. Roland T. Lichti
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.paladinsinn.tp.dcis.missions;

import de.paladinsinn.tp.dcis.model.jpa.Mission;
import de.paladinsinn.tp.dcis.model.lists.BasicData;
import de.paladinsinn.tp.dcis.model.lists.BasicList;
import de.paladinsinn.tp.dcis.model.meta.Paging;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.annotation.security.PermitAll;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.stream.Collectors;

@Schema(
        name = "Missions",
        description = "Mission Data CRUD Service"
)
@ApplicationScoped
@Path("/api/v1/mission")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@Slf4j
public class MissionResource {
    private static final int DEFAULT_START = 0;
    private static final int DEFAULT_SIZE = 20;

    private final MissionRepository repository;

    @GET
    public BasicList index(
            @QueryParam("start") int start,
            @QueryParam("size") int size
    ) {
        start = start != 0 ? start : DEFAULT_START;
        size = size != 0 ? size : DEFAULT_SIZE;

        Page<Mission> raw = repository.findAll(PageRequest.of(start * size, size));

        log.debug("missions loaded. count={}, total={}, ", raw.getNumber(), raw.getTotalElements());


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

    private BasicData mapMission(Mission orig) {
        return BasicData.builder()
                .id(orig.getId())
                .created(orig.getCreated())
                .modified(orig.getModified())

                .code(orig.getCode())
                .name(orig.getName())
                .description(orig.getDescription())
                .url(orig.getImage())

                .build();
    }
}
