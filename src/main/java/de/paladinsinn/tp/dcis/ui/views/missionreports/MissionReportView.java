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
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import de.codecamp.vaadin.serviceref.ServiceRef;
import de.paladinsinn.tp.dcis.data.missions.MissionReport;
import de.paladinsinn.tp.dcis.data.missions.MissionReportRepository;
import de.paladinsinn.tp.dcis.data.operative.Operative;
import de.paladinsinn.tp.dcis.data.operative.OperativeReport;
import de.paladinsinn.tp.dcis.security.LoggedInUser;
import de.paladinsinn.tp.dcis.ui.MainView;
import de.paladinsinn.tp.dcis.ui.i18n.I18nPageTitle;
import de.paladinsinn.tp.dcis.ui.i18n.TranslatableComponent;
import de.paladinsinn.tp.dcis.ui.views.missions.MissionForm;
import de.paladinsinn.tp.dcis.ui.views.operativereports.OperativeReportForm;
import de.paladinsinn.tp.dcis.ui.views.operativereports.OperativeReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.*;

/**
 * MissionReportView -- Display and Editor for {@link MissionReport}.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-03-28
 */
@Route(value = "missionreports/:id?", layout = MainView.class)
@I18nPageTitle("missionreport.editor.caption")
@CssImport("./views/edit-view.css")
@Secured({"PLAYER", "GM", "JUDGE", "ORGA", "ADMIN"})
public class MissionReportView extends Div implements BeforeEnterObserver, LocaleChangeObserver, TranslatableComponent, Serializable, AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(MissionReportView.class);

    @Autowired
    private LoggedInUser user;

    @Autowired
    private MissionForm missionForm;

    @Autowired
    private MissionReportForm missionReportForm;

    @Autowired
    private ServiceRef<MissionReportRepository> missionReportRepository;

    @Autowired
    private OperativeReportService operativeReportService;

    /** The mission report to be edited. */
    private MissionReport report;

    private Locale locale;

    @PostConstruct
    public void init() {
        setLocale(VaadinSession.getCurrent().getLocale());
        LOG.debug("Loading form. locale={}", locale);

        addClassName("missionreport-view");

        translate();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<String> reportId = event.getRouteParameters().get("id");
        LOG.trace("Entering form. reportId={}", reportId);

        Optional<MissionReport> report = Optional.empty();
        if (reportId.isPresent()) {
            report = missionReportRepository.get().findById(UUID.fromString(reportId.get()));
        } else {
            LOG.warn("ReportId is missing. reportId={}", reportId);
        }

        report.ifPresent(this::setReport);
    }

    private void setReport(@NotNull MissionReport report) {
        LOG.trace("Loaded MissionReport. report={}", report);

        this.report = report;
        missionForm.setData(report.getMission());
        missionReportForm.setData(report);

        translate();
    }


    @Override
    public void localeChange(LocaleChangeEvent event) {
        LOG.trace("Locale change event. locale={}", event.getLocale());

        setLocale(event.getLocale());
        missionForm.setLocale(event.getLocale());
        missionReportForm.setLocale(event.getLocale());
        translate();
    }

    @Override
    public void translate() {
        if (user == null || locale == null) {
            LOG.warn(
                    "Can't translate mission report view. user={}, locale={}",
                    user, locale
            );

            return;
        }

        removeAll();


        LOG.trace("Rebuild Mission Data Tab.");
        Tab missionTab = new Tab(getTranslation("mission.editor.caption"));
        missionForm.translate();

        LOG.trace("Rebuild Mission Report Data Tab.");
        Tab reportTab = new Tab(getTranslation("missionreport.editor.caption"));
        missionReportForm.translate();

        // ordering tabs
        Map<Tab, Component> selectableTabs = new HashMap<>();
        List<Tab> tabList = new ArrayList<>();
        selectableTabs.put(missionTab, missionForm);
        selectableTabs.put(reportTab, missionReportForm);
        tabList.add(missionTab);
        tabList.add(reportTab);

        // Now the tabs for the Storm Knights
        if (report != null) {
            for (OperativeReport o : report.getOperatives()) {
                if (!canViewOperative(o.getOperative())) {
                    LOG.debug(
                            "Person is not allowed to display operative. user='{}', operative='{}'",
                            user.getPerson().getName(),
                            o.getOperative().getName()
                    );
                } else {
                    LOG.debug("Adding tab for operative. operative='{}'", o.getOperative().getName());

                    Tab tab = new Tab(getTranslation("missionreport.operative.title",
                            o.getOperative().getName()));

                    OperativeReportForm operatorReportForm = new OperativeReportForm(operativeReportService, user);
                    operatorReportForm.setData(o);

                    selectableTabs.put(tab, operatorReportForm);
                    tabList.add(tab);
                }
            }
        }

        Tabs tabs = new Tabs(tabList.toArray(Tab[]::new));

        if (report == null) {
            tabs.setSelectedTab(missionTab);
        } else {
            tabs.setSelectedTab(reportTab);
        }

        add(tabs);


        selectableTabs.values().forEach(p -> {
            p.setVisible(false);
            add(p);
        });
        selectableTabs.get(tabs.getSelectedTab()).setVisible(true);

        tabs.addSelectedChangeListener(event -> {
            selectableTabs.values().forEach(p -> p.setVisible(false));
            selectableTabs.get(tabs.getSelectedTab()).setVisible(true);
        });

    }


    private boolean canViewOperative(Operative operative) {
        return user.isJudge() || user.isAdmin() || user.isOrga()
                || operative.getPlayer().equals(user.getPerson())
                || report.getGameMaster().equals(user.getPerson());
    }


    @Override
    public void setLocale(Locale locale) {
        this.locale = locale;

        missionForm.setLocale(locale);
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

        missionForm.close();

        removeAll();
        LOG.debug("Closed form.");
    }
}
