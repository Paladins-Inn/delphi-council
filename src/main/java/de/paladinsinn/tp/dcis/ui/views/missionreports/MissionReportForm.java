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

package de.paladinsinn.tp.dcis.ui.views.missionreports;

import com.sun.istack.NotNull;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.server.VaadinSession;
import de.paladinsinn.tp.dcis.data.missions.MissionReport;
import de.paladinsinn.tp.dcis.data.missions.SuccessState;
import de.paladinsinn.tp.dcis.data.person.PersonRepository;
import de.paladinsinn.tp.dcis.security.LoggedInUser;
import de.paladinsinn.tp.dcis.ui.components.PersonSelector;
import de.paladinsinn.tp.dcis.ui.components.TorgActionBar;
import de.paladinsinn.tp.dcis.ui.i18n.TranslatableComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

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

    /**
     * The service for writing data to.
     */
    private final MissionReportService reportService;

    /**
     * The service for reading the gm from.
     */
    private final PersonRepository personRepository;

    /**
     * The locale of this form.
     */
    private Locale locale;

    /**
     * The mission report to edit.
     */
    private MissionReport data;
    private MissionReport oldData;

    private final LoggedInUser user;


    /**
     * If this form is alread initialized.
     */
    private boolean initialized = false;

    // The form components.
    private final FormLayout form = new FormLayout();
    private final TextField id = new TextField();
    private final DatePicker missionDate = new DatePicker();
    private final ComboBox<SuccessState> successState = new ComboBox<>();
    private PersonSelector gm;
    private final TextArea achievements = new TextArea();
    private final TextArea notes = new TextArea();

    private TorgActionBar actions;


    @Autowired
    public MissionReportForm(
            @NotNull final MissionReportService reportService,
            @NotNull final PersonRepository personRepository,
            @NotNull final LoggedInUser user) {
        this.reportService = reportService;
        this.personRepository = personRepository;
        this.user = user;
    }


    private void init() {
        if (initialized || data == null || user == null) {
            LOG.debug(
                    "Already initialized or unable to initialize. initialized={}, data={}, user={}",
                    initialized, data, user
            );
            return;
        }

        if (locale == null) {
            locale = VaadinSession.getCurrent().getLocale();
        }

        addListener(MissionReportSaveEvent.class, reportService);

        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("1px", 1),
                new FormLayout.ResponsiveStep("400px", 2),
                new FormLayout.ResponsiveStep("800px", 3)
        );

        actions = new TorgActionBar(
                "buttons",
                event -> {
                    scrape();
                    getEventBus().fireEvent(new MissionReportSaveEvent(this, false));
                },
                event -> {
                    LOG.info("Resetting data from: displayed={}, new={}", data, oldData);
                    resetData();
                },
                null,
                null
        );
        actions.setReadOnly(
                user.isReadonly()
                        && !user.getPerson().equals(data.getGameMaster())
                        && !user.isOrga()
                        && !user.isAdmin()
                        && !user.isJudge()
        );

        id.setReadOnly(true);

        missionDate.setRequired(true);
        missionDate.setRequiredIndicatorVisible(true);
        missionDate.setClearButtonVisible(true);
        missionDate.setReadOnly(user.isReadonly());
        // TODO 2021-04-04 rlichti Add the min date with rule: either current date or current mission report saved date (whichever is an earlier date)

        successState.setAllowCustomValue(false);
        successState.setRequired(true);
        successState.setRequiredIndicatorVisible(true);
        successState.setReadOnly(user.isReadonly());
        successState.setDataProvider(SuccessState.FAILED.dataProvider());

        achievements.addClassName("long-text");
        achievements.setClearButtonVisible(true);
        achievements.setMaxLength(1000);
        achievements.setReadOnly(user.isReadonly());

        notes.addClassName("long-text");
        notes.setClearButtonVisible(true);
        notes.setMaxLength(1000);
        notes.setReadOnly(user.isReadonly());

        gm = new PersonSelector("missionreport.gm", personRepository, user);
        gm.setReadOnly(!user.isAdmin() && !user.isOrga() && !user.isJudge());

        getContent().add(form);

        // mark as initialized.
        initialized = true;
    }

    private void scrape() {
        if (data == null) data = new MissionReport();

        data.setDate(missionDate.getValue());
        data.setObjectivesMet(successState.getValue());
        data.setAchievements(achievements.getValue());
        data.setNotes(notes.getValue());
        data.setGameMaster(gm.getValue());
    }

    public void resetData() {
        setData(oldData);
    }

    public void setData(@NotNull final MissionReport data) {
        if (this.data != null && this.data.equals(data)) {
            LOG.info("Mission report didn't change. Ignoring event. code={}, id={}, gm={}",
                    this.data.getMission().getCode(), this.data.getId(),
                    this.data.getGameMaster().getUsername());

            return;
        }

        LOG.debug("Set mission report. id={}, code={}, date={}, gm={}",
                data.getId(), data.getMission().getCode(),
                data.getDate(), data.getGameMaster().getName());

        try {
            this.oldData = data.clone();
        } catch (CloneNotSupportedException e) {
            LOG.warn("Could not clone the data. Reset won't work. data={}", data);
            this.oldData = data;
        }
        this.data = data;

        init();
        populate();
        translate();
    }

    private void populate() {
        if (data == null) {
            LOG.warn("Tried to polulate form data without a mission report defined.");
            return;
        }

        if (data.getId() != null) {
            id.setValue(data.getId().toString());
        }

        bindData(data.getDate(), missionDate);
        bindData(data.getObjectivesMet(), successState);
        bindData(data.getAchievements(), achievements);
        bindData(data.getNotes(), notes);
        bindData(data.getGameMaster(), gm);
    }


    @SuppressWarnings({"rawtypes", "unchecked"})
    private void bindData(final Object data, @NotNull HasValue component) {
        if (data != null) {
            component.setValue(data);
        }
    }


    public Optional<MissionReport> getData() {
        return Optional.ofNullable(data);
    }

    @Override
    public void fireEvent(@NotNull ComponentEvent<?> event) {
        LOG.trace("Event to fire. event={}", event);

        getEventBus().fireEvent(event);
    }

    @Override
    public void translate() {
        if (user == null || data == null || locale == null) {
            LOG.warn(
                    "Can't build mission report group report editor due to mission data. user={}, data={}, locale={}",
                    user, data, locale
            );

            return;
        }

        LOG.debug("Building mission report group edit form. data={}, locale={}", data, locale);

        LOG.trace("Remove all form elements.");
        form.removeAll();

        // Form fields
        id.setLabel(getTranslation("input.id.caption"));
        id.setHelperText(getTranslation("input.id.help"));

        missionDate.setLabel(getTranslation("missionreport.date.caption"));
        missionDate.setHelperText(getTranslation("missionreport.date.help"));

        successState.setLabel(getTranslation("missionreport.success.caption"));

        achievements.setLabel(getTranslation("missionreport.achievements.caption"));
        achievements.setHelperText(getTranslation("missionreport.achievements.help"));

        notes.setLabel(getTranslation("missionreport.notes.caption"));
        notes.setHelperText(getTranslation("missionreport.notes.help"));

        gm.setLocale(locale);

        LOG.trace("Adding all form elements.");
        form.add(gm, missionDate, successState, achievements, notes);

        actions.setLocale(locale);
        form.add(actions);

        form.setColspan(achievements, 3);
        form.setColspan(notes, 3);
        form.setColspan(actions, 3);
    }


    @Override
    public void localeChange(LocaleChangeEvent event) {
        LOG.trace("Change locale event. locale={}", event.getLocale());

        setLocale(event.getLocale());
    }

    @Override
    public void setLocale(Locale locale) {
        if (this.locale != null && this.locale.equals(locale)) {
            LOG.debug("Locale has not changed. Ignoring event. locale={}", this.locale.getDisplayLanguage());
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
        LOG.debug("Closing form.");
        getContent().removeAll();
        form.removeAll();
    }
}
