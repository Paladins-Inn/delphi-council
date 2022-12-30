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

import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.quarkus.annotation.UIScoped;
import de.paladinsinn.torganized.core.missions.Mission;
import de.paladinsinn.tp.dcis.ui.components.mvp.BasicViewImpl;
import de.paladinsinn.tp.dcis.ui.components.notifications.ErrorNotification;
import de.paladinsinn.tp.dcis.ui.views.MainLayout;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.UUID;


/**
 * MissionView -- Displays data of a single mission.
 *
 * @author klenkes {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-12-29
 */
@Route(value = "/mission", layout = MainLayout.class)
@UIScoped
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@Slf4j
public class MissionView extends BasicViewImpl<Mission> implements HasUrlParameter<UUID> {
    private final MissionPresenter presenter;

    @PostConstruct
    public void initPresenter() {
        presenter.setView(this);
    }


    @Override
    protected void updateView() {
        // FIXME 2022-12-30 rlichti This method needs to be implemented.
    }

    @Override
    protected void readForm() {
        // FIXME 2022-12-30 rlichti This method needs to be implemented.
    }

    @Override
    public void setParameter(final BeforeEvent event, final UUID id) {
        if (id == null) {
            getUI().ifPresentOrElse(
                    ui -> ui.getPage().setLocation("/missions"),
                    () -> {
                        log.error("View has no UI to do redirect. view={}", this);

                        ErrorNotification.showMarkdown("This view needs an **ID** to display the data. Due to technical reasons the redirect to _/missions_ failed.");
                    }
            );

            return;
        }

        presenter.loadId(id);
    }
}
