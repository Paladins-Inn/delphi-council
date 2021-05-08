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

package de.paladinsinn.tp.dcis.ui.views.missions;

import com.sun.istack.NotNull;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import de.paladinsinn.tp.dcis.data.missions.Mission;
import de.paladinsinn.tp.dcis.data.missions.MissionRepository;
import de.paladinsinn.tp.dcis.ui.MainView;
import de.paladinsinn.tp.dcis.ui.i18n.I18nPageTitle;
import de.paladinsinn.tp.dcis.ui.i18n.TranslatableComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

/**
 * MissionEditorView -- Editor for adding new {@link Mission}.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-04-06
 */
@Route(value = "mission/:id?", layout = MainView.class)
@I18nPageTitle("mission.editor.caption")
@CssImport("./views/edit-view.css")
@Secured({"JUDGE", "ORGA", "ADMIN"})
@Slf4j
public class MissionEditorView extends Div implements BeforeEnterObserver, LocaleChangeObserver, TranslatableComponent, Serializable, AutoCloseable {
    @Autowired
    private MissionForm missionForm;

    @Autowired
    private MissionRepository missionRepository;

    private Locale locale;

    @PostConstruct
    public void init() {
        setLocale(VaadinSession.getCurrent().getLocale());
        log.debug("Loading form. locale={}", locale);

        addClassName("edit-view");

        add(missionForm);
        translate();
    }


    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<String> id = event.getRouteParameters().get("id");
        log.trace("Entering form. id={}", id);

        id.ifPresentOrElse(
                e -> missionRepository.findById(UUID.fromString(e)).ifPresentOrElse(
                        this::setData,
                        () -> setData(new Mission())
                ),
                () -> setData(new Mission())
        );
    }

    private void setData(@NotNull Mission data) {
        if (data == null || data.equals(missionForm.getData().orElse(null))) {
            log.info("Data is null or data didn't change - will do nothing. data={}", data);
            return;
        }
        log.trace("Loaded mission. mission={}", data);

        missionForm.setData(data);
        translate();
    }


    @Override
    public void localeChange(LocaleChangeEvent event) {
        log.trace("Locale change event. locale={}", event.getLocale());

        setLocale(event.getLocale());
    }

    @Override
    public void translate() {
        if (locale == null || missionForm.getData().isEmpty()) {
            log.debug("No locale or no data - will do nothing. locale={}, data={}", locale, missionForm.getData());
            return;
        }

        missionForm.translate();
    }

    @Override
    public void setLocale(Locale locale) {
        if (this.locale != null && this.locale.equals(locale)) {
            log.debug("Locale didn't change - will do nothing. locale={}", locale);
            return;
        }

        this.locale = locale;

        missionForm.setLocale(locale);
        translate();
    }

    @Override
    public void close() throws Exception {
        log.trace("Closing form.");

        missionForm.close();

        removeAll();

        log.debug("Closed form.");
    }
}
