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

package de.paladinsinn.delphicouncil.views.stormknights;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.paladinsinn.delphicouncil.data.operative.Operative;
import de.paladinsinn.delphicouncil.data.operative.OperativeService;
import de.paladinsinn.delphicouncil.views.main.MainView;

@Route(value = "operatives/registration", layout = MainView.class)
@PageTitle("Storm Knight Registration")
@CssImport("./views/stormknightregistration/storm-knight-registration-view.css")
public class StormKnightRegistrationView extends Div {

    private final TextField firstName = new TextField("First name");
    private final TextField lastName = new TextField("Last name");

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final Binder<Operative> binder = new Binder<>(Operative.class);

    public StormKnightRegistrationView(OperativeService personService) {
        addClassName("storm-knight-registration-view");

        add(createTitle());
        add(createFormLayout());
        add(createButtonLayout());

        binder.bindInstanceFields(this);
        clearForm();

        cancel.addClickListener(e -> clearForm());
        save.addClickListener(e -> {
            personService.update(binder.getBean());
            Notification.show(binder.getBean().getClass().getSimpleName() + " details stored.");
            clearForm();
        });
    }

    private void clearForm() {
        binder.setBean(new Operative());
    }

    private Component createTitle() {
        return new H3("Operatives information");
    }

    private Component createFormLayout() {
        FormLayout formLayout = new FormLayout();
        formLayout.add(firstName, lastName);
        return formLayout;
    }

    private Component createButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save);
        buttonLayout.add(cancel);
        return buttonLayout;
    }
}
