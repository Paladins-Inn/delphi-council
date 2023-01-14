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

import com.vaadin.flow.router.*;
import com.vaadin.quarkus.annotation.UIScoped;
import de.paladinsinn.tp.dcis.model.client.Dispatch;
import de.paladinsinn.tp.dcis.ui.components.mvp.BasicViewImpl;
import de.paladinsinn.tp.dcis.ui.views.MainLayout;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.security.RolesAllowed;
import java.util.UUID;


/**
 * DispatchView -- Displays data of a single mission.
 *
 * @author klenkes {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-12-29
 */
@Route(value = "/dispatch/:id?", layout = MainLayout.class)
@UIScoped
@Slf4j
@RolesAllowed({"orga", "judge", "admin"})
public class DispatchEditView extends BasicViewImpl<Dispatch> implements HasUrlParameter<UUID>, BeforeEnterObserver {

    public DispatchEditView(final DispatchEditPresenter presenter, final DispatchEditDataForm form) {
        super(presenter, form);
    }

    @Override
    protected void updateView() {
        remove(form);
        form.setData(presenter.getData());
        add(form);
    }


    @Override
    public void beforeEnter(final BeforeEnterEvent beforeEnterEvent) {
        beforeEnterEvent.getRouteParameters().get("id").ifPresentOrElse(
                id -> {
                    try {
                        presenter.loadId(UUID.fromString(id));
                    } catch (IllegalArgumentException e) {
                        log.error("The id is not valid. '" + id + "' is no valid UUID.");
                    }
                },
                this::createNewDispatch
        );
    }

    @Override
    public void setParameter(final BeforeEvent event, @OptionalParameter UUID id) {
        if (id != null) {
            presenter.loadId(id);
        } else {
            createNewDispatch();
        }
    }

    private void createNewDispatch() {
        log.info("Open form for new dispatch. Opening empty view.");
        presenter.setData(de.paladinsinn.tp.dcis.model.client.Dispatch.builder().build()
        );
    }

    @Override
    public String getPageTitle() {
        if (getData() != null) {
            return getTranslation("dispatch.editor.caption", getData().getShortName(), form.getTabTitle());
        } else {
            return getTranslation("dispatch.editor.caption", getTranslation("dispatch.unregistered"),
                    form.getTabTitle());
        }
    }
}
