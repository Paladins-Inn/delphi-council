/*
 * Copyright (c) 2023. Roland T. Lichti
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.paladinsinn.tp.dcis.model;

import de.kaiserpfalzedv.commons.core.resources.HasName;
import de.kaiserpfalzedv.commons.core.resources.Persisted;
import de.paladinsinn.tp.dcis.common.HasLanguage;
import de.paladinsinn.tp.dcis.model.components.HasCode;
import de.paladinsinn.tp.dcis.model.components.HasDescription;
import de.paladinsinn.tp.dcis.model.components.HasDisplayNames;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * <p>Dispatch -- Dispatches are the engagements of Storm Knights in the possibility wars.</p>
 *
 * <p>A dispatch is either a {@link Mission} or an {@link Operation} of the Delphi Council.</p>
 *
 * <p>Operations are the torganized games which drive the shared campaign and the development of the game world. The
 * operations are pre-defined and the GM registers with the standardized success definitions. So their outcome influence
 * the shared world of the Torganized Play world.</p>
 *
 * <p>Missions are games with torganized play characters on private tables where the GM of the game register the
 * development of the Storm Knights so they can take part in torganized play events. Normally the outcome of missions
 * don't influence the shared world.</p>
 *
 * @author klenkes {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2023-01-15
 */
public interface Dispatch
        extends HasName, HasCode, HasDescription, HasDisplayNames,
        HasLanguage, Persisted {

    @Schema(description = "Payment for every storm knight taking this mission.")
    int getPayment();

    @Schema(description = "XP for every storm knight taking this mission.")
    int getXp();

    String getPublication();

    String getImage();

}
