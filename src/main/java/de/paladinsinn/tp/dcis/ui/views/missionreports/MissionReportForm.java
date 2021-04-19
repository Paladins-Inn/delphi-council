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
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.VaadinSession;
import de.paladinsinn.tp.dcis.data.missions.MissionReport;
import de.paladinsinn.tp.dcis.data.missions.SuccessState;
import de.paladinsinn.tp.dcis.data.operative.Operative;
import de.paladinsinn.tp.dcis.data.operative.OperativeRepository;
import de.paladinsinn.tp.dcis.data.person.PersonRepository;
import de.paladinsinn.tp.dcis.security.LoggedInUser;
import de.paladinsinn.tp.dcis.ui.components.OperativeSelector;
import de.paladinsinn.tp.dcis.ui.components.PersonSelector;
import de.paladinsinn.tp.dcis.ui.components.TorgActionBar;
import de.paladinsinn.tp.dcis.ui.components.TorgForm;
import de.paladinsinn.tp.dcis.ui.i18n.I18nDatePicker;
import de.paladinsinn.tp.dcis.ui.views.operativereports.AddOperativeToMissionEvent;
import de.paladinsinn.tp.dcis.ui.views.operativereports.AddOperativeToMissionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

/**
 * MissionReportForm -- Edits/displays the data for a mission report on group level.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-03-29
 */
@Service
@Scope(SCOPE_PROTOTYPE)
public class MissionReportForm extends TorgForm<MissionReport> {
    private static final Logger LOG = LoggerFactory.getLogger(MissionReportForm.class);

    /**
     * The service for writing data to.
     */
    private final MissionReportService reportService;
    private final AddOperativeToMissionListener addOperativeToMissionListener;

    private final PersonRepository personRepository;
    private final OperativeRepository operativeRepository;


    private final TextField id = new TextField();
    private final DatePicker missionDate = new I18nDatePicker();
    private final ComboBox<SuccessState> successState = new ComboBox<>();
    private final TextArea achievements = new TextArea();
    private final TextArea notes = new TextArea();

    private PersonSelector gm;
    private OperativeSelector operatives;

    @Autowired
    public MissionReportForm(
            @NotNull final MissionReportService reportService,
            @NotNull final AddOperativeToMissionListener addOperativeToMissionListener,
            @NotNull final PersonRepository personRepository,
            @NotNull final OperativeRepository operativeRepository,

            @NotNull final LoggedInUser user
    ) {
        super(user);

        this.reportService = reportService;
        this.addOperativeToMissionListener = addOperativeToMissionListener;
        this.personRepository = personRepository;
        this.operativeRepository = operativeRepository;
    }


    protected void init() {
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
        addListener(AddOperativeToMissionEvent.class, addOperativeToMissionListener);

        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("1px", 1),
                new FormLayout.ResponsiveStep("400px", 2),
                new FormLayout.ResponsiveStep("800px", 3)
        );

        user.allow(user.getPerson().equals(data.getGameMaster()));

        actions = new TorgActionBar(
                "buttons",
                event -> { // save
                    scrape();
                    getEventBus().fireEvent(new MissionReportSaveEvent(this, false));

                    for (Operative o : operatives.getValue()) {
                        fireEvent(new AddOperativeToMissionEvent(this, data, o));
                    }
                },
                event -> { // reset
                    LOG.info("Resetting data from: displayed={}, new={}", data, oldData);
                    resetData();
                },
                event -> { // cancel
                    getUI().ifPresent(ui -> ui.getPage().getHistory().back());
                },
                null
        );
        actions.setReadOnly(user.isReadonly());

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
        achievements.setMaxLength(4000);
        achievements.setReadOnly(user.isReadonly());

        notes.addClassName("long-text");
        notes.setClearButtonVisible(true);
        notes.setMaxLength(4000);
        notes.setReadOnly(user.isReadonly());

        gm = new PersonSelector("missionreport.gm", personRepository, user);
        gm.setReadOnly(!user.isAdmin() && !user.isOrga() && !user.isJudge());

        operatives = new OperativeSelector("missionreport.add-operatives", operativeRepository);
        operatives.setReadonly(user.isReadonly());
        operatives.setVisible(!user.isReadonly());

        form.add(gm, missionDate, successState, achievements, notes, operatives);
        form.add(actions);

        form.setColspan(achievements, 3);
        form.setColspan(notes, 3);
        form.setColspan(actions, 3);

        getContent().add(form);


        // mark as initialized.
        initialized = true;
    }

    protected void scrape() {
        if (data == null) data = new MissionReport();

        data.setDate(missionDate.getValue());
        data.setObjectivesMet(successState.getValue());
        data.setAchievements(achievements.getValue());
        data.setNotes(notes.getValue());
        data.setGameMaster(gm.getValue());
    }


    @Override
    protected void populate() {
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
        actions.setLocale(locale);
    }
}
