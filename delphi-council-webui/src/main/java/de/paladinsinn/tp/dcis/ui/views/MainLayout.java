package de.paladinsinn.tp.dcis.ui.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.quarkus.annotation.UIScoped;
import de.paladinsinn.tp.dcis.ui.components.appnav.AppNav;
import de.paladinsinn.tp.dcis.ui.components.appnav.AppNavItem;
import de.paladinsinn.tp.dcis.ui.components.notifications.ErrorNotification;
import de.paladinsinn.tp.dcis.ui.components.users.FrontendUser;
import de.paladinsinn.tp.dcis.ui.views.about.AboutView;
import de.paladinsinn.tp.dcis.ui.views.about.ImprintView;
import de.paladinsinn.tp.dcis.ui.views.missions.MissionListView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * The main view is a top-level placeholder for other views.
 */
@UIScoped
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@AnonymousAllowed
public class MainLayout extends AppLayout implements RouterLayout {

    @SuppressWarnings("CdiInjectionPointsInspection")
    @Inject
    FrontendUser user;

    private H2 viewTitle;

    private final AccessAnnotationChecker accessChecker;


    @PostConstruct
    public void init() {
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();

    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.getElement().setAttribute("aria-label", "Menu toggle");

        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent() {
        H1 appName = new H1("Delphi Council Information System");
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        Header header = new Header(appName);

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private AppNav createNavigation() {
        // AppNav is not yet an official component.
        // For documentation, visit https://github.com/vaadin/vcf-nav#readme
        AppNav nav = new AppNav();

        addViewToNavigation(nav, AboutView.class, "la la-file");
        addViewToNavigation(nav, MissionListView.class, "la la-file");
        addViewToNavigation(nav, ImprintView.class, "la la-file");

        return nav;
    }

    private void addViewToNavigation(AppNav nav, @SuppressWarnings("rawtypes") final Class view, @SuppressWarnings("SameParameterValue") final String icon) {
        if (accessChecker.hasAccess(view)) {
            //noinspection unchecked
            nav.addItem(new AppNavItem(getTranslation(view.getSimpleName()), view, icon));
        } else {
            log.debug("User has no access to view. user='{}', roles={}, view={}",
                    user.getName(), user.getRoles(), view.getSimpleName());
        }
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        Avatar avatar = new Avatar(user.getName());


        avatar.setImage(user.getAvatar());
        avatar.setThemeName("xsmall");
        avatar.getElement().setAttribute("tabindex", "-1");

        MenuBar userMenu = new MenuBar();
        userMenu.setThemeName("tertiary-inline contrast");

        MenuItem userName = userMenu.addItem("");
        Div div = new Div();
        div.add(avatar);
        div.add(user.getName());
        div.add(new Icon("lumo", "dropdown"));
        div.getElement().getStyle().set("display", "flex");
        div.getElement().getStyle().set("align-items", "center");
        div.getElement().getStyle().set("gap", "var(--lumo-space-s)");
        userName.add(div);
        userName.getSubMenu().addItem("Roles", e -> {
            log.info("Roles of logged in user. roles={}",
                    String.join(", ", user.getRoles())
            );
            Notification.show(
                    "Roles: " +
                            String.join(", ", user.getRoles())

            );
        });
        userName.getSubMenu().addItem("Sign out", e -> {
            log.info("User logout. event={}", e);
            getUI().ifPresentOrElse(
                    ui -> ui.getPage().setLocation("/logout"),
                    () -> ErrorNotification.showMarkdown("User can't logout. There is no _UI_.")
            );
        });

        //noinspection SpellCheckingInspection
        userName.getSubMenu().getItems().forEach(e -> e.setId("usermenu"));
        layout.add(userMenu);

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
