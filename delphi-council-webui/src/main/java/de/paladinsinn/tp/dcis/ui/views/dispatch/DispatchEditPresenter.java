/*
 * Copyright (c) 2022-2023. Roland T. Lichti
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.paladinsinn.tp.dcis.ui.views.dispatch;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.quarkus.annotation.UIScoped;
import de.paladinsinn.tp.dcis.client.DispatchClient;
import de.paladinsinn.tp.dcis.model.client.Operation;
import de.paladinsinn.tp.dcis.ui.components.mvp.BasicPresenterImpl;
import de.paladinsinn.tp.dcis.ui.components.notifications.ErrorNotification;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.inject.Inject;
import java.util.UUID;

/**
 * MissionPresenter -- Managed the view for a single Mission.
 *
 * @author klenkes {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-12-29
 */
@UIScoped
@Getter
@Setter
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Slf4j
public class DispatchEditPresenter extends BasicPresenterImpl<Operation> {
    @Inject
    @RestClient
    @SuppressWarnings("LSPLocalInspectionTool")
    DispatchClient client;


    @Override
    public void loadId(UUID id) {
        Operation data = client.retrieve(id);

        if (data == null) {
            log.error("dispatch with ID '{}' not found!", id);
            return;
        }

        setData(data);
    }

    @Override
    public void save() {
        if (data == null) {
            ErrorNotification.show(getView().getTranslation("input.data.saved.failed", "(null)", "(null)", "No data given!"));
            return;
        }

        if (data.getCode() != null) {
            data = form.getData();

            log.trace("Updating dispatch. presenter={}, data={}", this, data);
            client.update(data.getId(), data);
        } else {
            log.trace("Creating new dispatch. presenter={}, data={}", this, data);
            data = form.getData();
            client.create(data);
        }
        log.info("Data saved. mission='{}', code='{}'", data.getId(), data.getCode());
        Notification.show(getView().getTranslation("input.data.saved.success", data.getName(), data.getCode()));

        returnToMissionsList();
    }

    @Override
    public void delete() {
        data = form.getData();

        if (data != null) {
            // TODO 2023-01-05 klenkes Implement a configuration rquestor for deleting the existing data.

            log.info("Deleting mission data. mission='{}', code='{}'", data.getId(), data.getCode());

            client.delete(data.getId());
            log.info("Deleted mission data. mission='{}', code='{}'", data.getId(), data.getCode());
            Notification.show(getView().getTranslation("input.data.deleted.success", data.getName(), data.getCode()));
        }

        returnToMissionsList();
    }

    @Override
    public void close() {
        log.info("Closing mission editor without saving the data.");
        // TODO 2023-01-05 klenkes Implement a confirmation requestor for loosing changes (if there are changes)
        returnToMissionsList();
    }

    private void returnToMissionsList() {
        ((Component)view).getUI().ifPresentOrElse(
                ui -> ui.navigate("/missions"),
                () -> UI.getCurrent().navigate("/missions")
        );
    }
}
