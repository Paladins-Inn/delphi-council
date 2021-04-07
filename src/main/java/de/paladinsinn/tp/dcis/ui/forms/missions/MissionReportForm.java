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

package de.paladinsinn.tp.dcis.ui.forms.missions;

import com.sun.istack.NotNull;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.server.VaadinSession;
import de.paladinsinn.tp.dcis.data.missions.MissionReport;
import de.paladinsinn.tp.dcis.data.missions.MissionReportService;
import de.paladinsinn.tp.dcis.data.missions.SuccessState;
import de.paladinsinn.tp.dcis.data.person.Person;
import de.paladinsinn.tp.dcis.data.person.PersonService;
import de.paladinsinn.tp.dcis.events.MissionGroupReportSaveEvent;
import de.paladinsinn.tp.dcis.security.LoggedInUser;
import de.paladinsinn.tp.dcis.ui.i18n.TranslatableComponent;
import de.paladinsinn.tp.dcis.ui.i18n.Translator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    /**
     * Tanslator (needed for the datepicker).
     */
    private final Translator translator;

    /**
     * The service for writing data to.
     */
    private final MissionReportService reportService;

    /**
     * The service for reading the gm from.
     */
    private final PersonService personService;

    /**
     * The locale of this form.
     */
    private Locale locale;

    /**
     * The mission report to edit.
     */
    private MissionReport data;

    @Autowired
    private LoggedInUser user;


    /**
     * If this form is alread initialized.
     */
    private boolean initialized = false;

    // The form components.
    private final FormLayout form = new FormLayout();
    private final TextField id = new TextField();
    private final DatePicker missionDate = new DatePicker();
    private final ComboBox<SuccessState> successState = new ComboBox<>();
    private final ComboBox<Person> gm = new ComboBox<>();
    private final TextArea achievements = new TextArea();
    private final TextArea notes = new TextArea();
    private final HorizontalLayout actions = new HorizontalLayout();
    private final Button save = new Button();
    private final Button reset = new Button();


    @Autowired
    public MissionReportForm(
            @NotNull final Translator translator,
            @NotNull final MissionReportService reportService,
            @NotNull final PersonService personService
    ) {
        this.translator = translator;
        this.reportService = reportService;
        this.personService = personService;
    }


    @PostConstruct
    public void init() {
        if (data == null) {
            LOG.debug("Can't initialize without having a mission.");
            return;
        }

        if (initialized) {
            LOG.debug("Already initialized.");
            return;
        }

        if (locale == null) {
            locale = VaadinSession.getCurrent().getLocale();
        }

        addListener(MissionGroupReportSaveEvent.class, reportService);

        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("1px", 1),
                new FormLayout.ResponsiveStep("400px", 2),
                new FormLayout.ResponsiveStep("800px", 3)
        );

        id.setReadOnly(true);
        missionDate.setRequired(true);
        missionDate.setClearButtonVisible(true);
        missionDate.setReadOnly(user.isReadonly());
        // TODO 2021-04-04 rlichti Add the min date with rule: either current date or current mission report saved date (whichever is an earlier date)
        successState.setAllowCustomValue(false);
        successState.setRequired(true);
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

        gm.setReadOnly(!user.isAdmin() && !user.isOrga() && !user.isJudge());
        gm.setDataProvider(DataProvider.fromFilteringCallbacks(
                query -> {
                    String filter = query.getFilter().orElse(null);
                    LOG.trace("player query. filter='{}'", filter);

                    int offset = query.getOffset();
                    int limit = query.getLimit();

                    Pageable page = PageRequest.of(offset / limit, limit);
                    Page<Person> players = personService.findAll(page);

                    return players.stream();
                },
                query -> {
                    String filter = query.getFilter().orElse(null);
                    LOG.trace("player count query. filter='{}'", filter);

                    return personService.count();
                }
        ));
        gm.setItemLabelGenerator((ItemLabelGenerator<Person>) Person::getName);


        save.getStyle().set("marginRight", "10px");
        save.addClickListener(event -> {
            if (data == null) data = new MissionReport();

            data.setDate(missionDate.getValue());
            data.setObjectivesMet(successState.getValue());
            data.setAchievements(achievements.getValue());
            data.setNotes(notes.getValue());
            data.setGameMaster(gm.getValue());

            getEventBus().fireEvent(new MissionGroupReportSaveEvent(this, false));
        });

        reset.addClickListener(event -> {
            // FIXME 2021-04-04 rlichti implement resetting the data.
        });

        getContent().add(form);

        // mark as initialized.
        initialized = true;
    }

    public void setData(@NotNull MissionReport data) {
        if (this.data != null && this.data.equals(data)) {
            LOG.info("Mission report didn't change. Ignoring event. code={}, id={}, gm={}",
                    this.data.getMission().getCode(), this.data.getId(),
                    this.data.getGameMaster().getUsername());

            return;
        }

        LOG.debug("Set mission report. id={}, code={}, date={}, gm={}",
                data.getId(), data.getMission().getCode(),
                data.getDate(), data.getGameMaster().getName());


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
    public void localeChange(LocaleChangeEvent event) {
        LOG.trace("Change locale event. locale={}", event.getLocale());

        setLocale(event.getLocale());
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

        gm.setLabel(getTranslation("missionreport.gm.caption"));
        gm.setHelperText(getTranslation("missionreport.gm.help"));

        LOG.trace("Adding all form elements.");
        form.add(gm, missionDate, successState, achievements, notes);

        // Buttons
        if (!user.isReadonly()) {
            save.setText(getTranslation("buttons.save.caption"));
            reset.setText(getTranslation("buttons.reset.caption"));

            actions.removeAll();
            actions.add(save, reset);
            LOG.trace("Adding action buttons.");
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
            LOG.debug("Locale has not changed. Ignoring event. locale={}", locale);
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
