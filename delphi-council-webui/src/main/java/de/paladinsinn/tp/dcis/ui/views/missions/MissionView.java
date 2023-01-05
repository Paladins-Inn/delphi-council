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

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.quarkus.annotation.UIScoped;
import de.paladinsinn.torganized.core.missions.Mission;
import de.paladinsinn.tp.dcis.ui.components.mvp.BasicViewImpl;
import de.paladinsinn.tp.dcis.ui.components.users.Roles;
import de.paladinsinn.tp.dcis.ui.views.MainLayout;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;


/**
 * MissionView -- Displays data of a single mission.
 *
 * @author klenkes {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-12-29
 */
@Route(value = "/mission", layout = MainLayout.class)
@UIScoped
@Slf4j
public class MissionView extends BasicViewImpl<Mission> implements HasUrlParameter<UUID> {

    public MissionView(final MissionPresenter presenter, final MissionDataForm form) {
        super(presenter, form);
    }

    @Override
    protected void updateView() {
        remove(form);

        form.setData(data);

        add(form);
        // FIXME 2022-12-30 rlichti This method needs to be implemented.
    }

    @Override
    public void setParameter(final BeforeEvent event, @OptionalParameter UUID id) {
        if (id == null) {
            redirectForNewMissionInput();
        } else {
            presenter.loadId(id);
        }
    }

    private void redirectForNewMissionInput() {
        if (user.isInRole(Roles.orga)) {
            createNewDispatch();
        } else {
            createNewSpecialMission();
        }
    }

    private void createNewDispatch() {
        log.info("Open form for new mission. opening empty view.");
        presenter.setData(Mission.builder()
                .id(UUID.randomUUID())
                .build()
        );
    }

    private void createNewSpecialMission() {
        if (user.isInRole(Roles.gm)) {
            log.info("User is GM. Redirecting to Special Mission input form. user='{}', roles={}",
                    user.getName(), user.getRoles());
            UI.getCurrent().getPage().setLocation("/specialmission");
        } else {
            log.warn("User is not member of the orga and is no gm. Can't create a new mession. user='{}', roles={}",
                    user.getName(), user.getRoles());
            UI.getCurrent().getPage().setLocation("/missions");
        }
    }

    @Override
    public String getPageTitle() {
        if (getData() != null) {
            return getTranslation("mission.editor.caption", getData().getShortName(), form.getTabTitle());
        } else {
            return getTranslation("mission.editor.caption", getTranslation("mission.unregistered"),
                    form.getTabTitle());
        }
    }
}
