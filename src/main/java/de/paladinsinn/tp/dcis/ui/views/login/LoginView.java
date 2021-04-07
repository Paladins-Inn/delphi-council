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

import ch.carnet.kasparscherrer.LanguageSelect;
import com.sun.istack.NotNull;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import de.codecamp.vaadin.serviceref.ServiceRef;
import de.paladinsinn.tp.dcis.ui.i18n.I18nSelector;
import de.paladinsinn.tp.dcis.ui.i18n.Translator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Locale;

/**
 * LoginView -- The view for log in a user to the system.
 *
 * We use the component {@link LoginOverlay} for logging in users.
 *
 * @author paulroemer (github.com/vaadin-lerning-center/spring-secured-vaadin)
 * @author klenkes74 {literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-03-26
 */
@Tag("sa-login-view")
@Route(LoginView.ROUTE)
@PageTitle("Login")
public class LoginView extends VerticalLayout implements BeforeEnterObserver, LocaleChangeObserver {
    private static final Logger LOG = LoggerFactory.getLogger(LoginView.class);

    public static final String ROUTE = "login";

    /** The login component. */
    private LanguageSelect languageSelect;
    private LoginOverlay login;

    /** Translator for i18n. */
    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    @Autowired
    private ServiceRef<Translator> translator;

    @PostConstruct
    public void init() {
        LOG.debug("Creating login view.");
        languageSelect = new I18nSelector();

        login = new LoginOverlay();
        login.setAction("login");
        login.setOpened(true);
        login.addForgotPasswordListener(event -> {
            Notification.show("sorry, not implemented yet", 2000, Notification.Position.BOTTOM_STRETCH);
        });

        setLocale(VaadinSession.getCurrent().getLocale());

        add(languageSelect, login);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        login.setError(event.getLocation().getQueryParameters().getParameters().containsKey("error"));

        LOG.trace("Entering login view. ui={}, location={}, error={}",
                event.getUI().getId(), event.getLocation(),
                event.getLocation().getQueryParameters().getParameters().containsKey("error"));
    }

    @Override
    public void localeChange(@NotNull final LocaleChangeEvent event) {
        setLocale(event.getLocale());
    }

    private void setLocale(@NotNull final Locale locale) {
        LOG.trace("Changing locale. locale={}", locale);

        login.setTitle(getTranslation("application.title"));
        login.setDescription(getTranslation("application.description"));
    }
}
