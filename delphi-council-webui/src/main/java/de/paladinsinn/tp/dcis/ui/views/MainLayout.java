package de.paladinsinn.tp.dcis.ui.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import de.kaiserpfalzedv.commons.core.libravatar.AvatarUri;
import de.kaiserpfalzedv.commons.core.libravatar.LibravatarDefaultImage;
import de.paladinsinn.tp.dcis.ui.components.appnav.AppNav;
import de.paladinsinn.tp.dcis.ui.components.appnav.AppNavItem;
import de.paladinsinn.tp.dcis.ui.components.notifications.ErrorNotification;
import de.paladinsinn.tp.dcis.ui.components.users.FrontendUser;
import de.paladinsinn.tp.dcis.ui.views.about.AboutView;
import de.paladinsinn.tp.dcis.ui.views.helloworld.HelloWorldView;
import io.quarkus.security.identity.SecurityIdentity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * The main view is a top-level placeholder for other views.
 */
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@AnonymousAllowed
public class MainLayout extends AppLayout implements RouterLayout {

    @Inject
    FrontendUser user;

    private final AboutView aboutView;
    private final HelloWorldView helloWorldView;

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

        if (accessChecker.hasAccess(AboutView.class)) {
            nav.addItem(new AppNavItem("About", AboutView.class, "la la-file"));
        }

        AppNavItem missions = new AppNavItem("Missions", AboutView.class, "la la-file");
        missions.addItem(new AppNavItem("Open", AboutView.class, "la la-file"));
        missions.addItem(new AppNavItem("Reports", AboutView.class, "la la-file"));
        nav.addItem(missions);

        nav.addItem(new AppNavItem("Storm Knights", AboutView.class, "la la-file"));

        return nav;
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
                    user.getRoles().stream().collect(Collectors.joining(", "))
            );
            Notification.show(
                    "Roles: " +
                            user.getRoles().stream().collect(Collectors.joining(", "))

            );
        });
        userName.getSubMenu().addItem("Sign out", e -> {
            log.info("User logout. event={}", e);
            getUI().ifPresentOrElse(
                    ui -> ui.getPage().setLocation("/logout"),
                    () -> {
                        ErrorNotification.showMarkdown("User can't logout. There is no _UI_.");
                    }
            );
        });

        userName.getSubMenu().getItems().forEach(e -> {
            e.setId("usermenu");
        });
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
