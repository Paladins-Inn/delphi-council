/*
 * Copyright (c) 2022-2023. Roland T. Lichti
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.paladinsinn.tp.dcis.ui.views.dispatchlist;

import de.paladinsinn.tp.dcis.client.DispatchClient;
import de.paladinsinn.tp.dcis.model.lists.BasicList;
import de.paladinsinn.tp.dcis.ui.components.mvp.BasicListPresenterImpl;
import de.paladinsinn.tp.dcis.ui.components.notifications.ErrorNotification;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class DispatchListPresenter extends BasicListPresenterImpl<DispatchListView> {
    @Inject
    @RestClient
    DispatchClient client;

    @Override
    public void loadData() throws UnsupportedOperationException {
        try {
            BasicList data = client.retrieve();
            log.trace("Retrieved mission data. data={}", data);

            setData(data);
        } catch (RuntimeException e) {
            log.error("Could not load data: " + e.getMessage(), e);
            ErrorNotification.show(getView().getTranslation("error.mainfraim_unavailable"));
        }
    }
}
