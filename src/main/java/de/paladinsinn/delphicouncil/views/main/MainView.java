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

package de.paladinsinn.delphicouncil.views.main;

import com.sun.istack.NotNull;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import de.codecamp.vaadin.serviceref.ServiceRef;
import de.paladinsinn.delphicouncil.app.i18n.Translator;
import de.paladinsinn.delphicouncil.data.person.Person;
import de.paladinsinn.delphicouncil.data.person.PersonRepository;
import de.paladinsinn.delphicouncil.views.missions.MissionListView;
import de.paladinsinn.delphicouncil.views.operative.OperativesListView;
import de.paladinsinn.delphicouncil.views.person.PersonListView;
import de.paladinsinn.delphicouncil.views.tools.ThreatCardView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.PostConstruct;
import java.util.*;

import static com.vaadin.flow.component.Unit.PIXELS;

/**
 * MainScreen -- The main view is a top-level placeholder for other views.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-03-27
 */
@PWA(name = "Delphi Council Information System", shortName = "DC IS", enableInstallPrompt = false)
@JsModule("./styles/shared-styles.js")
@Theme(value = Lumo.class, variant = Lumo.DARK)
@CssImport("./views/main/main-view.css")
public class MainView extends AppLayout implements LocaleChangeObserver {
    private static final Logger LOG = LoggerFactory.getLogger(MainView.class);

    @Autowired
    private ServiceRef<Translator> translator;

    @Autowired
    private PersonRepository personRepository;

    private Tabs menu;
    private H1 titleBar;


    @PostConstruct
    public void init() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            LOG.debug("Authenticated user. name={}, authorities={}", authentication.getName(), authentication.getAuthorities());
        }

        setLocale(VaadinSession.getCurrent().getLocale());

        setPrimarySection(Section.DRAWER);
        addToNavbar(true, createHeaderContent());
        menu = createMenu(authentication);
        addToDrawer(createDrawerContent(menu));
    }

    private Component createHeaderContent() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setId("header");
        layout.getThemeList().set("dark", true);
        layout.setWidthFull();
        layout.setSpacing(false);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.add(new DrawerToggle());
        titleBar = new H1();
        layout.add(titleBar);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Avatar avatar = createAvatar(authentication);
        layout.add(avatar);

        return layout;
    }

    private Avatar createAvatar(Authentication authentication) {
        Avatar result = new Avatar(authentication.getName());

        Person data = personRepository.findByUsername(authentication.getName());
        result.setImage(data.getGravatar());

        ContextMenu menu = new ContextMenu();
        menu.setTarget(result);

        MenuItem languageSwitch = menu.addItem(getTranslation("avatar.menu.account-settings"),
                e -> Notification.show("Settings not implemented yet.")
        );
        languageSwitch.setEnabled(false);

        menu.addItem(getTranslation("buttons.logout.caption"),
                e -> {
                    LOG.info("User wanted to log out. event={}", e);
                    e.getSource().getUI().ifPresent(ui -> ui.getPage().open("/logout", "_self"));
                }
        );


        return result;
    }

    private Component createDrawerContent(@NotNull final Tabs menu) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.getThemeList().set("spacing-s", true);
        layout.setAlignItems(FlexComponent.Alignment.STRETCH);
        HorizontalLayout logoLayout = new HorizontalLayout();
        logoLayout.setId("logo");
        logoLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        Image logoImage = new Image("/images/logo.png", "Delphi Council");
        logoImage.setMaxWidth(50, PIXELS);
        logoImage.setMaxHeight(50, PIXELS);
        logoLayout.add(logoImage);

        VerticalLayout logo = new VerticalLayout();
        logoLayout.add(logo);

        H1 systemName = new H1(translator.get().getMessage(this, "view.title", getLocale()));
        Div claim = new Div();
        claim.setText(translator.get().getMessage(this, "view.description", getLocale()));
        logo.add(systemName, claim);

        layout.add(logoLayout, menu);
        return layout;
    }

    private Tabs createMenu(@NotNull final Authentication authentication) {
        final Tabs tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.addThemeVariants(TabsVariant.LUMO_MINIMAL);
        tabs.setId("tabs");
        tabs.add(createMenuItems(authentication, getLocale()));
        return tabs;
    }

    private Component[] createMenuItems(@NotNull final Authentication authentication, @NotNull final Locale locale) {
        ArrayList<Component> result = new ArrayList<>();

        ArrayList<Class<? extends Component>> allTabs = new ArrayList<>();
        allTabs.add(MissionListView.class);
        allTabs.add(OperativesListView.class);
        allTabs.add(PersonListView.class);
        allTabs.add(ThreatCardView.class);

        String role = readRoleFromAuthentication(authentication);

        for (Class<? extends Component> t : allTabs) {
            Secured secured = t.getAnnotation(Secured.class);
            if (secured == null || Arrays.asList(secured.value()).contains(role)) {
                LOG.trace(
                        "View is either free or role matches. role={}, view={}, rolesAllowed={}",
                        role,
                        t.getSimpleName(),
                        secured != null ? Arrays.asList(secured.value()) : "-not-set-"
                );

                Tab tab = createTab(translator.get().getMessage(this, t.getSimpleName(), locale), t);
                result.add(tab);
            } else {
                LOG.debug("View is not allowed for user. role={}, view={}, rolesAllowed={}",
                        role,
                        t.getSimpleName(),
                        Arrays.asList(secured.value())
                );
            }
        }

        return result.toArray(new Component[]{});
    }

    private String readRoleFromAuthentication(Authentication authentication) {
        Collection<? extends GrantedAuthority> roles = authentication.getAuthorities();
        String role = "";
        if (! roles.isEmpty()) {
            role = roles.iterator().next().getAuthority();
        }
        return role;
    }

    private static Tab createTab(String text, Class<? extends Component> navigationTarget) {
        final Tab tab = new Tab();
        tab.add(new RouterLink(text, navigationTarget));
        ComponentUtil.setData(tab, Class.class, navigationTarget);
        return tab;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        getTabForComponent(getContent()).ifPresent(menu::setSelectedTab);
        titleBar.setText(getCurrentPageTitle());
    }

    private Optional<Tab> getTabForComponent(Component component) {
        return menu.getChildren().filter(tab -> ComponentUtil.getData(tab, Class.class).equals(component.getClass()))
                .findFirst().map(Tab.class::cast);
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }

    @Override
    public void localeChange(@NotNull final LocaleChangeEvent event) {
        Locale locale = event.getLocale();

        setLocale(locale);
    }

    public void setLocale(@NotNull final Locale locale) {
        LOG.trace("Change locale. view={}, locale={}", this, locale);
    }
}
