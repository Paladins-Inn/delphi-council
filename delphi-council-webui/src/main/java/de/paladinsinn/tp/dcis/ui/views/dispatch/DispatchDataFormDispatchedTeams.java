/*
 * Copyright (c) 2023. Roland T. Lichti
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.paladinsinn.tp.dcis.ui.views.dispatch;

import de.paladinsinn.tp.dcis.model.Dispatch;
import de.paladinsinn.tp.dcis.ui.components.mvp.BasicTab;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

/**
 * MissionDataFormBaseData -- The basic data to any mission.
 *
 * @author klenkes {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2023-01-05
 */
@Dependent
@ToString(onlyExplicitlyIncluded = true)
@Setter
@Getter
@Slf4j
public class DispatchDataFormDispatchedTeams extends BasicTab<Dispatch> {
    private static final String I18N_KEY = "mission.form.tab.dispatches";


    @Inject
    public DispatchDataFormDispatchedTeams(DispatchPresenter presenter) {
        super(presenter);
    }


    @Override
    public String getI18nKey() {
        return I18N_KEY;
    }
}
