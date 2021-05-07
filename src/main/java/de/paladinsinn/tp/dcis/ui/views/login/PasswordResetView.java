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
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import de.paladinsinn.tp.dcis.ui.components.TorgActionBar;
import de.paladinsinn.tp.dcis.ui.components.TorgNotification;
import de.paladinsinn.tp.dcis.ui.components.TorgScreen;
import de.paladinsinn.tp.dcis.ui.i18n.I18nPageTitle;
import de.paladinsinn.tp.dcis.ui.i18n.I18nSelector;
import de.paladinsinn.tp.dcis.ui.i18n.TranslatableComponent;
import de.paladinsinn.tp.dcis.ui.views.person.ResetPasswordEvent;
import de.paladinsinn.tp.dcis.ui.views.person.ResetPasswordListener;
import de.paladinsinn.tp.dcis.ui.views.person.StartPasswordResetEvent;
import de.paladinsinn.tp.dcis.ui.views.person.StartPasswordResetListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

import static com.vaadin.flow.component.Unit.PIXELS;

/**
 * RegistrationView --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-04-10
 */
@Tag("sa-registration-view")
@Route(PasswordResetView.ROUTE + "/:token?")
@I18nPageTitle("password-reset.caption")
@CssImport("./views/edit-view.css")
public class PasswordResetView extends TorgScreen implements BeforeEnterObserver, LocaleChangeObserver, TranslatableComponent {
    private static final Logger LOG = LoggerFactory.getLogger(PasswordResetView.class);

    public static final String ROUTE = "password-reset";


    @Autowired
    private StartPasswordResetListener startPasswordResetListener;

    @Autowired
    private ResetPasswordListener resetPasswordListener;

    private Locale locale;

    private H1 title;
    private Div description;

    private TextField username;
    private EmailField email;
    private I18nSelector languageSelect;

    private PasswordField password;

    private TorgActionBar actions;

    @PostConstruct
    public void init() {
        addListener(StartPasswordResetEvent.class, startPasswordResetListener);
        addListener(ResetPasswordEvent.class, resetPasswordListener);

        locale = VaadinSession.getCurrent().getLocale();
        if (locale == null) {
            locale = Locale.GERMAN;
        }

        setSizeFull();
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();

        FormLayout form = new FormLayout();
        form.setHeightFull();
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("100px", 1),
                new FormLayout.ResponsiveStep("300px", 2),
                new FormLayout.ResponsiveStep("600px", 4)
        );

        title = new H1(getTranslation("password-reset.caption"));
        description = new Div();
        description.setText(getTranslation("password-reset.help"));

        email = new EmailField(getTranslation("person.email.caption"));
        email.setHelperText(getTranslation("person.email.help"));
        email.setClearButtonVisible(true);
        email.setRequiredIndicatorVisible(true);

        username = new TextField(getTranslation("person.username.caption"));
        username.setHelperText(getTranslation("person.username.help"));
        username.setClearButtonVisible(true);
        username.setRequired(true);
        username.setRequiredIndicatorVisible(true);
        username.setMinLength(1);
        username.setMaxLength(50);

        password = new PasswordField(getTranslation("person.password.caption"));
        password.setHelperText(getTranslation("person.password.caption"));
        password.setClearButtonVisible(true);
        password.setRequired(true);
        password.setRequiredIndicatorVisible(true);
        password.setMinLength(8);
        password.setMaxLength(50);

        languageSelect = new I18nSelector("input.locale", locale);
        languageSelect.setRequiredIndicatorVisible(true);
        languageSelect.setValue(VaadinSession.getCurrent().getLocale());

        // save
        // reset
        // cancel
        actions = new TorgActionBar(
                "buttons",
                ev -> { // save
                    getEventBus().fireEvent(new StartPasswordResetEvent(
                            this,
                            username.getValue(),
                            email.getValue()
                    ));

                    new TorgNotification(
                            "password-reset.send-registration",
                            null,
                            null,
                            Arrays.asList(username.getValue(), email.getValue())
                    ).open();

                    ev.getSource().getUI().ifPresent(ui -> ui.navigate(LoginView.ROUTE));
                },
                ev -> { // reset
                    email.setValue(null);
                    username.setValue(null);
                    password.setValue(null);
                },
                ev -> { // cancel
                    ev.getSource().getUI().ifPresent(ui -> ui.navigate(LoginView.ROUTE));
                },
                null
        );

        form.add(title, description,
                email, languageSelect,
                username, password,
                actions);
        form.setColspan(title, 4);
        form.setColspan(description, 4);
        form.setColspan(email, 4);
        form.setColspan(username, 2);
        form.setColspan(password, 2);
        form.setColspan(actions, 4);

        form.setMinWidth(400, PIXELS);
        form.setMaxWidth(600, PIXELS);

        add(form);
    }


    @Override
    public void translate() {
        if (locale == null) {
            locale = VaadinSession.getCurrent().getLocale();
        }

        LOG.trace("Translating form. locale={}", locale.getDisplayName());

        title.setText(getTranslation("registration.caption"));
        description.setText(getTranslation("registration.help"));

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
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<String> tokenString = event.getRouteParameters().get("token");
        LOG.trace("confirming new user. token={}", tokenString);

        tokenString.ifPresent(token -> {
            try {
                LOG.info("Password reset token. token={}", token);

                // FIXME 2021-05-07 rlichti Create the real password reset flow.

            } catch (IllegalArgumentException e) {
                new TorgNotification(
                        "password-reset.invalid-token.wrong-format",
                        null,
                        null,
                        null
                ).open();
            }

            event.getUI().navigate(LoginView.ROUTE);
        });
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