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

package de.paladinsinn.tp.dcis.ui.views.operative;

import com.sun.istack.NotNull;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.*;
import de.codecamp.vaadin.serviceref.ServiceRef;
import de.paladinsinn.tp.dcis.data.operative.Operative;
import de.paladinsinn.tp.dcis.data.operative.OperativeReport;
import de.paladinsinn.tp.dcis.data.operative.OperativeService;
import de.paladinsinn.tp.dcis.data.person.PersonRepository;
import de.paladinsinn.tp.dcis.security.LoggedInUser;
import de.paladinsinn.tp.dcis.ui.components.DataCard;
import de.paladinsinn.tp.dcis.ui.components.TorgButton;
import de.paladinsinn.tp.dcis.ui.i18n.TranslatableComponent;
import de.paladinsinn.tp.dcis.ui.views.main.MainView;
import de.paladinsinn.tp.dcis.ui.views.missions.MissionReportView;
import de.paladinsinn.tp.dcis.ui.views.person.PersonEditView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.StringJoiner;

import static com.vaadin.flow.component.Unit.PERCENTAGE;
import static com.vaadin.flow.component.Unit.PIXELS;

/**
 * MissionCatalogueView -- An in-game view of the missions inside the database.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-03-28
 */
@Route(value = "operatives", layout = MainView.class)
@PageTitle("Storm Knights")
@CssImport("./views/lists-view.css")
public class OperativesListView extends Div implements Serializable, AutoCloseable, LocaleChangeObserver, TranslatableComponent, AfterNavigationObserver {
    public static final Long serial = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(OperativesListView.class);

    private final HashSet<TranslatableComponent> translatables = new HashSet<>();
    private Locale locale;

    @Autowired
    private OperativeService service;

    @Autowired
    private LoggedInUser user;

    @Autowired
    private ServiceRef<PersonRepository> personRepository;

    private final Grid<Operative> grid = new Grid<>();
    private TorgButton addOperative;

    @PostConstruct
    public void init() {
        addClassName("list-view");
        setSizeFull();

        grid.setMinHeight(80, PERCENTAGE);
        grid.setMaxHeight(100, PERCENTAGE);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS);
        grid.addComponentColumn(this::createCard);

        addOperative = new TorgButton(
                "operative.add",
                OperativeEditView.class,
                new RouteParameters(new RouteParam("person", user.getPerson().getId().toString()))
        );

        add(addOperative, grid);
    }

    private DataCard createCard(@NotNull final Operative data) {
        DataCard card = new DataCard();
        card.init();

        // Logo
        Image logo = new Image();
        if (data.getAvatar() != null) {
            logo.setSrc(data.getAvatar());
            card.setLogo(logo);
        }

        // Header
        Span title = new Span(getTranslation("operative.title.card", getLocale(), data.getName()));
        title.setClassName("name");
        title.setMinWidth(300, PIXELS);
        title.setWidth(40, PERCENTAGE);

        Span clearance = new Span(getTranslation("torg.clearance.card", getLocale(), getTranslation("torg.clearance." + data.getClearance().name(), getLocale())));
        clearance.setClassName("date");
        clearance.setMinWidth(150, PIXELS);
        clearance.setWidth(20, PERCENTAGE);

        Span publication = new Span();
        publication.setClassName("name");
        publication.setMinWidth(150, PIXELS);
        publication.setWidth(20, PERCENTAGE);


        card.addHeader(title, publication, clearance);
        card.getHeader().setFlexGrow(100, title);
        card.getHeader().setFlexGrow(1, publication);
        card.getHeader().setFlexGrow(1, clearance);

        // Main text
        FlexLayout description = new FlexLayout();

        for (OperativeReport r : data.getReports()) {
            LOG.debug("Adding Link to mission reports. mission={}, report={}", data.getId(), r.getId());

            TorgButton reportButton = new TorgButton(
                    "operative.report-link",
                    MissionReportView.class,
                    r.getReport().getId(),
                    r.getReport().getMission().getCode(),
                    r.getReport().getMission().getTitle(),
                    r.getReport().getDate().format(DateTimeFormatter.ISO_LOCAL_DATE),
                    r.getReport().getGameMaster().getUsername()
            );

            card.addDescription(reportButton);
        }

        description.getStyle().set("white-space", "normal");
        description.getStyle().set("test-align", "right");
        description.addClassName("description");
        description.setMaxWidth(100, PERCENTAGE);
        description.setMaxHeight(500, PIXELS);

        card.addDescription(description);
        card.getDescription().setFlexGrow(100, description);

        // Footer line
        Span code = new Span(getTranslation("operative.code.card", getLocale(), data.getId()));
        code.setClassName("date");
        code.setMinWidth(300, PIXELS);
        code.setWidth(40, PERCENTAGE);


        Span created = new Span(getTranslation("db.entry.created.title", getLocale(), data.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE)));
        created.addClassName("date");
        created.setMinWidth(150, PIXELS);
        created.setWidth(20, PERCENTAGE);

        Span modified = new Span(getTranslation("db.entry.modified.title", getLocale(), data.getModified().format(DateTimeFormatter.ISO_LOCAL_DATE)));
        modified.addClassName("date");
        modified.setMinWidth(150, PIXELS);
        modified.setWidth(20, PERCENTAGE);

        card.addFooter(code, created, modified);
        card.getFooter().setFlexGrow(100, publication);
        card.getFooter().setFlexGrow(1, created);
        card.getFooter().setFlexGrow(1, modified);

        TorgButton edit = new TorgButton(
                "buttons.edit",
                OperativeEditView.class,
                data.getId()
        );
        card.addMargin(edit);

        if (
                user.isOrga()
                        || user.isAdmin()
                        || user.isJudge()
                        || data.getPlayer().equals(user.getPerson())
        ) {
            TorgButton player = new TorgButton(
                    "operative.player-link",
                    PersonEditView.class,
                    data.getPlayer().getId(),
                    data.getPlayer().getName()
            );
            card.addMargin(player);
        }

        translatables.add(card);
        return card;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        grid.setDataProvider(
                DataProvider.fromCallbacks(
                        query -> {
                            int offset = query.getOffset();
                            int limit = query.getLimit();

                            Pageable page = PageRequest.of(offset/limit, limit);
                            Page<Operative> missions = service.findAll(page);

                            return missions.stream();
                        },
                        query -> service.count()
                )
        );
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        LOG.trace("Locale change event. locale={}", event.getLocale());

        setLocale(event.getLocale());
        translate();
    }

    @Override
    public void translate() {
        LOG.trace("Translate View. locale={}", locale);

        for (TranslatableComponent t : translatables) {
            t.translate();
        }

        addOperative.translate();
    }

    @Override
    public void setLocale(Locale locale) {
        this.locale = locale;

        for (TranslatableComponent t : translatables) {
            t.setLocale(locale);
        }
    }


    @Override
    public void close() throws Exception {
        LOG.debug("Closing view.");

        removeAll();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OperativesListView)) return false;
        OperativesListView that = (OperativesListView) o;
        return getLocale().equals(that.getLocale());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLocale());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", OperativesListView.class.getSimpleName() + "[", "]")
                .add("locale=" + locale)
                .toString();
    }
}
