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

package de.paladinsinn.tp.dcis.ui.views.specialmissions;

import com.sun.istack.NotNull;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import de.codecamp.vaadin.serviceref.ServiceRef;
import de.paladinsinn.tp.dcis.data.missions.MissionReport;
import de.paladinsinn.tp.dcis.data.operative.Operative;
import de.paladinsinn.tp.dcis.data.operative.OperativeSpecialReport;
import de.paladinsinn.tp.dcis.data.specialmissions.SpecialMission;
import de.paladinsinn.tp.dcis.data.specialmissions.SpecialMissionRepository;
import de.paladinsinn.tp.dcis.security.LoggedInUser;
import de.paladinsinn.tp.dcis.ui.MainView;
import de.paladinsinn.tp.dcis.ui.i18n.I18nPageTitle;
import de.paladinsinn.tp.dcis.ui.i18n.TranslatableComponent;
import de.paladinsinn.tp.dcis.ui.views.operativespecialreports.OperativeSpecialReportForm;
import de.paladinsinn.tp.dcis.ui.views.operativespecialreports.OperativeSpecialReportService;
import de.paladinsinn.tp.dcis.ui.views.operativespecialreports.RemoveOperativeFromSpecialMissionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SpecialMissionView -- Display and Editor for {@link MissionReport}.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-03-28
 */
@Route(value = "specialmissionreports/:id?", layout = MainView.class)
@I18nPageTitle("missionreport.editor.caption")
@CssImport("./views/edit-view.css")
@Secured({"GM", "JUDGE", "ORGA", "ADMIN"})
public class SpecialMissionView extends Div implements BeforeEnterObserver, LocaleChangeObserver, TranslatableComponent, Serializable, AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(SpecialMissionView.class);

    @Autowired
    private LoggedInUser user;

    @Autowired
    private SpecialMissionForm missionReportForm;

    @Autowired
    private ServiceRef<SpecialMissionRepository> missionReportRepository;

    @Autowired
    private RemoveOperativeFromSpecialMissionListener removeOperativeFromSpecialMissionListener;

    @Autowired
    private OperativeSpecialReportService operativeReportService;

    /**
     * The mission report to be edited.
     */
    private SpecialMission report;

    private final Map<Tab, OperativeSpecialReportForm> operatives = new ConcurrentHashMap<>();

    private Locale locale;

    private boolean initialized = false;


    private Tabs tabs;
    private Tab reportTab;


    @PostConstruct
    public void init() {
        if (initialized) {
            return;
        }

        addClassName("missionreport-view");

        reportTab = new Tab(getTranslation("specialmission.editor.caption"));
        missionReportForm.setVisible(true);

        tabs = new Tabs();
        tabs.add(reportTab);
        tabs.setSelectedTab(reportTab);
        tabs.addSelectedChangeListener(event -> {
            LOG.trace("Change selected tabs. old='{}', new='{}'",
                    event.getPreviousTab(), event.getSelectedTab());
            if (event.getSelectedTab() == null || event.getSelectedTab().equals(event.getPreviousTab())) {
                LOG.debug("Tab not changed or new tab is 'null'. old='{}', new='{}'",
                        event.getPreviousTab(), event.getSelectedTab());
                return;
            }

            if (event.getSelectedTab() == reportTab) {
                operatives.values().forEach(this::remove);
                add(missionReportForm);
            } else {
                operatives.values().forEach(this::remove);
                remove(missionReportForm);
                add(operatives.get(event.getSelectedTab()));
            }
        });


        add(tabs);
        add(missionReportForm);

        initialized = true;

        if (report != null) {
            populate();
        }
    }

    protected void populate() {
        tabs.removeAll();
        tabs.add(reportTab);

        missionReportForm.setData(report);

        getInvolvedOperatives(report);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        init();

        Optional<String> reportId = event.getRouteParameters().get("id");
        LOG.trace("Entering form. reportId={}", reportId);

        Optional<SpecialMission> report = Optional.empty();
        if (reportId.isPresent()) {
            report = missionReportRepository.get().findById(UUID.fromString(reportId.get()));
        } else {
            LOG.warn("ReportId is missing. reportId={}", reportId);
        }

        report.ifPresent(r -> {
            setData(r);

            populate();
            translate();
        });
    }

    private void setData(@NotNull SpecialMission report) {
        LOG.trace("Loaded MissionReport. report={}", report);

        this.report = report;
    }

    private void getInvolvedOperatives(SpecialMission report) {
        operatives.clear();
        for (OperativeSpecialReport o : report.getOperatives()) {
            if (!canViewOperative(o.getOperative())) {
                LOG.debug(
                        "Person is not allowed to display operative. user='{}', operative='{}'",
                        user.getPerson().getName(),
                        o.getOperative().getName()
                );
            } else {
                LOG.debug("Adding operative. operative='{}'", o.getOperative().getName());

                Tab tab = new Tab(o.getOperative().getName());

                OperativeSpecialReportForm operatorReportForm = new OperativeSpecialReportForm(operativeReportService, removeOperativeFromSpecialMissionListener, user);
                operatorReportForm.setData(o);

                operatives.put(tab, operatorReportForm);

                operatives.forEach((key, value) -> tabs.add(key));
            }
        }
    }


    @Override
    public void localeChange(LocaleChangeEvent event) {
        LOG.trace("Locale change event. locale={}", event.getLocale());

        setLocale(event.getLocale());
    }

    @Override
    public void setLocale(@NotNull final Locale locale) {
        if (this.locale != null && this.locale.equals(locale)) {
            LOG.debug("Locale did not change - ignore event. locale={}", this.locale);
            return;
        }

        this.locale = locale;

        missionReportForm.setLocale(locale);
        operatives.values().forEach(o -> o.setLocale(locale));

        translate();
    }

    @Override
    public void translate() {
        init();

        reportTab.setLabel(getTranslation("specialmission.editor.caption"));
        missionReportForm.translate();
        operatives.values().forEach(OperativeSpecialReportForm::translate);
    }


    private boolean canViewOperative(Operative operative) {
        return user.isJudge() || user.isAdmin() || user.isOrga()
                || operative.getPlayer().equals(user.getPerson())
                || report.getGameMaster().equals(user.getPerson());
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
        LOG.trace("Closing form.");

        missionReportForm.close();

        for (OperativeSpecialReportForm operativeSpecialReportForm : operatives.values()) {
            operativeSpecialReportForm.close();
        }

        removeAll();
        LOG.debug("Closed form.");
    }
}
