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

import com.github.appreciated.card.ClickableCard;
import com.github.appreciated.card.action.ActionButton;
import com.github.appreciated.card.action.Actions;
import com.github.appreciated.card.label.PrimaryLabel;
import com.github.appreciated.card.label.SecondaryLabel;
import com.github.appreciated.card.label.TitleLabel;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.router.Route;
import com.vaadin.quarkus.annotation.UIScoped;
import de.paladinsinn.torganized.core.missions.Mission;
import de.paladinsinn.tp.dcis.ui.components.mvp.BasicListViewImpl;
import de.paladinsinn.tp.dcis.ui.views.MainLayout;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.security.PermitAll;
import javax.inject.Inject;


/**
 * MissionListView -- Displays lists of missions.
 *
 * @author klenkes {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-12-29
 */
@Route(value = "/missions", layout = MainLayout.class)
@UIScoped
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@Slf4j
@PermitAll
public class MissionListView extends BasicListViewImpl<Mission> {
    private static final float MIN_WIDTH = 100f;
    private static final float DEFAULT_WIDTH = 150f;
    private static final float MAX_WIDTH = 200f;

    private static final float MIN_HEIGHT = 150f;
    private static final float DEFAULT_HEIGHT = 225f;
    private static final float MAX_HEIGHT = 300f;

    private final MissionListPresenter presenter;

    @PostConstruct
    public void initPresenter() {
        presenter.setView(this);
        presenter.loadData();
    }

    @Override
    protected void updateView() {
        removeAll();

        for (Mission m : data) {
            add(generateNewCard(m));
        }
    }

    @Override
    protected void readForm() {
        // nothing to do - this view is read-only.
    }

    private ClickableCard generateNewCard(Mission mission) {
        ClickableCard result = new ClickableCard(
                e -> {},
                new TitleLabel(mission.getName()),
                new PrimaryLabel(mission.getCode()),
                new SecondaryLabel(mission.getDescription()),
                new Actions(
                        new ActionButton("Show", e -> log.debug("Show event. mission='{}'", mission.getId())),
                        new ActionButton("Report", e-> log.debug("Report event. mission='{}'", mission.getId()))
                )
        );

        setCardDimensions(result);
        result.setId("mission-" + mission.getId());

        return result;
    }

    private void setCardDimensions(ClickableCard card) {
        card.setMinWidth(MIN_WIDTH, Unit.PIXELS);
        card.setWidth(DEFAULT_WIDTH, Unit.PIXELS);
        card.setMaxWidth(MAX_WIDTH, Unit.PIXELS);

        card.setMinHeight(MIN_HEIGHT, Unit.PIXELS);
        card.setHeight(DEFAULT_HEIGHT, Unit.PIXELS);
        card.setMaxHeight(MAX_HEIGHT, Unit.PIXELS);
    }
}
