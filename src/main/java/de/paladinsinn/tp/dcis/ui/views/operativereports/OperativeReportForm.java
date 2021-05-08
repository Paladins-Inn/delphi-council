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

package de.paladinsinn.tp.dcis.ui.views.operativereports;

import com.sun.istack.NotNull;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.VaadinSession;
import de.paladinsinn.tp.dcis.data.operative.OperativeReport;
import de.paladinsinn.tp.dcis.security.LoggedInUser;
import de.paladinsinn.tp.dcis.ui.components.TorgActionBar;
import de.paladinsinn.tp.dcis.ui.components.TorgForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

/**
 * MissionOperativeReportEditForm -- Edits/displays the data for a mission report on group level.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-04-04
 */
@Service
@Scope(SCOPE_PROTOTYPE)
@Slf4j
public class OperativeReportForm extends TorgForm<OperativeReport> {
    private final OperativeReportService reportService;
    private final RemoveOperativeFromMissionListener removeOperativeFromMissionListener;


    // The form components.
    private final FormLayout form = new FormLayout();
    private final TextField id = new TextField();
    private final TextArea achievements = new TextArea();
    private final TextArea notes = new TextArea();


    @Autowired
    public OperativeReportForm(
            @NotNull final OperativeReportService reportService,
            @NotNull final RemoveOperativeFromMissionListener removeOperativeFromMissionListener,

            LoggedInUser user) {
        super(user);

        this.reportService = reportService;
        this.removeOperativeFromMissionListener = removeOperativeFromMissionListener;
    }


    @PostConstruct
    public void init() {
        if (initialized || data == null || user == null) {
            log.debug(
                    "Already initialized or unable to initialize. initialized={}, data={}, user={}",
                    initialized,
                    data,
                    user
            );
            return;
        }

        if (locale == null) {
            locale = VaadinSession.getCurrent().getLocale();
        }

        addListener(OperativeReportSaveEvent.class, reportService);
        addListener(RemoveOperativeFromMissionEvent.class, removeOperativeFromMissionListener);

        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("1px", 1),
                new FormLayout.ResponsiveStep("400px", 2),
                new FormLayout.ResponsiveStep("800px", 3)
        );

        id.setReadOnly(true);
        achievements.addClassName("long-text");
        achievements.setClearButtonVisible(true);
        achievements.setMaxLength(4000);

        achievements.setReadOnly(user.isReadonly() && !(data != null && user.getPerson().getName().equals(data.getReport().getGameMaster().getUsername())));
        notes.addClassName("long-text");
        notes.setClearButtonVisible(true);
        notes.setMaxLength(4000);
        notes.setReadOnly(user.isReadonly() && !(data != null && user.getPerson().getName().equals(data.getReport().getGameMaster().getUsername())));

        actions = new TorgActionBar(
                "buttons",
                event -> { // save
                    scrape();
                    getEventBus().fireEvent(new OperativeReportSaveEvent(this, false));
                },
                event -> { // reset
                    log.info("Resetting data from: displayed={}, new={}", data, oldData);
                    resetData();
                },
                ev -> { // cancel
                },
                ev -> { // delete
                    if (user.isOrga() || user.isJudge() || data.getReport().getDate().isAfter(LocalDate.now())) {
                        log.info("Removing this operative from mission");
                        scrape();

                        fireEvent(new RemoveOperativeFromMissionEvent(this, data));

                        getUI().ifPresent(ui -> ui.getPage().getHistory().back());
                    }
                }
        );
        actions.setReadOnly(
                user.isReadonly()
                        && !user.getPerson().equals(data.getReport().getGameMaster())
                        && !user.isOrga()
                        && !user.isAdmin()
                        && !user.isJudge()
        );

        getContent().add(form);

        // mark as initialized.
        initialized = true;
    }

    protected void populate() {
        if (data == null) {
            log.warn("Tried to polulate form data without a mission report defined.");
            return;
        }

        bindData(data.getId().toString(), id);
        bindData(data.getAchievements(), achievements);
        bindData(data.getNotes(), notes);
    }

    protected void scrape() {
        if (data == null) data = new OperativeReport();

        data.setAchievements(achievements.getValue());
        data.setNotes(notes.getValue());
    }

    @Override
    public void translate() {
        if (user == null || data == null || locale == null) {
            log.warn(
                    "Can't build mission report group report editor due to mission data. user={}, report={}, locale={}",
                    user, data, locale
            );

            return;
        }

        log.debug("Building mission report group edit form. report={}, locale={}", data, locale);

        log.trace("Remove all form elements.");
        form.removeAll();

        // Form fields
        id.setLabel(getTranslation("input.id.caption"));
        id.setHelperText(getTranslation("input.id.help"));

        achievements.setLabel(getTranslation("missionreport.achievements.caption"));
        achievements.setHelperText(getTranslation("missionreport.achievements.help"));

        notes.setLabel(getTranslation("missionreport.notes.caption"));
        notes.setHelperText(getTranslation("missionreport.notes.help"));


        log.trace("Adding all form elements.");
        form.add(achievements);
        form.add(notes);

        form.add(actions);

        form.setColspan(achievements, 3);
        form.setColspan(notes, 3);
        form.setColspan(actions, 3);
    }
}
