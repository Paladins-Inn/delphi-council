package de.paladinsinn.tp.dcis.ui.views.about;

import com.vaadin.flow.component.html.*;
import com.vaadin.flow.router.RouteAlias;
import de.kaiserpfalzedv.commons.core.text.MarkdownConverter;
import de.paladinsinn.tp.dcis.ui.components.MarkDownDiv;
import de.paladinsinn.tp.dcis.ui.views.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@PageTitle("About")
@Route(value = "about", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@AnonymousAllowed
public class AboutView extends VerticalLayout {

    public AboutView() {
        setSizeFull();
        setSpacing(false);

        Image img = new Image("/icons/icon.png", "Delphi Council Logo");
        img.setWidth("200px");
        add(img);

        add(new H2("Delphi Council Information System"));
        add(new MarkDownDiv("Test-String _mit_ **MarkDown**."));

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
        setId("page");
    }

}
