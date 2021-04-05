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

package de.paladinsinn.delphicouncil.ui.forms.missions;

import com.sun.istack.NotNull;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.server.VaadinSession;
import de.paladinsinn.delphicouncil.app.events.MissionSaveEvent;
import de.paladinsinn.delphicouncil.app.i18n.TranslatableComponent;
import de.paladinsinn.delphicouncil.data.Clearance;
import de.paladinsinn.delphicouncil.data.missions.Mission;
import de.paladinsinn.delphicouncil.data.missions.MissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Locale;
import java.util.Optional;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

/**
 * MissionEditForm -- Edits/displays the data for a mission.
 *
 * @author klenkes74 {literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-03-29
 */
@Service
@Scope(SCOPE_PROTOTYPE)
public class MissionForm extends Composite<Div> implements LocaleChangeObserver, TranslatableComponent, Serializable, AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(MissionForm.class);
    public static final int DEFAULT_PAYMENT = 250;
    public static final int DEFAULT_XP = 5;

    /** service for writing data to. */
    private final MissionService missionService;

    /** Details of logged in user. */
    private Authentication userDetails;

    /** The locale of the form. */
    private Locale locale;

    private final TextField title = new TextField();
    private final FormLayout form = new FormLayout();
    private final HorizontalLayout actions = new HorizontalLayout();
    private final Button save = new Button();
    private final Button reset = new Button();

    private final TextField id = new TextField();
    private final TextField code = new TextField();
    private final ComboBox<Clearance> clearance = new ComboBox<>();
    private final TextField publication = new TextField();
    private final TextField image = new TextField();
    private final TextArea description = new TextArea();
    private final IntegerField payment = new IntegerField();
    private final IntegerField xp = new IntegerField();
    private final TextArea objectivesSuccess = new TextArea();
    private final TextArea objectivesGood = new TextArea();
    private final TextArea objectivesOutstanding = new TextArea();

    /** mission data */
    private Mission mission;

    /** If the form is read-only. */
    private boolean readonly = true;

    /** If the form is already initialized. */
    private boolean initialized = false;

    @Autowired
    public MissionForm(
            @NotNull final MissionService missionService
    ) {
        this.missionService = missionService;
    }

    @PostConstruct
    public void init() {
        if (mission == null) {
            LOG.debug("CanÄt initialize without having data. form={}", this);
            return;
        }

        if (initialized) {
            LOG.debug("Already initialized. form={}", this);
            return;
        }

        if (locale == null) {
            locale = VaadinSession.getCurrent().getLocale();
        }

        addListener(MissionSaveEvent.class, missionService);

        readUserDetails();

        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("1px", 1),
                new FormLayout.ResponsiveStep("400px", 2),
                new FormLayout.ResponsiveStep("800px", 3)
        );

        save.getStyle().set("marginRight", "10px");
        save.addClickListener(event -> {
            if (mission == null) mission = new Mission();

            mission.setCode(code.getValue());
            mission.setTitle(title.getValue());
            mission.setClearance(clearance.getValue());
            mission.setPublication(publication.getValue());
            mission.setImage(image.getValue());
            mission.setDescription(description.getValue());
            mission.setPayment(payment.getValue());
            mission.setXp(xp.getValue());
            mission.setObjectivesSuccess(objectivesSuccess.getValue());
            mission.setObjectivesGood(objectivesGood.getValue());
            mission.setObjectivesOutstanding(objectivesOutstanding.getValue());

            fireEvent(new MissionSaveEvent(this, false));
        });

        reset.addClickListener(event -> {
            // FIXME 2021-04-04 rlichti implement resetting the form.
        });


        id.setReadOnly(true);

        code.setRequired(true);
        code.setReadOnly(readonly);

        title.setRequired(true);
        title.setReadOnly(readonly);

        clearance.setDataProvider(Clearance.ANY.dataProvider());
        clearance.setAllowCustomValue(false);
        clearance.setRequired(true);
        clearance.setReadOnly(readonly);

        publication.setRequired(false);
        publication.setReadOnly(readonly);

        image.setRequired(false);
        image.setReadOnly(readonly);

        description.addClassName("long-text");
        description.setRequired(true);
        description.setReadOnly(readonly);

        payment.setMin(100);
        payment.setMax(1000);
        payment.setStep(50);
        payment.setValue(DEFAULT_PAYMENT);
        payment.setReadOnly(readonly);

        xp.setMin(1);
        xp.setMax(50);
        xp.setStep(1);
        xp.setValue(DEFAULT_XP);
        xp.setReadOnly(readonly);

        objectivesSuccess.addClassName("long-text");
        objectivesSuccess.setRequired(true);
        objectivesSuccess.setReadOnly(readonly);

        objectivesGood.addClassName("long-text");
        objectivesGood.setRequired(true);
        objectivesGood.setReadOnly(readonly);

        objectivesOutstanding.addClassName("long-text");
        objectivesOutstanding.setRequired(true);
        objectivesOutstanding.setReadOnly(readonly);

        getContent().add(form);

        initialized = true;
    }

    public void setMission(@NotNull Mission mission) {
        if (this.mission != null && this.mission.equals(mission)) {
            LOG.info("Mission report didn't change. Ignoring event. form={}, code={}, id={}",
                    this, this.mission.getCode(), this.mission.getId());

            return;
        }

        LOG.debug("Set mission. id={}, code={}, title={}", mission.getId(), mission.getCode(), mission.getTitle());

        this.mission = mission;

        init();
        populate();
        translate();
    }

    public void populate() {
        if (mission == null) {
            LOG.warn("Tried to populate form data without a mission defined. form={}", this);
            return;
        }

        id.setValue(mission.getId().toString());
        code.setValue(mission.getCode());
        title.setValue(mission.getTitle());
        clearance.setValue(mission.getClearance());
        publication.setValue(mission.getPublication());
        image.setValue(mission.getImage());
        description.setValue(mission.getDescription());
        payment.setValue(mission.getPayment());
        xp.setValue(mission.getXp());
        objectivesSuccess.setValue(mission.getObjectivesSuccess());
        objectivesGood.setValue(mission.getObjectivesGood());
        objectivesOutstanding.setValue(mission.getObjectivesOutstanding());

        calculateReadOnly();
    }

    public void initializeMission() {
        LOG.debug("Creating a new mission. form={}", this);

        mission = new Mission();

        init();
        populate();
        translate();
    }

    public Optional<Mission> getMission() {
        return Optional.ofNullable(mission);
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        LOG.trace("Change locale event. form={}, locale={}", this, event.getLocale());

        setLocale(event.getLocale());
    }

    @Override
    public void translate() {
        if (userDetails == null || mission == null || locale == null) {
            LOG.warn(
                    "Can't build mission form. form={}, user={}, report={}, locale={}",
                    this, userDetails, mission, locale
            );

            return;
        }

        LOG.debug("Building mission edit form. form={}, locale={}, mission={}", this, locale, mission);

        LOG.trace("Removing all form elements. form={}", this);
        form.removeAll();


        // Form fields
        LOG.trace("Translating form elements. form={}", this);
        id.setLabel(getTranslation("input.id.caption"));
        id.setHelperText(getTranslation("input.id.help"));

        code.setLabel(getTranslation("mission.code.caption"));
        code.setHelperText(getTranslation("mission.code.help"));

        title.setLabel(getTranslation("mission.title.caption"));
        title.setHelperText(getTranslation("mission.title.help"));

        clearance.setLabel(getTranslation("torg.clearance.caption"));
        clearance.setHelperText(getTranslation("torg.clearance.help"));
        clearance.setItemLabelGenerator((ItemLabelGenerator<Clearance>) item -> getTranslation("torg.clearance." + item.name()));

        publication.setLabel(getTranslation("mission.publication.caption"));
        publication.setHelperText(getTranslation("mission.publication.help"));

        image.setLabel(getTranslation("mission.image.caption"));
        image.setHelperText(getTranslation("mission.image.help"));

        description.setLabel(getTranslation("mission.description.caption"));
        description.setHelperText(getTranslation("mission.description.help"));

        payment.setLabel(getTranslation("mission.payment.caption"));
        payment.setHelperText(getTranslation("mission.description.help"));

        xp.setLabel(getTranslation("mission.xp.caption"));
        xp.setHeight(getTranslation("mission.xp.help"));

        objectivesSuccess.setLabel(getTranslation("mission.objectives.success.caption"));
        objectivesSuccess.setHelperText(getTranslation("mission.objectives.success.help"));

        objectivesGood.setLabel(getTranslation("mission.objectives.good.caption"));
        objectivesGood.setHelperText(getTranslation("mission.objectives.good.help"));

        objectivesOutstanding.setLabel(getTranslation("mission.objectives.outstanding.caption"));
        objectivesOutstanding.setHelperText(getTranslation("mission.objectives.outstanding.help"));


        LOG.trace("adding all form elements. form={}", this);
        form.add(code, clearance, title, publication, payment, xp, image, description,
                objectivesSuccess, objectivesGood, objectivesOutstanding);

        // Buttons
        if (!readonly) {
            save.setText(getTranslation("buttons.save.caption"));
            reset.setText(getTranslation("buttons.reset.caption"));

            actions.removeAll();
            actions.add(save, reset);
            LOG.trace("Adding action buttons. form={}", this);
            form.add(actions);
        }

        form.setColspan(title, 3);
        form.setColspan(publication, 3);
        form.setColspan(description, 3);
        form.setColspan(objectivesSuccess, 3);
        form.setColspan(objectivesGood, 3);
        form.setColspan(objectivesOutstanding, 3);
    }

    private void readUserDetails() {
        if (userDetails == null) {
            try {
                userDetails = SecurityContextHolder.getContext().getAuthentication();
            } catch (NullPointerException e) {
                LOG.warn("No user is logged in. Can't load user details. view={}", this);
            }
        }

        calculateReadOnly();
    }

    private void calculateReadOnly() {
        readonly = userDetails != null
                && !userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ORGA"))
                && !userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"));
    }


    public void displayNote(@NotNull final String i18nkey, @NotNull final String type, String... parameters) {
        LOG.trace("Displaying notification. i18nKey='{}', type='{}', parameter={}", i18nkey, type, parameters);

        Notification.show(
                getTranslation(i18nkey, getTranslation(type), parameters),
                2000,
                Notification.Position.BOTTOM_STRETCH
        );
    }


    @Override
    public void setLocale(Locale locale) {
        if (this.locale != null && this.locale.equals(locale)) {
            LOG.debug("Locale has not changed. Ignoring event. form={}, locale={}", this, locale);
            return;
        }

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
        form.removeAll();
    }
}
