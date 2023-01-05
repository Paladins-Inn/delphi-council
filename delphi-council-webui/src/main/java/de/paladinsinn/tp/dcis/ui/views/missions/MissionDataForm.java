/*
 * Copyright (c) 2023. Roland T. Lichti
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.paladinsinn.tp.dcis.ui.views.missions;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.quarkus.annotation.UIScoped;
import de.paladinsinn.torganized.core.missions.Mission;
import de.paladinsinn.tp.dcis.ui.components.mvp.BasicDataForm;
import de.paladinsinn.tp.dcis.ui.components.users.FrontendUser;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;


/**
 * MissionDataForm -- Displays the data for the mission in a a hopefully easy to use manner.
 *
 * @author klenkes {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2023-01-01
 */
@UIScoped
@ToString(onlyExplicitlyIncluded = true)
@Setter
@Getter
@Slf4j
public class MissionDataForm extends BasicDataForm<Mission> {

    private final MissionDataFormBaseData baseData;
    private final MissionDataFormResultQuestionaire resultQuestionaire;
    private final MissionDataFormResults results;

    private final MissionDataFormDispatchedTeams dispatchedTeams;

    private final Binder<Mission> binder = new BeanValidationBinder<>(Mission.class);


    @Inject
    public MissionDataForm(
            final MissionDataFormBaseData baseData,
            final MissionDataFormResultQuestionaire resultQuestionaire,
            final MissionDataFormResults results,
            final MissionDataFormDispatchedTeams dispatchedTeams,
            final FrontendUser user
    ) {
        super(user);

        this.baseData = baseData;
        this.resultQuestionaire = resultQuestionaire;
        this.results = results;
        this.dispatchedTeams = dispatchedTeams;

    }

    @Override
    public void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        addTab(baseData);
        addTab(resultQuestionaire);
        addTab(results);
        addTab(dispatchedTeams);
    }
}


