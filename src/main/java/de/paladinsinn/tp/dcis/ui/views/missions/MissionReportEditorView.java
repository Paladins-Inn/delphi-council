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
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import de.paladinsinn.tp.dcis.data.missions.MissionReport;
import de.paladinsinn.tp.dcis.data.missions.MissionReportService;
import de.paladinsinn.tp.dcis.data.missions.MissionService;
import de.paladinsinn.tp.dcis.data.person.PersonService;
import de.paladinsinn.tp.dcis.ui.forms.missions.MissionReportForm;
import de.paladinsinn.tp.dcis.ui.i18n.TranslatableComponent;
import de.paladinsinn.tp.dcis.ui.views.main.MainView;
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
 * MissionReportEditorView -- Editor for adding new {@link MissionReport}.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-04-06
 */
@Route(value = "missionreport/:mission?/:id?/:gm?", layout = MainView.class)
@PageTitle("Mission Report")
@CssImport("./views/edit-view.css")
@Secured({"JUDGE", "ORGA", "ADMIN"})
public class MissionReportEditorView extends Div implements BeforeEnterObserver, LocaleChangeObserver, TranslatableComponent, Serializable, AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(MissionReportEditorView.class);

    @Autowired
    private MissionReportForm form;

    @Autowired
    private MissionReportService service;

    @Autowired
    private MissionService missionService;

    @Autowired
    private PersonService personService;

    private Locale locale;

    @PostConstruct
    public void init() {
        setLocale(VaadinSession.getCurrent().getLocale());
        LOG.debug("Loading form. locale={}", locale);

        addClassName("edit-view");

        add(form);
        translate();
    }


    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<String> id = event.getRouteParameters().get("id");
        Optional<String> missionId = event.getRouteParameters().get("mission");
        Optional<String> personId = event.getRouteParameters().get("gm");
        LOG.trace("Entering form. report={}, mission={}, gm={}", id, missionId, personId);

        id.ifPresentOrElse(
                e -> service.findById(UUID.fromString(e)).ifPresentOrElse(
                        this::setData,
                        () -> setData(generateNewMission(missionId, personId))
                ),
                () -> setData(generateNewMission(missionId, personId))
        );
    }

    private MissionReport generateNewMission(
            @SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<String> missionId,
            @SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<String> personId
    ) {
        MissionReport result = new MissionReport();

        missionId.flatMap(e -> missionService.findById(UUID.fromString(e))).ifPresent(result::setMission);
        personId.flatMap(e -> personService.findById(UUID.fromString(e))).ifPresent(result::setGameMaster);

        return result;
    }

    private void setData(@NotNull MissionReport data) {
        if (data == null || data.equals(form.getData().orElse(null))) {
            LOG.info("Data is null or data didn't change - will do nothing. data={}", data);
            return;
        }
        LOG.trace("Loaded mission report. mission={}, report={}", data.getMission().getId(), data.getId());

        form.setData(data);
        translate();
    }


    @Override
    public void localeChange(LocaleChangeEvent event) {
        LOG.trace("Locale change event. locale={}, event={}", event.getLocale(), event);

        setLocale(event.getLocale());
    }

    @Override
    public void translate() {
        if (locale == null || form.getData().isEmpty()) {
            LOG.debug("No locale or no data - will do nothing. locale={}, data={}", locale, form.getData());
            return;
        }

        form.translate();
    }

    @Override
    public void setLocale(Locale locale) {
        if (this.locale != null && this.locale.equals(locale)) {
            LOG.debug("Locale didn't change - will do nothing. locale={}", locale);
            return;
        }

        this.locale = locale;

        form.setLocale(locale);
        translate();
    }

    @Override
    public void close() throws Exception {
        LOG.trace("Closing form.");

        form.close();

        removeAll();

        LOG.debug("Closed form.");
    }
}
