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

package de.paladinsinn.tp.dcis.ui.views.login;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import de.paladinsinn.tp.dcis.ui.components.TorgActionBar;
import de.paladinsinn.tp.dcis.ui.components.TorgNotification;
import de.paladinsinn.tp.dcis.ui.i18n.I18nPageTitle;
import de.paladinsinn.tp.dcis.ui.i18n.I18nSelector;
import de.paladinsinn.tp.dcis.ui.i18n.TranslatableComponent;
import de.paladinsinn.tp.dcis.ui.views.person.PersonRegistrationEvent;
import de.paladinsinn.tp.dcis.ui.views.person.PersonRegistrationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Locale;

import static com.vaadin.flow.component.Unit.PERCENTAGE;
import static com.vaadin.flow.component.Unit.PIXELS;

/**
 * RegistrationView --
 *
 * @author klenkes74 {literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-04-10
 */
@Tag("sa-registration-view")
@Route(RegistrationView.ROUTE)
@I18nPageTitle("registration.title")
@CssImport("./views/edit-view.css")
public class RegistrationView extends HorizontalLayout implements LocaleChangeObserver, TranslatableComponent {
    private static final Logger LOG = LoggerFactory.getLogger(RegistrationView.class);

    public static final String ROUTE = "register";


    @Value("${application.registration.redirect-url}")
    public String redirectPage = "https://www.ritter-der-stuerme.de/user-registered";

    @Value("${application.registration.cancel-url}")
    public String cancelPage = "https://www.ritter-der-stuerme.de/user-registration-canceled";

    @Autowired
    private PersonRegistrationListener registrationListener;

    private VerticalLayout left, right;
    private FormLayout form;
    private Locale locale;

    private TextField name;
    private TextField firstname;
    private TextField lastname;

    private EmailField email;
    private I18nSelector languageSelect;

    private TextField username;
    private PasswordField password;

    private TorgActionBar actions;

    @PostConstruct
    public void init() {
        addListener(PersonRegistrationEvent.class, registrationListener);
        locale = VaadinSession.getCurrent().getLocale();
        if (locale == null) {
            locale = Locale.GERMANY;
        }

        form = new FormLayout();
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("1px", 1),
                new FormLayout.ResponsiveStep("400px", 2),
                new FormLayout.ResponsiveStep("800px", 3)
        );

        name = new TextField(getTranslation("person.name.caption"));
        name.setClearButtonVisible(true);
        name.setRequired(true);
        name.setRequiredIndicatorVisible(true);
        name.setHelperText(getTranslation("person.name.help"));

        firstname = new TextField(getTranslation("person.first-name.caption"));
        firstname.setHelperText(getTranslation("person.first-name.help"));
        firstname.setClearButtonVisible(true);
        firstname.setRequired(true);
        firstname.setRequiredIndicatorVisible(true);

        lastname = new TextField(getTranslation("person.last-name.caption"));
        lastname.setHelperText(getTranslation("person.last-name.help"));
        lastname.setClearButtonVisible(true);
        lastname.setRequired(true);
        lastname.setRequiredIndicatorVisible(true);

        email = new EmailField(getTranslation("person.email.caption"));
        email.setHelperText(getTranslation("person.email.help"));
        email.setClearButtonVisible(true);
        email.setRequiredIndicatorVisible(true);

        username = new TextField(getTranslation("person.username.caption"));
        username.setHelperText(getTranslation("person.username.help"));
        username.setClearButtonVisible(true);
        username.setRequired(true);
        username.setRequiredIndicatorVisible(true);

        password = new PasswordField(getTranslation("person.password.caption"));
        password.setHelperText(getTranslation("person.password.caption"));
        password.setClearButtonVisible(true);
        password.setRequired(true);
        password.setRequiredIndicatorVisible(true);

        languageSelect = new I18nSelector("input.locale", locale);
        languageSelect.setRequiredIndicatorVisible(true);
        languageSelect.setValue(VaadinSession.getCurrent().getLocale());

        // save
        // reset
        // cancel
        actions = new TorgActionBar(
                "buttons",
                ev -> { // save
                    getEventBus().fireEvent(new PersonRegistrationEvent(
                            this,
                            name.getValue(),
                            lastname.getValue(), firstname.getValue(),
                            email.getValue(),
                            username.getValue(), password.getValue(),
                            languageSelect.getValue()
                    ));

                    new TorgNotification(
                            "registration.send-registration",
                            notify -> ev.getSource().getUI().ifPresent(ui -> ui.navigate(redirectPage)),
                            null,
                            Arrays.asList(name.getValue(), username.getValue(), email.getValue())
                    ).open();
                },
                ev -> { // reset
                    name.setValue(null);
                    lastname.setValue(null);
                    firstname.setValue(null);
                    email.setValue(null);
                    username.setValue(null);
                    password.setValue(null);
                },
                ev -> { // cancel
                    ev.getSource().getUI().ifPresent(ui -> ui.navigate(cancelPage));
                },
                null
        );

        form.add(name,
                lastname, firstname,
                email, new Span(),
                username, password,
                languageSelect, actions);
        form.setColspan(name, 2);
        form.setColspan(actions, 3);

        Div spacer = new Div();
        spacer.setClassName("torg");

        form.setMinWidth(400, PIXELS);
        form.setMaxWidth(600, PIXELS);
        form.setMaxHeight(100, PERCENTAGE);
        left = new VerticalLayout();
        left.add(new Div());
        right = new VerticalLayout();
        right.add(new Div());

        add(left, form, right);

        setFlexGrow(5, form);
        setFlexGrow(5, left, right);
    }


    @Override
    public void translate() {
        if (locale == null) {
            locale = VaadinSession.getCurrent().getLocale();
        }

        LOG.trace("Translating form. locale={}", locale.getDisplayName());

        name.setLabel(getTranslation("person.name.caption"));
        name.setHelperText(getTranslation("person.name.help"));
        firstname.setLabel(getTranslation("person.first-name.caption"));
        firstname.setHelperText(getTranslation("person.first-name.help"));
        lastname.setLabel(getTranslation("person.last-name.caption"));
        lastname.setHelperText(getTranslation("person.last-name.help"));

        email.setLabel(getTranslation("person.email.caption"));
        email.setHelperText(getTranslation("person.email.help"));

        username.setLabel(getTranslation("person.username.caption"));
        username.setHelperText(getTranslation("person.username.help"));
        password.setLabel(getTranslation("person.password.caption"));
        password.setHelperText(getTranslation("person.password.caption"));

        languageSelect.setLabel(getTranslation("input.locale.caption"));

        actions.setLocale(locale);
    }


    @Override
    public void localeChange(LocaleChangeEvent event) {
        setLocale(event.getLocale());
    }


    @Override
    public void setLocale(Locale locale) {
        if (this.locale != null && this.locale.equals(locale)) {
            LOG.debug("Locale not changed. current={}, new={}", this.locale.getDisplayName(), locale.getDisplayName());
            return;
        }

        this.locale = locale;

        translate();
    }
}