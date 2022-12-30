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

import de.paladinsinn.torganized.core.missions.Mission;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import javax.annotation.security.RolesAllowed;
import java.util.UUID;

@Schema(
        name = "Missions",
        description = "Mission Data CRUD Service"
)
@RepositoryRestResource(path = "/api/v1/missions")
public interface MissionResource extends PagingAndSortingRepository<Mission, UUID> {
    @Override
    @RolesAllowed({"orga","judge","admin"})
    <S extends Mission> S save(S data);

    @Override
    @RolesAllowed({"orga","judge","admin"})
    <S extends Mission> Iterable<S> saveAll(Iterable<S> data);

    @Override
    @RolesAllowed({"orga","judge","admin"})
    void deleteById(UUID id);

    @Override
    @RolesAllowed({"orga","judge","admin"})
    void delete(Mission data);

    @Override
    @RolesAllowed({"orga","judge","admin"})
    void deleteAll(Iterable<? extends Mission> data);

    @Override
    @RolesAllowed("admin")
    void deleteAll();
}
