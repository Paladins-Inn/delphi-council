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

package de.paladinsinn.delphicouncil.views.person;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.paladinsinn.delphicouncil.app.i18n.TranslatableComponent;
import de.paladinsinn.delphicouncil.data.person.Person;
import de.paladinsinn.delphicouncil.data.person.PersonService;
import de.paladinsinn.delphicouncil.ui.forms.persons.PersonForm;
import de.paladinsinn.delphicouncil.views.main.MainView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.*;

/**
 * PersonEditView -- An in-game view of the missions inside the database.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-03-28
 */
@Route(value = "person/:id?", layout = MainView.class)
@PageTitle("Person")
@CssImport("./views/person/edit-view.css")
public class PersonEditView extends Div implements BeforeEnterObserver, LocaleChangeObserver, TranslatableComponent, Serializable, AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(PersonEditView.class);
    public static final Long serial = 1L;


    private Locale locale;

    @Autowired
    private PersonService repository;

    private PersonForm form;

    @PostConstruct
    public void init() {
        addClassName("person-edit-view");
        setSizeFull();

        form = new PersonForm(repository);

        add(form);
    }


    @Override
    public void localeChange(LocaleChangeEvent event) {
        setLocale(event.getLocale());
        translate();
    }

    @Override
    public void translate() {
        LOG.trace("Translate View. view={}, locale={}", this, locale);

        form.translate();
    }

    @Override
    public void setLocale(Locale locale) {
        this.locale = locale;

        form.setLocale(locale);
    }


    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<String> personId = event.getRouteParameters().get("id");
        LOG.trace("Entering form. view={}, person={}", this, personId);

        Optional<Person> person = Optional.empty();
        if (personId.isPresent()) {
            person = repository.findById(UUID.fromString(personId.get()));
        } else {
            LOG.warn("Person ID is missing. view={}, person={}", this, personId);
        }

        person.ifPresent(form::setPerson);
    }


    @Override
    public void close() throws Exception {
        LOG.debug("Closing view. view={}", this);

        form.close();
        removeAll();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PersonEditView)) return false;
        PersonEditView that = (PersonEditView) o;
        return getLocale().equals(that.getLocale());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLocale());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PersonEditView.class.getSimpleName() + "[", "]")
                .add("locale=" + locale)
                .toString();
    }
}
