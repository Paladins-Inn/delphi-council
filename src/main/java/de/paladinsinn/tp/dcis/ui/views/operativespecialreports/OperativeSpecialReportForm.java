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

package de.paladinsinn.tp.dcis.ui.views.operativespecialreports;

import com.sun.istack.NotNull;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import de.paladinsinn.tp.dcis.data.operative.OperativeSpecialReport;
import de.paladinsinn.tp.dcis.security.LoggedInUser;
import de.paladinsinn.tp.dcis.ui.components.TorgActionBar;
import de.paladinsinn.tp.dcis.ui.components.TorgForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

/**
 * MissionOperativeReportEditForm -- Edits/displays the data for a mission report on group level.
 *
 * @author klenkes74 {literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-04-04
 */
@Service
@Scope(SCOPE_PROTOTYPE)
public class OperativeSpecialReportForm extends TorgForm<OperativeSpecialReport> {
    private static final Logger LOG = LoggerFactory.getLogger(OperativeSpecialReportForm.class);

    private final OperativeSpecialReportService reportService;
    private final RemoveOperativeFromSpecialMissionListener removeOperativeFromMissionListener;


    // The form components.
    private final FormLayout form = new FormLayout();
    private final TextField id = new TextField();
    private final TextArea notes = new TextArea();


    @Autowired
    public OperativeSpecialReportForm(
            @NotNull final OperativeSpecialReportService reportService,
            @NotNull final RemoveOperativeFromSpecialMissionListener removeOperativeFromMissionListener,

            LoggedInUser user) {
        super(user);

        this.reportService = reportService;
        this.removeOperativeFromMissionListener = removeOperativeFromMissionListener;
    }


    @PostConstruct
    public void init() {
        if (initialized || data == null || user == null) {
            LOG.debug(
                    "Already initialized or unable to initialize. initialized={}, data={}, user={}",
                    initialized,
                    data,
                    user
            );
            return;
        }

        super.init();

        addListener(OperativeSpecialReportSaveEvent.class, reportService);
        addListener(RemoveOperativeFromSpecialMissionEvent.class, removeOperativeFromMissionListener);

        id.setReadOnly(true);

        notes.addClassName("long-text");
        notes.setClearButtonVisible(true);
        notes.setMaxLength(4000);
        notes.setReadOnly(user.isReadonly() && !(data != null && user.getPerson().equals(data.getSpecialMission().getGameMaster())));

        actions = new TorgActionBar(
                "buttons",
                event -> { // save
                    scrape();
                    getEventBus().fireEvent(new OperativeSpecialReportSaveEvent(this, data));
                },
                event -> { // reset
                    LOG.info("Resetting data from: displayed={}, new={}", data, oldData);
                    resetData();
                },
                ev -> { // cancel
                },
                ev -> { // delete
                    if (user.isOrga() || user.isJudge() || data.getSpecialMission().getMissionDate().isAfter(LocalDate.now())) {
                        LOG.info("Removing this operative from mission");
                        scrape();

                        fireEvent(new RemoveOperativeFromSpecialMissionEvent(this, data));

                        getUI().ifPresent(ui -> ui.getPage().getHistory().back());
                    }
                }
        );
        actions.setReadOnly(
                user.isReadonly()
                        && !user.getPerson().equals(data.getSpecialMission().getGameMaster())
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
            LOG.warn("Tried to polulate form data without a mission report defined.");
            return;
        }

        bindData(data.getId().toString(), id);
        bindData(data.getNotes(), notes);
    }

    protected void scrape() {
        if (data == null) data = new OperativeSpecialReport();

        data.setNotes(notes.getValue());
    }

    @Override
    public void translate() {
        if (user == null || data == null || locale == null) {
            LOG.warn(
                    "Can't build mission report group report editor due to mission data. user={}, report={}, locale={}",
                    user, data, locale
            );

            return;
        }

        LOG.debug("Building mission report group edit form. report={}, locale={}", data, locale);

        LOG.trace("Remove all form elements.");
        form.removeAll();

        // Form fields
        id.setLabel(getTranslation("input.id.caption"));
        id.setHelperText(getTranslation("input.id.help"));

        notes.setLabel(getTranslation("missionreport.notes.caption"));
        notes.setHelperText(getTranslation("missionreport.notes.help"));


        LOG.trace("Adding all form elements.");
        form.add(notes, actions);

        form.setColspan(notes, 3);
        form.setColspan(actions, 3);
    }
}
