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

package de.paladinsinn.tp.dcis.ui.views.person;

import com.sun.istack.NotNull;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.*;
import de.codecamp.vaadin.serviceref.ServiceRef;
import de.paladinsinn.tp.dcis.data.missions.MissionReport;
import de.paladinsinn.tp.dcis.data.operative.Operative;
import de.paladinsinn.tp.dcis.data.person.Person;
import de.paladinsinn.tp.dcis.data.person.PersonRepository;
import de.paladinsinn.tp.dcis.data.person.RoleName;
import de.paladinsinn.tp.dcis.data.specialmissions.SpecialMission;
import de.paladinsinn.tp.dcis.security.LoggedInUser;
import de.paladinsinn.tp.dcis.ui.MainView;
import de.paladinsinn.tp.dcis.ui.components.DataCard;
import de.paladinsinn.tp.dcis.ui.components.TorgButton;
import de.paladinsinn.tp.dcis.ui.i18n.I18nPageTitle;
import de.paladinsinn.tp.dcis.ui.views.missionreports.MissionReportView;
import de.paladinsinn.tp.dcis.ui.views.operative.OperativeEditView;
import de.paladinsinn.tp.dcis.ui.views.specialmissions.SpecialMissionEditorView;
import de.paladinsinn.tp.dcis.ui.views.specialmissions.SpecialMissionView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;

import javax.annotation.PostConstruct;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static com.vaadin.flow.component.Unit.PERCENTAGE;

@Route(value = "persons", layout = MainView.class)
@I18nPageTitle("person.list.title")
@CssImport("./views/lists-view.css")
@Secured({"ADMIN", "ORGA", "JUDGE"})
public class PersonListView extends Div implements AfterNavigationObserver, LocaleChangeObserver {
    private static final Logger LOG = LoggerFactory.getLogger(PersonListView.class);

    @Autowired
    private ServiceRef<PersonRepository> repository;

    @Autowired
    private LoggedInUser user;

    private Grid<Person> grid;

    @PostConstruct
    public void init() {
        addClassName("list-view");
        setSizeFull();

        grid = new Grid<>();
        grid.setHeight("100%");
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS);
        grid.addComponentColumn(this::createCard);
        add(grid);
    }

    private HorizontalLayout createCard(@NotNull final Person person) {
        DataCard card = new DataCard();
        card.init();

        Image image = person.getAvatarImage();
        card.setLogo(image);


        Span name = new Span(getTranslation("person.title.card", person.getName()));
        name.addClassName("name");
        name.setMinWidth(40, PERCENTAGE);

        Span date = new Span(getTranslation("db.entry.created.title", person.getCreated()));
        date.addClassName("date");
        date.setMinWidth(15, PERCENTAGE);

        card.addHeader(name, date);
        card.getHeader().setFlexGrow(100, name);

        Span username = new Span(getTranslation("person.username.card", person.getUsername()));
        username.addClassName("name");
        username.setMinWidth(40, PERCENTAGE);

        Span modified = new Span(getTranslation("db.entry.modified.title", person.getModified()));
        modified.addClassName("date");
        modified.setMinWidth(15, PERCENTAGE);

        card.addFooter(username, modified);
        card.getFooter().setFlexGrow(100, username);

        TorgButton editButton = new TorgButton(
                "buttons.edit",
                PersonEditView.class,
                person.getId(),
                person.getName()
        );

        if (person.matchRole(RoleName.GM)) {
            TorgButton registerExecution = new TorgButton(
                    "specialmission.add",
                    SpecialMissionEditorView.class,
                    new RouteParameters(
                            new RouteParam("id", UUID.randomUUID().toString()),
                            new RouteParam("gm", user.getPerson().getId().toString())
                    )
            );

            card.addMargin(registerExecution);
        }


        TorgButton addOperative = new TorgButton("operative.add", OperativeEditView.class);
        addOperative.addClickListener(e -> e.getSource().getUI().ifPresent(
                ui -> ui.navigate(
                        OperativeEditView.class,
                        new RouteParameters(
                                new RouteParam("person", person.getId().toString()),
                                new RouteParam("id", UUID.randomUUID().toString())
                        )
                )
        ));

        card.addMargin(editButton, addOperative);

        for (MissionReport r : person.getReports()) {
            Span spacer = new Span(" ");
            TorgButton link = new TorgButton(
                    "person.missionreport-link",
                    MissionReportView.class,
                    r.getId(),
                    r.getMission().getCode(), r.getMission().getName(), r.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE)
            );


            card.addDescription(link, spacer);
        }

        for (SpecialMission r : person.getSpecialMissions()) {
            Span spacer = new Span(" ");
            TorgButton link = new TorgButton(
                    "person.missionreport-link",
                    SpecialMissionView.class,
                    r.getId(),
                    r.getTitle(), r.getTitle(), r.getMissionDate().format(DateTimeFormatter.ISO_LOCAL_DATE)
            );


            card.addDescription(link, spacer);
        }

        for (Operative o : person.getOperatives()) {
            card.addDescription(
                    new TorgButton(
                            "person.operative-link",
                            OperativeEditView.class,
                            o.getId(),
                            o.getName()
                    )
            );
        }

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
                    Page<Person> players = repository.get().findAll(page);

                    return players.stream();
                },
                query -> ((int)repository.get().count())
            )
        );
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        LOG.trace("New locale. locale={}", event.getLocale());
    }
}
