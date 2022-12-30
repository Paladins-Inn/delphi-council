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
import de.paladinsinn.tp.dcis.ui.components.mvp.BasicListPresenterImpl;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

/**
 * MissionListPresenter -- Managed the view for lists of Missions.
 *
 * @author klenkes {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-12-29
 */
@Dependent
public class MissionListPresenter extends BasicListPresenterImpl<Mission, MissionListView> {
    @Inject
    @RestClient
    MissionClient client;

    @Override
    public void loadData() throws UnsupportedOperationException {
        setData(client.retrieve());
    }
}
