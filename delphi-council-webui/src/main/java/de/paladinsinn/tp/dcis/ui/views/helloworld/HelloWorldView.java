package de.paladinsinn.tp.dcis.ui.views.helloworld;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.quarkus.annotation.VaadinSessionScoped;
import de.kaiserpfalzedv.commons.core.text.MarkdownConverter;
import de.paladinsinn.tp.dcis.ui.views.MainLayout;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import io.quarkus.security.identity.SecurityIdentity;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import java.util.stream.Collectors;

@VaadinSessionScoped
@PageTitle("Hello World")
@Route(value = "hello", layout = MainLayout.class)
@PermitAll
public class HelloWorldView extends VerticalLayout {
    private HorizontalLayout line;
    private TextField name;
    private Button sayHello;

    public HelloWorldView() {

        name = new TextField("Your name");
        sayHello = new Button("Say hello");
        sayHello.addClickListener(e -> {
            Notification.show("Hello " + name.getValue());
        });
        sayHello.addClickShortcut(Key.ENTER);

        line = new HorizontalLayout();
        line.setMargin(true);
        line.setVerticalComponentAlignment(Alignment.END, name, sayHello);
        line.add(name, sayHello);

        setId("page");
        setSizeFull();
        setMargin(false);
        setSpacing(false);
        add(line);
    }

}
