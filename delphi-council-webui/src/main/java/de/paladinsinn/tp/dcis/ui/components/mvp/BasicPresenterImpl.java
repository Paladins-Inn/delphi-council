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

import de.paladinsinn.tp.dcis.ui.components.users.FrontendUser;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.UUID;

@Slf4j
public abstract class BasicPresenterImpl<T, V extends BasicView<T>> implements BasicPresenter<T, V> {
    protected T data;
    protected V view;
    protected FrontendUser user;

    @Override
    public void setData(T data) {
        this.data = data;
        view.setData(data);

        log.trace("Data in view changed. view={}, data={}", view, data);
    }

    @Override
    public T getData() {
        this.data = view.getData();

        log.trace("Data loaded from view. view={}, data={}", view, data);
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
        }

        log.trace("Added view to presenter. view={}, presenter={}", view, this);
    }

    @Inject
    @Override
    public void setFrontendUser(FrontendUser identity) {
        this.user = identity;
        view.setFrontendUser(identity);

        log.trace("Changed identity in presenter. user={}, view={}, presenter={}", user, view, this);
    }


    @Override
    public void loadData() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }


    @Override
    public void loadId(UUID id) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("List presenters don't load by id");
    }
}
