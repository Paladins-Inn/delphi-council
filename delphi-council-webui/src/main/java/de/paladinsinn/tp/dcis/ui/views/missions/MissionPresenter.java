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

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import de.paladinsinn.torganized.core.missions.Mission;
import de.paladinsinn.tp.dcis.client.missions.MissionClient;
import de.paladinsinn.tp.dcis.ui.components.mvp.BasicPresenterImpl;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.UUID;

/**
 * MissionPresenter -- Managed the view for a single Mission.
 *
 * @author klenkes {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-12-29
 */
@Dependent
@Data
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Slf4j
public class MissionPresenter extends BasicPresenterImpl<Mission, MissionView> {
    @SuppressWarnings("LSPLocalInspectionTool")
    @Inject
    @RestClient
    MissionClient client;

    @ToString.Include
    @EqualsAndHashCode.Include
    private MissionDataForm form;
    private MissionDataSaveListener saveListener;
    private MissionDataDeleteListener deleteListener;
    private MissionDataCloseListener closeListener;

    @PostConstruct
    public void init() {
        saveListener = new MissionDataSaveListener();
        deleteListener = new MissionDataDeleteListener();
        closeListener = new MissionDataCloseListener();
    }

    @Override
    public void loadId(UUID id) {
        Mission data = client.retrieve(id);

        if (data == null) {
            log.error("mission with ID '{}' not found!", id);
            return;
        }

        setData(data);
    }

    public void setForm(MissionDataForm form) {
        if (this.form != null && !this.form.equals(form)) {
            log.error("Can't unregister old form listeners. form.old={}, form.new={}", this.form, form);
        }

        this.form = form;

        registerForm(form);
    }

    private void registerForm(MissionDataForm form) {
        form.addListener(MissionDataForm.SaveEvent.class, saveListener);
        form.addListener(MissionDataForm.DeleteEvent.class, deleteListener);
        form.addListener(MissionDataForm.CloseEvent.class, closeListener);
    }

    public class MissionDataSaveListener implements ComponentEventListener<MissionDataForm.SaveEvent> {
        @Override
        public void onComponentEvent(final MissionDataForm.SaveEvent event) {
            // FIXME 2023-01-05 klenkes Implement this method
            log.trace(
                    "mission data save event. form={}, event={}, data.new={}, data.old={}",
                    form, event, form.getData(), getData()
            );
        }
    }

    public class MissionDataDeleteListener implements ComponentEventListener<MissionDataForm.DeleteEvent> {
        @Override
        public void onComponentEvent(final MissionDataForm.DeleteEvent event) {
            // FIXME 2023-01-05 klenkes Implement this method
            log.trace(
                    "mission data delete event. form={}, event={}, data.new={}, data.old={}",
                    form, event, form.getData(), getData()
            );
        }
    }

    public class MissionDataCloseListener implements ComponentEventListener<MissionDataForm.CloseEvent> {
        @Override
        public void onComponentEvent(final MissionDataForm.CloseEvent event) {
            // TODO 2023-01-05 klenkes Implement check to save modified data.

            UI.getCurrent().getPage().setLocation("/missions");

            log.trace("redirected to mission list.");
        }
    }
}
