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

package de.paladinsinn.tp.dcis.ui.views.operativereports;

import com.sun.istack.NotNull;
import com.vaadin.flow.component.ComponentEvent;
import de.paladinsinn.tp.dcis.data.missions.MissionReport;
import de.paladinsinn.tp.dcis.data.operative.Operative;
import de.paladinsinn.tp.dcis.ui.components.TorgForm;
import de.paladinsinn.tp.dcis.ui.views.missionreports.MissionReportForm;
import lombok.Getter;

import java.util.StringJoiner;

/**
 * AddOperativeToMissionEvent -- Adds an {@link Operative} to a {@link MissionReport}.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-04-11
 */
@Getter
public class AddOperativeToMissionEvent extends ComponentEvent<TorgForm> {

    /**
     * The operative to be added.
     */
    private final Operative operative;

    /**
     * The mission to be changed.
     */
    private final MissionReport mission;

    /**
     * Creates a new event using the given source and indicator whether the
     * event originated from the client side or the server side.
     *
     * @param source    the source component
     * @param mission   the mission the operative is added to
     * @param operative the operative to add to the mission
     */
    public AddOperativeToMissionEvent(@NotNull final MissionReportForm source, @NotNull final MissionReport mission, @NotNull final Operative operative) {
        super(source, false);

        this.mission = mission;
        this.operative = operative;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", AddOperativeToMissionEvent.class.getSimpleName() + "[", "]")
                .add("operative=" + operative)
                .add("mission=" + mission)
                .toString();
    }
}
