package de.paladinsinn.tp.dcis.ui.views.about;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.paladinsinn.tp.dcis.ui.components.i18n.AutoPageTitle;
import de.paladinsinn.tp.dcis.ui.views.MainLayout;

@Route(value = "impressum", layout = MainLayout.class)
@AnonymousAllowed
public class ImprintView extends VerticalLayout implements AutoPageTitle {

    public ImprintView() {
        setSizeFull();
        setSpacing(false);

        Image img = new Image("/icons/icon.png", "Delphi Council Logo");
        img.setWidth("200px");
        add(img);

        add(new H2("Impressum"));
        add(new Paragraph(""));
        add(new Paragraph("It’s a place where you can grow your own UI 🤗"));

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
        setId("page");
    }

}
