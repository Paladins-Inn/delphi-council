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

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.NativeButton;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

/**
 * PasswordResetView -- View for sending the token and then resetting the password.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.1.0  2021-05-07
 */
@Tag("sa-password-reset-view")
@Route(PasswordResetView.ROUTE + "/:token?")
@I18nPageTitle("password-reset.caption")
@CssImport("./views/edit-view.css")
@Slf4j
public class PasswordResetView extends TorgScreen implements BeforeEnterObserver, LocaleChangeObserver, TranslatableComponent {
    public static final String ROUTE = "password-reset";


    @Autowired
    private StartPasswordResetListener startPasswordResetListener;

    @Autowired
    private ResetPasswordListener resetPasswordListener;

    private Locale locale;

    // Shown in both subviews.
    private H1 title;
    private Div description;
    private I18nSelector languageSelect;
    private TorgActionBar actions;

    // Only in token-less subview.
    private TextField username;
    private EmailField email;

    // Only when token is set.
    private PasswordField password;

    /**
     * person to reset password for.
     */
    private UUID token;


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


        title = new H1(getTranslation("password-reset.caption"));
        description = new Div();
        description.setText(getTranslation("password-reset.help"));

        languageSelect = new I18nSelector("input.locale", locale);
        languageSelect.setRequiredIndicatorVisible(true);
        languageSelect.setValue(VaadinSession.getCurrent().getLocale());

        actions = new TorgActionBar(
                "buttons",
                this::save,
                this::reset,
                this::cancel,
                null
        );
    }

    private void save(ClickEvent<NativeButton> ev) {
        if (token == null) {
            getEventBus().fireEvent(new StartPasswordResetEvent(
                    this,
                    username.getValue(),
                    email.getValue()
            ));
        } else {
            getEventBus().fireEvent(new ResetPasswordEvent(
                    this,
                    token,
                    password.getValue()
            ));
        }

        ev.getSource().getUI().ifPresent(ui -> ui.navigate(LoginView.ROUTE));
    }

    private void reset(ClickEvent<NativeButton> ev) {
        email.setValue(null);
        username.setValue(null);
        password.setValue(null);
    }

    private void cancel(ClickEvent<NativeButton> ev) {
        ev.getSource().getUI().ifPresent(ui -> ui.navigate(LoginView.ROUTE));
    }


    @Override
    public void translate() {
        if (locale == null) {
            locale = VaadinSession.getCurrent().getLocale();
        }

        log.trace("Translating form. locale={}", locale.getDisplayName());

        title.setText(getTranslation("password-reset.caption"));
        description.setText(getTranslation("password-reset.help"));
        languageSelect.setLabel(getTranslation("input.locale.caption"));
        actions.setLocale(locale);

        if (token == null) {
            email.setLabel(getTranslation("person.email.caption"));
            email.setHelperText(getTranslation("password-reset.email.help"));

            username.setLabel(getTranslation("person.username.caption"));
            username.setHelperText(getTranslation("password-reset.username.help"));
        } else {
            password.setLabel(getTranslation("person.password.caption"));
            password.setHelperText(getTranslation("password-reset.password.caption"));
        }
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<String> tokenString = event.getRouteParameters().get("token");
        log.trace("confirming new user. token={}", tokenString);


        form.add(title, description, languageSelect);
        form.setColspan(title, 4);
        form.setColspan(description, 4);
        form.setColspan(languageSelect, 4);

        tokenString.ifPresentOrElse(
                token -> {
                    try {
                        this.token = UUID.fromString(token);
                        log.info("Password reset token. token={}", token);
                    } catch (IllegalArgumentException e) {
                        new TorgNotification(
                                "password-reset.invalid-token",
                                null,
                                null,
                                null
                        ).open();

                        event.getUI().navigate(LoginView.ROUTE);
                    }

                    password = new PasswordField(getTranslation("person.password.caption"));
                    password.setHelperText(getTranslation("person.password.caption"));
                    password.setClearButtonVisible(true);
                    password.setRequired(true);
                    password.setRequiredIndicatorVisible(true);
                    password.setMinLength(8);
                    password.setMaxLength(50);

                    form.add(password, actions);
                    form.setColspan(password, 4);
                },
                () -> {
                    username = new TextField(getTranslation("person.username.caption"));
                    username.setHelperText(getTranslation("password-reset.username.help"));
                    username.setClearButtonVisible(true);
                    username.setRequired(false);
                    username.setRequiredIndicatorVisible(false);
                    username.setMaxLength(50);

                    email = new EmailField(getTranslation("person.email.caption"));
                    email.setHelperText(getTranslation("password-reset.email.help"));
                    email.setClearButtonVisible(true);
                    email.setRequiredIndicatorVisible(false);
                    username.setMaxLength(100);

                    form.add(username, email, actions);
                    form.setColspan(email, 4);
                    form.setColspan(username, 4);
                }
        );

        form.setColspan(actions, 4);
    }


    @Override
    public void localeChange(LocaleChangeEvent event) {
        setLocale(event.getLocale());
    }


    @Override
    public void setLocale(Locale locale) {
        if (this.locale != null && this.locale.equals(locale)) {
            log.debug("Locale not changed. current={}, new={}", this.locale.getDisplayName(), locale.getDisplayName());
            return;
        }

        this.locale = locale;

        translate();
    }
}