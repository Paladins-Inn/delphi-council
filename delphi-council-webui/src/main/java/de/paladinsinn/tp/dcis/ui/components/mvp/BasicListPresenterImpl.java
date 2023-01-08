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
    protected V view;
    protected FrontendUser user;

    @Override
    public void setData(final BasicList data) {
        if (view == null) {
            throw new IllegalStateException("No view set for presenter. Can't set data yet. presenter=" + this);
        }

        view.setData(data);
    }

    @Override
    public BasicList getData() {
        if (view == null) {
            throw new IllegalStateException("No view set for presenter. Can't retrieve data yet. presenter=" + this);
        }

        return view.getData();
    }


    @Override
    public void setView(V view) {
        log.debug("Presenter recieved view. presenter={}, view={}", this, view);
        this.view = view;

        view.setFrontendUser(user);

        if (view.getData() == null) {
            loadData();
        }
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
