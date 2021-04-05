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
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.server.VaadinSession;
import de.paladinsinn.delphicouncil.app.events.MissionGroupReportSaveEvent;
import de.paladinsinn.delphicouncil.app.i18n.I18nDatePicker;
import de.paladinsinn.delphicouncil.app.i18n.TranslatableComponent;
import de.paladinsinn.delphicouncil.app.i18n.Translator;
import de.paladinsinn.delphicouncil.data.missions.Mission;
import de.paladinsinn.delphicouncil.data.missions.MissionReport;
import de.paladinsinn.delphicouncil.data.missions.MissionReportService;
import de.paladinsinn.delphicouncil.data.missions.SuccessState;
import de.paladinsinn.delphicouncil.data.person.Person;
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
 * MissionGroupReportEditForm -- Edits/displays the data for a mission report on group level.
 *
 * @author klenkes74 {literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-03-29
 */
@Service
@Scope(SCOPE_PROTOTYPE)
public class MissionReportForm extends Composite<Div> implements LocaleChangeObserver, TranslatableComponent, Serializable, AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(MissionReportForm.class);

    /** Tanslator (needed for the datepicker). */
    private final Translator translator;

    /** The service for writing data to. */
    private final MissionReportService reportService;

    /** Details of the logged in user. */
    private Authentication userDetails;

    /** The locale of this form. */
    private Locale locale;

    /** The mission report to edit. */
    private MissionReport missionReport;

    /** If the form is read-only. */
    private boolean readonly = true;


    /** If this form is alread initialized. */
    private boolean initialized = false;

    // The form components.
    private final FormLayout form = new FormLayout();
    private final TextField id = new TextField();
    private final DatePicker missionDate = new DatePicker();
    private final ComboBox<SuccessState> successState = new ComboBox<>();
    private final TextArea achievements = new TextArea();
    private final TextArea notes = new TextArea();
    private final HorizontalLayout actions = new HorizontalLayout();
    private final Button save = new Button();
    private final Button reset = new Button();


    @Autowired
    public MissionReportForm(
            @NotNull final Translator translator,
            @NotNull final MissionReportService reportService
    ) {
        this.translator = translator;
        this.reportService = reportService;
    }


    @PostConstruct
    public void init() {
        if (missionReport == null) {
            LOG.debug("Can't initialize without having a mission. form={}", this);
            return;
        }

        if (initialized) {
            LOG.debug("Already initialized. form={}", this);
            return;
        }

        if (locale == null) {
            locale = VaadinSession.getCurrent().getLocale();
        }

        addListener(MissionGroupReportSaveEvent.class, reportService);

        readUserDetails();

        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("1px", 1),
                new FormLayout.ResponsiveStep("400px", 2),
                new FormLayout.ResponsiveStep("800px", 3)
        );

        id.setReadOnly(true);
        missionDate.setRequired(true);
        missionDate.setClearButtonVisible(true);
        missionDate.setReadOnly(readonly);
        // TODO 2021-04-04 rlichti Add the min date with rule: either current date or current mission report saved date (whichever is an earlier date)
        successState.setAllowCustomValue(false);
        successState.setRequired(true);
        successState.setReadOnly(readonly);
        successState.setDataProvider(SuccessState.FAILED.dataProvider());
        achievements.addClassName("long-text");
        achievements.setClearButtonVisible(true);
        achievements.setMaxLength(1000);
        achievements.setReadOnly(readonly);
        notes.addClassName("long-text");
        notes.setClearButtonVisible(true);
        notes.setMaxLength(1000);
        notes.setReadOnly(readonly);

        save.getStyle().set("marginRight", "10px");
        save.addClickListener(event -> {
            if (missionReport == null) missionReport = new MissionReport();

            missionReport.setDate(missionDate.getValue());
            missionReport.setObjectivesMet(successState.getValue());
            missionReport.setAchievements(achievements.getValue());
            missionReport.setNotes(notes.getValue());

            getEventBus().fireEvent(new MissionGroupReportSaveEvent(this, false));
        });

        reset.addClickListener(event -> {
            // FIXME 2021-04-04 rlichti implement resetting the data.
        });

        getContent().add(form);

        // mark as initialized.
        initialized = true;
    }

    public void setMissionReport(@NotNull MissionReport missionReport) {
        if (this.missionReport != null && this.missionReport.equals(missionReport)) {
            LOG.info("Mission report didn't change. Ignoring event. form={}, code={}, id={}, gm={}", this,
                    this.missionReport.getMission().getCode(), this.missionReport.getId(),
                    this.missionReport.getGameMaster().getUsername());

            return;
        }

        LOG.debug("Set mission report. id={}, code={}, date={}, gm={}",
                missionReport.getId(), missionReport.getMission().getCode(),
                missionReport.getDate(), missionReport.getGameMaster().getName());


        this.missionReport = missionReport;

        init();
        populate();
        translate();
    }

    private void populate() {
        if (missionReport == null) {
            LOG.warn("Tried to polulate form data without a mission report defined. form={}", this);
            return;
        }

        id.setValue(missionReport.getId().toString());
        missionDate.setValue(missionReport.getDate());
        successState.setValue(missionReport.getObjectivesMet());
        achievements.setValue(missionReport.getAchievements());
        notes.setValue(missionReport.getNotes());

        calculateReadOnly();
    }

    public void initializeMissionReport(
            @NotNull Mission mission,
            @NotNull Person gameMaster
    ) {
        LOG.debug("Creating a new mission. code={}, gm={}", mission.getCode(), gameMaster.getUsername());

        missionReport = new MissionReport();
        missionReport.setMission(mission);
        missionReport.setGameMaster(gameMaster);

        init();
        populate();
        translate();
    }


    public Optional<MissionReport> getMissionReport() {
        return Optional.ofNullable(missionReport);
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
                && !userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))
                && !(missionReport != null && userDetails.getName().equals(missionReport.getGameMaster().getUsername()));
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        LOG.trace("Change locale event. form={}, locale={}", this, event.getLocale());

        setLocale(event.getLocale());
    }

    @Override
    public void translate() {
        if (userDetails == null || missionReport == null || locale == null) {
            LOG.warn(
                    "Can't build mission report group report editor due to mission data. form={}, user={}, report={}, locale={}",
                    this, userDetails, missionReport, locale
            );

            return;
        }

        LOG.debug("Building mission report group edit form. form={}, report={}, locale={}", this, missionReport, locale);

        LOG.trace("Remove all form elements. form={}", this);
        form.removeAll();

        // Form fields
        id.setTitle(getTranslation("input.id.caption"));
        id.setHelperText(getTranslation("input.id.help"));

        missionDate.setI18n(new I18nDatePicker(getLocale(), translator));
        missionDate.setLocale(getLocale());
        missionDate.setLabel(getTranslation("missionreport.date.caption"));
        missionDate.setHelperText(getTranslation("missionreport.date.help"));

        successState.setLabel(getTranslation("missionreport.success.caption"));
        successState.setHelperText(getTranslation("missionreport.success.help"));

        achievements.setLabel(getTranslation("missionreport.achievements.caption"));
        achievements.setHelperText(getTranslation("missionreport.achievements.help"));

        notes.setLabel(getTranslation("missionreport.notes.caption"));
        notes.setHelperText(getTranslation("missionreport.notes.help"));


        LOG.trace("Adding all form elements. form={}", this);
        form.addFormItem(id, getTranslation("input.id.caption"));
        form.addFormItem(missionDate, getTranslation("missionreport.datepicker.caption"));
        form.addFormItem(successState, getTranslation("missionreport.success.caption"));
        form.addFormItem(achievements, getTranslation("missionreport.achievements.caption"));
        form.addFormItem(notes, getTranslation("missionreport.notes.caption"));

        // Buttons
        if (!readonly) {
            save.setText(getTranslation("buttons.save.caption"));
            reset.setText(getTranslation("buttons.reset.caption"));

            actions.removeAll();
            actions.add(save, reset);
            LOG.trace("Adding action buttons. form={}", this);
            form.add(actions);
        }

        form.setColspan(achievements, 3);
        form.setColspan(notes, 3);
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
