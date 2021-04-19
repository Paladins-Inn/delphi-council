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

package de.paladinsinn.tp.dcis.ui.views.operativespecialreports;

import com.sun.istack.NotNull;
import com.vaadin.flow.component.ComponentEvent;
import de.paladinsinn.tp.dcis.data.missions.MissionReport;
import de.paladinsinn.tp.dcis.data.operative.Operative;
import de.paladinsinn.tp.dcis.data.operative.OperativeSpecialReport;
import de.paladinsinn.tp.dcis.ui.components.TorgForm;
import lombok.Getter;

import java.util.StringJoiner;

/**
 * RemoveOperativeFromMissionEvent -- Removes an {@link Operative} from a {@link MissionReport}.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-04-11
 */
@Getter
public class RemoveOperativeFromSpecialMissionEvent extends ComponentEvent<TorgForm> {

    /**
     * The operative to be removed.
     */
    private final OperativeSpecialReport operativeReport;

    /**
     * Creates a new event using the given source and indicator whether the
     * event originated from the client side or the server side.
     *
     * @param source the source component
     * @param report the mission the operative is removed from
     */
    public RemoveOperativeFromSpecialMissionEvent(@NotNull final TorgForm source, @NotNull final OperativeSpecialReport report) {
        super(source, false);

        this.operativeReport = report;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", RemoveOperativeFromSpecialMissionEvent.class.getSimpleName() + "[", "]")
                .add("report=" + operativeReport)
                .toString();
    }
}
