/*
 * Copyright (c) 2022. Roland T. Lichti
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.paladinsinn.tp.dcis.client.operatives;

import de.paladinsinn.torganized.core.operative.Operative;
import de.paladinsinn.tp.dcis.client.StandardClient;
import io.quarkus.oidc.client.filter.OidcClientRequestFilter;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.Path;
import java.util.UUID;

@RegisterRestClient(configKey = "operative-api")
@RegisterProvider(OidcClientRequestFilter.class)
@Path("/api/v1/operatives")
public interface OperativeClient extends StandardClient<Operative, UUID> {}
