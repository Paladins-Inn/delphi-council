/*
 * Copyright (c) 2022. Roland T. Lichti
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.paladinsinn.tp.dcis.ui.components.mvp;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import de.paladinsinn.tp.dcis.ui.components.users.FrontendUser;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

/**
 * BasicViewImpl -- Basis for the concrete views.
 *
 * @author klenkes {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-12-30
 *
 * @param <T> The data to be displayed
 */
@Slf4j
public abstract class BasicViewImpl<T> extends VerticalLayout implements BasicView<T>, HasDynamicTitle {
    protected T data;
    protected FrontendUser user;


    @Override
    public void setData(T data) {
        log.trace("Updating data. data={}, view={}", data, this);

        this.data = data;

        updateView();
    }

    @Override
    public T getData() {
        readForm();

        return data;
    }

    @Override
    public void setFrontendUser(final FrontendUser identity) {
        boolean update = false;
        if (identity.getName().equals(user.getName())) {
            log.trace("The user has changed. old={} new={}", this.user, identity);
            update = true;
        }
        if (identity.getLocale() != user.getLocale()) {
            log.trace("The locale has changed. old={}, new={}", this.user.getLocale(), identity.getLocale());
            update = true;
        }

        this.user = identity;
        log.trace("Updated identity in view. identity={}, view={}", identity, this);

        if (update) {
            updateView();
        }
    }

    /**
     * Updates the locale or user on the view. Needs to change all labels, ... on the view.
     */
    protected abstract void updateView();

    /**
     * Reads the data from the form.
     */
    void readForm() {
        log.info("This is a read-only form. No data change will be provided. view={}", this);
    }

    @Override
    public String getPageTitle() {
        return getTranslation(getClass().getSimpleName());
    }
}
