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

import de.paladinsinn.torganized.core.missions.SpecialMission;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import javax.annotation.security.RolesAllowed;
import java.util.UUID;

@RepositoryRestResource(path = "/api/v1/missions/special")
public interface SpecialMissionResource extends PagingAndSortingRepository<SpecialMission, UUID> {
    @Override
    @RolesAllowed({"gm","orga","judge","admin"})
    <S extends SpecialMission> S save(S data);

    @Override
    @RolesAllowed({"gm","orga","judge","admin"})
    <S extends SpecialMission> Iterable<S> saveAll(Iterable<S> data);

    @Override
    @RolesAllowed({"gm","orga","judge","admin"})
    void deleteById(UUID id);

    @Override
    @RolesAllowed({"gm","orga","judge","admin"})
    void delete(SpecialMission data);

    @Override
    @RolesAllowed({"gm","orga","judge","admin"})
    void deleteAll(Iterable<? extends SpecialMission> data);

    @Override
    @RolesAllowed({"gm","orga","judge","admin"})
    void deleteAll();
}
