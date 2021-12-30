/*
 * Copyright (c) 2021 Kaiserpfalz EDV-Service, Roland T. Lichti
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package de.paladinsinn.tp.dcis.model.operativereports;

import io.quarkus.hibernate.orm.rest.data.panache.PanacheRepositoryResource;

import java.util.UUID;

/**
 * @author rlichti
 * @version 1.0.0 2021-09-08
 * @since 1.0.0 2021-09-08
 */
public interface OperativeReportResource extends PanacheRepositoryResource<OperativeReportRepository, OperativeReport, UUID> {
}