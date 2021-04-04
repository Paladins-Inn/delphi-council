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
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.server.VaadinSession;
import de.paladinsinn.delphicouncil.app.events.OperatorReportSaveEvent;
import de.paladinsinn.delphicouncil.app.i18n.TranslatableComponent;
import de.paladinsinn.delphicouncil.data.missions.MissionReport;
import de.paladinsinn.delphicouncil.data.operative.OperativeReport;
import de.paladinsinn.delphicouncil.data.operative.OperativeReportService;
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
 * MissionOperativeReportEditForm -- Edits/displays the data for a mission report on group level.
 *
 * @author klenkes74 {literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-04-04
 */
@Service
@Scope(SCOPE_PROTOTYPE)
public class OperativeReportForm extends Composite<Div> implements LocaleChangeObserver, TranslatableComponent, Serializable, AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(OperativeReportForm.class);

    /** The service for writing data to. */
    private final OperativeReportService reportService;

    /** Details of the logged in user. */
    private Authentication userDetails;

    /** The locale of this form. */
    private Locale locale;

    /** The mission report to edit. */
    private OperativeReport operativeReport;

    /** If the form is read-only. */
    private boolean readonly = true;


    /** If this form is alread initialized. */
    private boolean initialized = false;

    // The form components.
    private final FormLayout form = new FormLayout();
    private final TextField id = new TextField();
    private final TextArea achievements = new TextArea();
    private final TextArea notes = new TextArea();
    private final HorizontalLayout actions = new HorizontalLayout();
    private final Button save = new Button();
    private final Button reset = new Button();


    @Autowired
    public OperativeReportForm(
            @NotNull final OperativeReportService reportService
    ) {
        this.reportService = reportService;
    }


    @PostConstruct
    public void init() {
        if (operativeReport == null) {
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

        addListener(OperatorReportSaveEvent.class, reportService);

        readUserDetails();

        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("1px", 1),
                new FormLayout.ResponsiveStep("400px", 2),
                new FormLayout.ResponsiveStep("800px", 3)
        );

        id.setReadOnly(true);
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
            if (operativeReport == null) operativeReport = new OperativeReport();

            operativeReport.setAchievements(achievements.getValue());
            operativeReport.setNotes(notes.getValue());

            getEventBus().fireEvent(new OperatorReportSaveEvent(this, false));
        });

        reset.addClickListener(event -> {
            // FIXME 2021-04-04 rlichti implement resetting the data.
        });

        getContent().add(form);

        // mark as initialized.
        initialized = true;
    }

    public void setReport(@NotNull OperativeReport operativeReport) {
        if (this.operativeReport != null && this.operativeReport.equals(operativeReport)) {
            LOG.info("Mission report didn't change. Ignoring event. form={}, code={}, id={}, operative={}", this,
                    this.operativeReport.getReport().getMission().getCode(), this.operativeReport.getOperative().getId(),
                    this.operativeReport.getOperative().getName());

            return;
        }

        LOG.debug("Set mission report. id={}, code={}, date={}, operative={}",
                operativeReport.getId(), operativeReport.getReport().getMission().getCode(),
                operativeReport.getReport().getDate(), operativeReport.getOperative().getName());

        this.operativeReport = operativeReport;

        init();
        populate();
        translate();
    }

    private void populate() {
        if (operativeReport == null) {
            LOG.warn("Tried to polulate form data without a mission report defined. form={}", this);
            return;
        }

        id.setValue(operativeReport.getId().toString());
        achievements.setValue(operativeReport.getAchievements());
        notes.setValue(operativeReport.getNotes());

        calculateReadOnly();
    }

    public void initializeReport(
            @NotNull MissionReport mission
    ) {
        LOG.debug("Creating a new mission. code={}, gm={}",
                mission.getMission().getCode(), mission.getGameMaster().getUsername());

        operativeReport = new OperativeReport();
        operativeReport.setMissionReport(mission);

        init();
        populate();
        translate();
    }


    public Optional<OperativeReport> getReport() {
        return Optional.ofNullable(operativeReport);
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
                && !(operativeReport != null && userDetails.getName().equals(operativeReport.getReport().getGameMaster().getUsername()));
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        LOG.trace("Change locale event. form={}, locale={}", this, event.getLocale());

        setLocale(event.getLocale());
    }

    @Override
    public void translate() {
        if (userDetails == null || operativeReport == null || locale == null) {
            LOG.warn(
                    "Can't build mission report group report editor due to mission data. form={}, user={}, report={}, locale={}",
                    this, userDetails, operativeReport, locale
            );

            return;
        }

        LOG.debug("Building mission report group edit form. form={}, report={}, locale={}", this, operativeReport, locale);

        LOG.trace("Remove all form elements. form={}", this);
        form.removeAll();

        // Form fields
        id.setTitle(getTranslation("missionreport.id.caption"));
        id.setHelperText(getTranslation("missionreport.id.help"));

        achievements.setLabel(getTranslation("missionreport.achievements.caption"));
        achievements.setHelperText(getTranslation("missionreport.achievements.help"));

        notes.setLabel(getTranslation("missionreport.notes.caption"));
        notes.setHelperText(getTranslation("missionreport.notes.help"));


        LOG.trace("Adding all form elements. form={}", this);
        form.addFormItem(id, getTranslation("missionreport.id.caption"));
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

    public void displayNote(@NotNull final String i18nkey, @NotNull final String type, Object... parameters) {
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
