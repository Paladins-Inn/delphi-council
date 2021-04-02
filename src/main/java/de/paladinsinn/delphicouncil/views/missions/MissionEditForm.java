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

package de.paladinsinn.delphicouncil.views.missions;

import com.sun.istack.NotNull;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.BindingValidationStatus;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.converter.StringToUuidConverter;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import de.paladinsinn.delphicouncil.app.i18n.TranslatableComponent;
import de.paladinsinn.delphicouncil.data.Clearance;
import de.paladinsinn.delphicouncil.data.missions.Mission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.vaadin.flow.component.Unit.PIXELS;
import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

/**
 * MissionEditForm -- Edits/displays the data for a mission.
 *
 * @author klenkes74 {literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-03-29
 */
@Service
@Scope(SCOPE_PROTOTYPE)
public class MissionEditForm extends Composite<Div> implements LocaleChangeObserver, TranslatableComponent, Serializable, AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(MissionEditForm.class);
    public static final int DEFAULT_PAYMENT = 250;

    private Locale locale;

    /** Binder for data */
    private Binder<Mission> binder;
    private Mission mission;


    @PostConstruct
    public void init() {
        LOG.debug("Loading form. form={}", this);

        binder = new Binder<>(Mission.class);

        getContent().setMinWidth(400, PIXELS);
        getContent().setMinHeight(500, PIXELS);
    }

    public void setMission(@NotNull Mission mission) {
        LOG.debug("Set mission. id={}, title={}", mission.getId(), mission.getTitle());

        this.mission = mission;
        binder.readBean(this.mission);
    }

    public Mission getMission() {
        return mission;
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        LOG.trace("Change locale event. form={}, locale={}", this, event.getLocale());

        setLocale(event.getLocale());
        translate();
    }

    @Override
    public void translate() {
        LOG.debug("Building mission edit form. form={}, locale={}, mission={}", this, locale, binder.getBean());

        Authentication userDetails = null;
        try {
            userDetails = SecurityContextHolder.getContext()
                    .getAuthentication();
        } catch (NullPointerException e) {
            LOG.warn("No user is logged in. Can't load user details. view={}", this);
        }
        LOG.trace(
                "Rebuilding form. form={}, user={}, locale={}",
                this,
                userDetails != null ? userDetails.getName() : "-not set-",
                locale
        );

        getContent().removeAll();

        LOG.trace("Loading mission data. mission={}", mission);
        binder.readBean(mission);

        FormLayout form = new FormLayout();
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("1px", 1),
                new FormLayout.ResponsiveStep("400px", 2),
                new FormLayout.ResponsiveStep("800px", 3)
        );

        // Info label and buttons
        HorizontalLayout actions = new HorizontalLayout();
        // Buttons
        Button save = new Button(getTranslation("buttons.save.caption"));
        save.getStyle().set("marginRight", "10px");
        Button reset = new Button(getTranslation("buttons.reset.caption"));

        // Only add the buttons when user is able to edit the data.
        if (!isReadOnly(userDetails)) {
            actions.add(save, reset);
        }

        save.addClickListener(event -> {
            if (! binder.writeBeanIfValid(mission)) {
                BinderValidationStatus<Mission> validate = binder.validate();
                String error = validate.getFieldValidationStatuses()
                        .stream().filter(BindingValidationStatus::isError)
                        .map(BindingValidationStatus::getMessage)
                        .map(Optional::get).distinct()
                        .collect(Collectors.joining(", "));

                Notification.show(
                        getTranslation("input.validation.errors", error),
                        60,
                        Notification.Position.BOTTOM_STRETCH
                );
            }
        });

        reset.addClickListener(event -> {
            binder.readBean(mission);
            Notification.show(
                    getTranslation("input.reset.text"),
                    60,
                    Notification.Position.BOTTOM_STRETCH
            );
        });

        // Form fields
        TextField id = new TextField();
        id.setTitle(getTranslation("mission.id.caption"));
        id.setHelperText(getTranslation("mission.id.help"));
        id.setReadOnly(true);
        binder.forField(id)
                .withNullRepresentation("")
                .withConverter(new StringToUuidConverter(getTranslation("input.validate.error.uuid")))
                .bind(Mission::getId, null);

        TextField code = new TextField();
        code.setTitle(getTranslation("mission.code.caption"));
        code.setHelperText(getTranslation("mission.code.help"));
        code.setRequired(true);
        setAccess(userDetails, code);
        binder.forField(code)
                .withNullRepresentation("")
                .bind("code");

        TextField title = new TextField();
        title.setTitle(getTranslation("mission.title.caption"));
        title.setHelperText(getTranslation("mission.title.help"));
        title.setRequired(true);
        setAccess(userDetails, title);
        binder.forField(title)
                .withNullRepresentation("")
                .bind("title");

        ComboBox<Clearance> clearance = new ComboBox<>();
        clearance.setDataProvider(Clearance.ANY.dataProvider());
        clearance.setLabel(getTranslation("torg.clearance.caption"));
        clearance.setHelperText(getTranslation("torg.clearance.help"));
        clearance.setAllowCustomValue(false);
        clearance.setRequired(true);
        setAccess(userDetails, clearance);
        binder.forField(clearance)
                .withNullRepresentation(Clearance.ANY)
                .bind("clearance");

        TextField publication = new TextField();
        publication.setTitle(getTranslation("mission.publication.caption"));
        publication.setHelperText(getTranslation("mission.publication.help"));
        publication.setRequired(false);
        setAccess(userDetails, publication);
        binder.forField(publication)
                .withNullRepresentation("")
                .bind("publication");

        TextField image = new TextField();
        image.setTitle(getTranslation("mission.image.caption"));
        image.setHelperText(getTranslation("mission.image.help"));
        image.setRequired(false);
        setAccess(userDetails, image);
        binder.forField(image)
                .withNullRepresentation("")
                .bind("image");

        TextArea description = new TextArea();
        description.addClassName("long-text");
        description.setLabel(getTranslation("mission.description.caption"));
        description.setHelperText(getTranslation("mission.description.help"));
        description.setRequired(true);
        setAccess(userDetails, description);
        binder.forField(description)
                .withNullRepresentation("")
                .bind("description");

        IntegerField payment = new IntegerField();
        payment.setLabel(getTranslation("mission.payment.caption"));
        payment.setHelperText(getTranslation("mission.description.help"));
        payment.setMin(100);
        payment.setMax(1000);
        payment.setStep(50);
        setAccess(userDetails, payment);
        binder.forField(payment)
                .withNullRepresentation(DEFAULT_PAYMENT)
                .bind(Mission::getPayment, Mission::setPayment);

        TextArea objectivesSuccess = new TextArea();
        objectivesSuccess.addClassName("long-text");
        objectivesSuccess.setLabel(getTranslation("mission.objectives.success.caption"));
        objectivesSuccess.setHelperText(getTranslation("mission.objectives.success.help"));
        objectivesSuccess.setRequired(true);
        setAccess(userDetails, objectivesSuccess);
        binder.forField(objectivesSuccess)
                .withNullRepresentation("")
                .bind("objectivesSuccess");

        TextArea objectivesGood = new TextArea();
        objectivesGood.addClassName("long-text");
        objectivesGood.setLabel(getTranslation("mission.objectives.good.caption"));
        objectivesGood.setHelperText(getTranslation("mission.objectives.good.help"));
        objectivesGood.setRequired(true);
        setAccess(userDetails, objectivesGood);
        binder.forField(objectivesGood)
                .withNullRepresentation("")
                .bind("objectivesGood");

        TextArea objectivesOutstanding = new TextArea();
        objectivesOutstanding.addClassName("long-text");
        objectivesOutstanding.setLabel(getTranslation("mission.objectives.outstanding.caption"));
        objectivesOutstanding.setHelperText(getTranslation("mission.objectives.outstanding.help"));
        objectivesOutstanding.setRequired(true);
        setAccess(userDetails, objectivesOutstanding);
        binder.forField(objectivesOutstanding)
                .withNullRepresentation("")
                .bind("objectivesOutstanding");

        binder.readBean(mission);

        form.addFormItem(id, getTranslation("mission.id.caption"));
        form.addFormItem(code, getTranslation("mission.code.caption"));
        form.addFormItem(clearance, getTranslation("torg.clearance.caption"));
        form.addFormItem(title, getTranslation("mission.title.caption"));
        form.addFormItem(publication, getTranslation("mission.publication.caption"));
        form.addFormItem(payment, getTranslation("mission.payment.caption"));
        form.addFormItem(image, getTranslation("mission.image.caption"));
        form.addFormItem(description, getTranslation("mission.description.caption"));
        form.addFormItem(objectivesSuccess, getTranslation("mission.objectives.success.caption"));
        form.addFormItem(objectivesGood, getTranslation("mission.objectives.good.caption"));
        form.addFormItem(objectivesOutstanding, getTranslation("mission.objectives.outstanding.caption"));
        form.add(actions);

        form.setColspan(title, 3);
        form.setColspan(publication, 3);
        form.setColspan(description, 3);
        form.setColspan(objectivesSuccess, 3);
        form.setColspan(objectivesGood, 3);
        form.setColspan(objectivesOutstanding, 3);

        getContent().add(form);
    }

    /**
     * Sets the component read-only if the calling user has not the ROLE ORGA or ADMIN.
     *
     * @param component Component to be en/disabled depending on the calling user.
     */
    private void setAccess(Authentication userDetails, HasValue<?,?> component) {
        component.setReadOnly(isReadOnly(userDetails));
    }

    private boolean isReadOnly(Authentication userDetails) {
        return userDetails != null
                && !userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ORGA"))
                && !userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"));
    }

    @Override
    public void setLocale(Locale locale) {
        this.locale = locale;

        translate();
    }

    private String getTranslation(@NotNull final String key) {
        try {
            return super.getTranslation(key);
        } catch (NullPointerException e) {
            LOG.warn("Can't call translator from vaadin: {}", e.getMessage());
            return "!" + key;
        }
    }

    @Override
    public void close() throws Exception {
        LOG.debug("Closing form. form={}", this);
        getContent().removeAll();
    }
}
