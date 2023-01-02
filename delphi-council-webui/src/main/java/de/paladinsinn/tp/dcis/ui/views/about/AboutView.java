package de.paladinsinn.tp.dcis.ui.views.about;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.paladinsinn.tp.dcis.ui.components.MarkDownDiv;
import de.paladinsinn.tp.dcis.ui.views.MainLayout;

@Route(value = "about", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@AnonymousAllowed
public class AboutView extends VerticalLayout implements HasDynamicTitle {

    public AboutView() {
        setSizeFull();
        setSpacing(false);

        Image img = new Image("/icons/icon.png", "Delphi Council Logo");
        img.setWidth("200px");
        add(img);

        add(new H2(getTranslation("application.title")));
        MarkDownDiv description = new MarkDownDiv(getTranslation("application.description"));
        description.getStyle().set("text-align", "left");

        add(description);

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
        setId("page");
    }

    @Override
    public String getPageTitle() {
        return getTranslation(AboutView.class.getSimpleName());
    }
}
