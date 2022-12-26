/*
 * Copyright (c) 2022. Roland T. Lichti
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.paladinsinn.tp.dcis.operatives;

import de.paladinsinn.torganized.core.operative.OperativeSpecialReport;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import javax.annotation.security.RolesAllowed;
import java.util.UUID;

@RepositoryRestResource(path = "/api/v1/operatives/specialreports")
public interface OperativeSpecialReportResource extends PagingAndSortingRepository<OperativeSpecialReport, UUID> {
    @Override
    @RolesAllowed({"gm","orga","judge","admin"})
    <S extends OperativeSpecialReport> S save(S data);

    @Override
    @RolesAllowed({"gm","orga","judge","admin"})
    <S extends OperativeSpecialReport> Iterable<S> saveAll(Iterable<S> data);

    @Override
    @RolesAllowed({"gm","orga","judge","admin"})
    void deleteById(UUID id);

    @Override
    @RolesAllowed({"gm","orga","judge","admin"})
    void delete(OperativeSpecialReport data);

    @Override
    @RolesAllowed({"gm","orga","judge","admin"})
    void deleteAll(Iterable<? extends OperativeSpecialReport> data);

    @Override
    @RolesAllowed({"gm","orga","judge","admin"})
    void deleteAll();
}
