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

package de.paladinsinn.delphicouncil.views.missions;

import com.sun.istack.NotNull;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import de.paladinsinn.delphicouncil.app.i18n.TranslatableComponent;
import de.paladinsinn.delphicouncil.data.missions.Mission;
import de.paladinsinn.delphicouncil.data.missions.MissionService;
import de.paladinsinn.delphicouncil.ui.forms.missions.MissionForm;
import de.paladinsinn.delphicouncil.views.main.MainView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

/**
 * MissionEditorView -- Editor for adding new {@link de.paladinsinn.delphicouncil.data.missions.Mission}.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-04-06
 */
@Route(value = "mission/:id?", layout = MainView.class)
@PageTitle("Mission")
@CssImport("./views/edit-view.css")
@Secured({"JUDGE", "ORGA", "ADMIN"})
public class MissionEditorView extends Div implements BeforeEnterObserver, LocaleChangeObserver, TranslatableComponent, Serializable, AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(MissionEditorView.class);

    @Autowired
    private MissionForm missionForm;

    @Autowired
    private MissionService missionService;

    private Locale locale;

    @PostConstruct
    public void init() {
        setLocale(VaadinSession.getCurrent().getLocale());
        LOG.debug("Loading form. view={}, locale={}", this, locale);

        addClassName("edit-view");

        add(missionForm);
        translate();
    }


    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<String> id = event.getRouteParameters().get("id");
        LOG.trace("Entering form. view={}, id={}", this, id);

        id.ifPresentOrElse(
                e -> missionService.findById(UUID.fromString(e)).ifPresentOrElse(
                        this::setData,
                        () -> setData(new Mission())
                ),
                () -> setData(new Mission())
        );
    }

    private void setData(@NotNull Mission data) {
        if (data == null || data.equals(missionForm.getMission().orElse(null))) {
            LOG.info("Data is null or data didn't change - will do nothing. data={}", data);
            return;
        }
        LOG.trace("Loaded mission. view={}, mission={}", this, data);

        missionForm.setMission(data);
        translate();
    }


    @Override
    public void localeChange(LocaleChangeEvent event) {
        LOG.trace("Locale change event. view={}, locale={}, event={}", this, event.getLocale(), event);

        setLocale(event.getLocale());
    }

    @Override
    public void translate() {
        if (locale == null || missionForm.getMission().isEmpty()) {
            LOG.debug("No locale or no data - will do nothing. view={}, locale={}, data={}", this, locale, missionForm.getMission());
            return;
        }

        missionForm.translate();
    }

    @Override
    public void setLocale(Locale locale) {
        if (this.locale != null && this.locale.equals(locale)) {
            LOG.debug("Locale didn't change - will do nothing. view={}, locale={}", this, locale);
            return;
        }

        this.locale = locale;

        missionForm.setLocale(locale);
        translate();
    }

    @Override
    public void close() throws Exception {
        LOG.trace("Closing form. view={}", this);

        missionForm.close();

        removeAll();

        LOG.debug("Closed form. view={}", this);
    }
}
