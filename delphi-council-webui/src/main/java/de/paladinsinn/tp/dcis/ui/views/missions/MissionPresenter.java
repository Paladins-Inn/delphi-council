/*
 * Copyright (c) 2022. Roland T. Lichti
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.paladinsinn.tp.dcis.ui.views.missions;

import de.paladinsinn.torganized.core.missions.Mission;
import de.paladinsinn.tp.dcis.client.missions.MissionClient;
import de.paladinsinn.tp.dcis.ui.components.mvp.BasicPresenterImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

/**
 * MissionPresenter -- Managed the view for a single Mission.
 *
 * @author klenkes {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-12-29
 */
@Dependent
@RequiredArgsConstructor(onConstructor = @__({@Inject, @RestClient}))
@Slf4j
public class MissionPresenter extends BasicPresenterImpl<Mission, MissionView> {
    @SuppressWarnings("LSPLocalInspectionTool")
    private final MissionClient client;

    @Override
    public void loadId(UUID id) {
        List<Mission> data = client.retrieve("id=" + id.toString());

        if (data.size() != 1) {
            log.error("mission with ID '{}' not found!", id);
            return;
        }

        setData(data.get(0));
    }
}
