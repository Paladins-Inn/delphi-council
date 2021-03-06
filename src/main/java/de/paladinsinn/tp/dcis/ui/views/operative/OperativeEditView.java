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

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import de.paladinsinn.tp.dcis.data.Clearance;
import de.paladinsinn.tp.dcis.data.operative.Operative;
import de.paladinsinn.tp.dcis.data.operative.OperativeRepository;
import de.paladinsinn.tp.dcis.data.person.Person;
import de.paladinsinn.tp.dcis.data.person.PersonRepository;
import de.paladinsinn.tp.dcis.security.LoggedInUser;
import de.paladinsinn.tp.dcis.ui.MainView;
import de.paladinsinn.tp.dcis.ui.i18n.I18nPageTitle;
import de.paladinsinn.tp.dcis.ui.i18n.TranslatableComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.*;

/**
 * StormKnightEditView -- An in-game view of the missions inside the database.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-03-28
 */
@Route(value = "operative/:id?/:person?", layout = MainView.class)
@I18nPageTitle("operative.editor.caption")
@CssImport("./views/edit-view.css")
@Slf4j
public class OperativeEditView extends Div implements BeforeEnterObserver, LocaleChangeObserver, TranslatableComponent, Serializable, AutoCloseable {
    private Locale locale;

    @Autowired
    private OperativeService operativeService;

    @Autowired
    private OperativeRepository operativeRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private LoggedInUser user;

    private OperativeForm form;

    @PostConstruct
    public void init() {
        addClassName("edit-view");
        setSizeFull();

        form = new OperativeForm(operativeService, personRepository, user);

        add(form);
    }


    @Override
    public void localeChange(LocaleChangeEvent event) {
        setLocale(event.getLocale());
        translate();
    }

    @Override
    public void translate() {
        log.trace("Translate View. locale={}", locale);

        form.translate();
    }

    @Override
    public void setLocale(Locale locale) {
        this.locale = locale;

        form.setLocale(locale);
    }


    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<String> id = event.getRouteParameters().get("id");
        Optional<String> personId = event.getRouteParameters().get("person");
        log.trace("Entering form. person={}, id={}", personId, id);

        id.ifPresentOrElse(
                e -> operativeRepository.findById(UUID.fromString(e)).ifPresentOrElse(
                        form::setData,
                        () -> form.setData(generateNewOperative(personId, e))
                ),
                () -> form.setData(generateNewOperative(personId))
        );
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Operative generateNewOperative(Optional<String> personId) {
        return generateNewOperative(personId, UUID.randomUUID().toString());
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Operative generateNewOperative(Optional<String> personId, String operativeId) {
        Operative data = new Operative();
        data.setId(UUID.fromString(operativeId));
        data.setClearance(Clearance.ALPHA);
        data.preUpdate();

        Person person = data.getPlayer();
        if (person == null) {
            personId.ifPresentOrElse(
                    p -> personRepository.findById(UUID.fromString(p)).ifPresentOrElse(
                            data::setPlayer,
                            () -> {

                            }
                    ),
                    () -> data.setPlayer(user.getPerson())
            );
        }
        return data;
    }

    @Override
    public void close() throws Exception {
        log.debug("Closing view.");

        form.close();
        removeAll();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OperativeEditView)) return false;
        OperativeEditView that = (OperativeEditView) o;
        return getLocale().equals(that.getLocale());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLocale());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", OperativeEditView.class.getSimpleName() + "[", "]")
                .toString();
    }
}
