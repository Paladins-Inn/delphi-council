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

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.quarkus.annotation.UIScoped;
import de.paladinsinn.torganized.core.missions.Mission;
import de.paladinsinn.tp.dcis.ui.components.Tile;
import de.paladinsinn.tp.dcis.ui.components.mvp.BasicViewImpl;
import de.paladinsinn.tp.dcis.ui.views.MainLayout;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;


/**
 * MissionListView -- Displays lists of missions.
 *
 * @author klenkes {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-12-29
 */
@Route(value = "/missions", layout = MainLayout.class)
@UIScoped
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class MissionListView extends BasicViewImpl<List<Mission>> {
    private final MissionListPresenter presenter;

    @Builder.Default
    private final ArrayList<Tile<Mission>> missions = new ArrayList<>();

    @PostConstruct
    public void initPresenter() {
        presenter.setView(this);
        presenter.loadData();
    }


    @Override
    protected void updateView() {

    }
}
