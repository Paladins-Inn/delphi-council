/*
 * Copyright (c) 2023. Roland T. Lichti
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.paladinsinn.tp.dcis.ui.components.mvp;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.tabs.Tab;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
@Data
@Slf4j
public abstract class BasicTab<T extends Serializable> extends Tab {
    protected final BasicView<T> view;

    protected final BasicPresenter<T> presenter;

    protected final FormLayout layout = new FormLayout();

    public BasicTab(final BasicPresenter<T> presenter) {
        this.presenter = presenter;
        this.view = presenter.getView();

        setUpLayout();
        add(layout);
    }

    public abstract String getI18nKey();

    private void setUpLayout() {
        layout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("100px", 1),
                new FormLayout.ResponsiveStep("200px", 2),
                new FormLayout.ResponsiveStep("600px", 4)
        );

        layout.setSizeFull();
    }
}
