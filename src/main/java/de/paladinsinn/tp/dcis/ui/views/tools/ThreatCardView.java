/*
 * Copyright (c) 2021 Kaiserpfalz EDV-Service, Roland T. Lichti
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.paladinsinn.tp.dcis.ui.views.tools;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.paladinsinn.tp.dcis.ui.views.main.MainView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

/**
 * ThreatCardView --
 *
 * @author klenkes74 {literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-04-04
 */
@Route(value = "threatcard", layout = MainView.class)
@PageTitle("Threat Card")
@CssImport("./views/tools-view.css")
public class ThreatCardView extends Div {
    private static final Logger LOG = LoggerFactory.getLogger(ThreatCardView.class);


    @PostConstruct
    public void init() {
        addClassName("threat-card-view");
        setSizeFull();

        String iframe = "<style>iframe {margin: 0; padding: 0; border: none; width: 100%; height: 100%}</style><iframe ignore-router src=\"/ThreatCard/index.html\"/>";
        Label content = new Label();
        content.getElement().setProperty("innerHTML", iframe);

        add(content);
    }

}
