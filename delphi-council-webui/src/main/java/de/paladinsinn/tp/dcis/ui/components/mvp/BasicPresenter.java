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

import java.util.UUID;

/**
 * Basic Presenter -- Common API to every Presenter.
 *
 * @author klenkes {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-12-29
 */
public interface BasicPresenter<T, V extends BasicView> {
    /**
     * Inserts the data for the view.
     *
     * @param data The data to be presented
     */
    public void setData(final T data);

    /**
     * Retrieves the data from the view.
     *
     * @return The data from the view.
     */
    public T getData();

    /**
     * Resets the data in the view.
     */
    public void resetData();

    /**
     * Inserts the view this presenter works on.
     *
     * @param view The basic view of this presenter.
     */
    public void setView(final V view);

    /**
     * Updates the logged in user.
     *
     * @param identity
     */
    public void setFrontendUser(final FrontendUser identity);

    public void loadData() throws UnsupportedOperationException;

    public void loadId(final UUID id) throws UnsupportedOperationException;
}
