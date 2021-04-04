/*
 * Copyright (c) 2021 Kaiserpfalz EDV-Service, Roland T. Lichti
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

package de.paladinsinn.delphicouncil.app.events;

import com.vaadin.flow.component.ComponentEvent;
import de.paladinsinn.delphicouncil.data.missions.Mission;
import de.paladinsinn.delphicouncil.ui.forms.missions.MissionForm;

/**
 * MissionSaveEvent --
 *
 * @author klenkes74 {literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-04-04
 */
public class MissionSaveEvent extends ComponentEvent<MissionForm> {
    /**
     * Creates a new event using the given source and indicator whether the
     * event originated from the client side or the server side.
     *
     * @param source     the source component
     * @param fromClient <code>true</code> if the event originated from the client
     */
    public MissionSaveEvent(MissionForm source, boolean fromClient) {
        super(source, fromClient);
    }

    /**
     * @return the mission report to be saved.
     */
    public Mission getMission() {
        return getSource().getMission().orElseThrow();
    }
}
