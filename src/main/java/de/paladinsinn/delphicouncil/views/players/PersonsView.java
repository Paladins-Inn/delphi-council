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

package de.paladinsinn.delphicouncil.views.players;

import com.sun.istack.NotNull;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.codecamp.vaadin.serviceref.ServiceRef;
import de.paladinsinn.delphicouncil.data.person.Person;
import de.paladinsinn.delphicouncil.data.person.PersonRepository;
import de.paladinsinn.delphicouncil.ui.DataCard;
import de.paladinsinn.delphicouncil.views.main.MainView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;

import javax.annotation.PostConstruct;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Route(value = "players", layout = MainView.class)
@PageTitle("Persons")
@CssImport("./views/players/players-view.css")
@Secured({"ADMIN","ORGA","JUDGE"})
public class PersonsView extends Div implements AfterNavigationObserver, LocaleChangeObserver {
    private static final Logger LOG = LoggerFactory.getLogger(PersonsView.class);

    @Autowired
    private ServiceRef<PersonRepository> repository;

    private Grid<Person> grid;

    private Locale locale;

    @PostConstruct
    public void init() {
        addClassName("players-view");
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

        Image image = new Image();
        image.setSrc(person.getGravatar());
        card.setLogo(image);

        Span name = new Span(person.getName());
        name.addClassName("name");
        Span date = new Span(person.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE));
        date.addClassName("date");
        card.addHeader(name, date);

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
        LOG.trace("New locale. view={}, locale={}", this, event.getLocale());

        this.locale = event.getLocale();
    }
}
