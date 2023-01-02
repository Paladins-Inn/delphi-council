/*
 * Copyright (c) 2023. Roland T. Lichti
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.paladinsinn.tp.dcis.ui.views.missions;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import de.paladinsinn.torganized.core.missions.Mission;
import de.paladinsinn.tp.dcis.ui.components.users.FrontendUser;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
@AllArgsConstructor
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@ToString(onlyExplicitlyIncluded = true)
@Getter
@Setter
@Slf4j
public class MissionDataForm extends FormLayout {
    @ToString.Include
    private Mission data;
    private final FrontendUser user;

    private final Binder<Mission> binder = new BeanValidationBinder<>(Mission.class);

    @PostConstruct
    public void init() {
        addClassName("mission-basedata");
        binder.bindInstanceFields(this);

        add(createButtonsLayout());
    }

    public void setData(final Mission data) {
        this.data = data;
        binder.readBean(this.data);
    }


    /*
     * Events for save/close/delete
     */
    private Button save, delete, close;

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, data)));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        try {
            binder.writeBean(data);
            fireEvent(new SaveEvent(this, data));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    @Getter
    public static abstract class MissionDataFormEvent extends ComponentEvent<MissionDataForm> {
        private final Mission data;

        /**
         * Creates a new event using the given source and indicator whether the
         * event originated from the client side or the server side.
         *
         * @param source     the source component
         * @param data       the current data
         */
        public MissionDataFormEvent(MissionDataForm source, Mission data) {
            super(source, false);

            this.data = data;
        }
    }

    public static class SaveEvent extends MissionDataFormEvent {
        /**
         * Creates a new event using the given source and indicator whether the
         * event originated from the client side or the server side.
         *
         * @param source     the source component
         * @param data       the current data
         */
        public SaveEvent(MissionDataForm source, Mission data) {
            super(source, data);
        }
    }

    public static class DeleteEvent extends MissionDataFormEvent {
        /**
         * Creates a new event using the given source and indicator whether the
         * event originated from the client side or the server side.
         *
         * @param source     the source component
         * @param data       the current data
         */
        public DeleteEvent(MissionDataForm source, Mission data) {
            super(source, data);
        }
    }

    public static class CloseEvent extends MissionDataFormEvent {
        /**
         * Creates a new event using the given source and indicator whether the
         * event originated from the client side or the server side.
         *
         * @param source     the source component
         */
        public CloseEvent(MissionDataForm source) {
            super(source, source.getData());
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}


