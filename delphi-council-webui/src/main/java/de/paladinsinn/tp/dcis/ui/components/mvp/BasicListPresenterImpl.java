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

import de.paladinsinn.tp.dcis.model.lists.BasicList;
import de.paladinsinn.tp.dcis.ui.components.users.FrontendUser;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@Slf4j
public abstract class BasicListPresenterImpl<V extends BasicListView> implements BasicListPresenter<V> {
    protected BasicList data;
    protected V view;
    protected FrontendUser user;

    @Override
    public void setData(final BasicList data) {
        if (data != null) {
            this.data = data;
            log.trace("data in presenter changed. data.count={}", data.getPage().getCount());
        } else {
            this.data = null;
            log.info("No data set in presenter.");
        }

        if (view != null) {
            view.setData(this.data);
        }
    }

    @Override
    public BasicList getData() {
        this.data = view.getData();

        return data;
    }

    @Override
    public void resetData() {
        view.setData(data);

        log.trace("Data reset in view. view={}, data={}", view, data);
    }

    @Override
    public void setView(V view) {
        this.view = view;

        if (data != null) {
            view.setData(data);

            log.trace("Added data to view. view={}, presenter={}, data={}", view, this, data);
        }

        if (user != null) {
            view.setFrontendUser(user);

            log.trace("Added user to view. view={}, presenter={}, user={}", view, this, user);
        }

        log.debug("Configured view. view={}, presenter={}", view, this);
    }

    @Override
    public V getView() {
        return view;
    }

    @Inject
    @Override
    public void setFrontendUser(@SuppressWarnings("CdiInjectionPointsInspection") FrontendUser identity) {
        this.user = identity;

        if (view != null) {
            view.setFrontendUser(identity);
        }

        log.trace("Changed identity in presenter. user={}, view={}, presenter={}", user, view, this);
    }
}
