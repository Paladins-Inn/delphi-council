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

package de.paladinsinn.delphicouncil.views.missions;

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
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import de.codecamp.vaadin.serviceref.ServiceRef;
import de.paladinsinn.delphicouncil.app.i18n.TranslatableComponent;
import de.paladinsinn.delphicouncil.data.missions.MissionReport;
import de.paladinsinn.delphicouncil.data.missions.MissionReportRepository;
import de.paladinsinn.delphicouncil.data.operative.Operative;
import de.paladinsinn.delphicouncil.views.main.MainView;
import de.paladinsinn.delphicouncil.ui.forms.missions.MissionEditForm;
import de.paladinsinn.delphicouncil.ui.forms.missions.MissionGroupReportEditForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.*;

/**
 * MissionReportView -- Display and Editor for {@link MissionReport}.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-03-28
 */
@Route(value = "missionreport/:report?", layout = MainView.class)
@PageTitle("Mission Reports")
@CssImport("./views/missionreport/missionreport-view.css")
@Secured({"PLAYER", "GM", "JUDGE", "ORGA"})
public class MissionReportView extends Div implements BeforeEnterObserver, LocaleChangeObserver, TranslatableComponent, Serializable, AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(MissionReportView.class);

    @Autowired
    private MissionEditForm missionForm;

    @Autowired
    private MissionGroupReportEditForm missionReportForm;

    @Autowired
    private ServiceRef<MissionReportRepository> missionReportRepository;

    private MissionReport report;

    private Locale locale;

    @PostConstruct
    public void init() {
        setLocale(VaadinSession.getCurrent().getLocale());
        LOG.debug("Loading form. view={}, locale={}", this, locale);

        addClassName("missionreport-view");

        translate();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<String> reportId = event.getRouteParameters().get("report");
        LOG.trace("Entering form. view={}, reportId={}", this, reportId);

        Optional<MissionReport> report = Optional.empty();
        if (reportId.isPresent()) {
            report = missionReportRepository.get().findById(UUID.fromString(reportId.get()));
        } else {
            LOG.warn("ReportId is missing. view={}, reportId={}", this, reportId);
        }

        report.ifPresent(this::setReport);
    }

    private void setReport(@NotNull MissionReport report) {
        LOG.trace("Loaded MissionReport. view={}, report={}", this, report);

        this.report = report;
        missionForm.setMission(report.getMission());
        missionReportForm.setMissionReport(report);

        translate();
    }


    @Override
    public void localeChange(LocaleChangeEvent event) {
        LOG.trace("Locale change event. view={}, locale={}, event={}", this, event.getLocale(), event);

        setLocale(event.getLocale());
        missionForm.setLocale(event.getLocale());
        missionReportForm.setLocale(event.getLocale());
        translate();
    }

    @Override
    public void translate() {
        Authentication userDetails = null;
        try {
            userDetails = SecurityContextHolder.getContext()
                    .getAuthentication();
        } catch (NullPointerException e) {
            LOG.warn("No user is logged in. Can't load user details. view={}", this);
        }
        LOG.trace(
                "Rebuilding view. view={}, user={}, locale={}",
                this,
                userDetails != null ? userDetails.getName() : "-not set-",
                locale
        );

        removeAll();


        LOG.trace("Rebuild Mission Data Tab. view={}", this);
        Tab missionTab = new Tab(getTranslation("mission.editor.title"));
        missionForm.translate();

        LOG.trace("Rebuild Mission Report Data Tab. view={}", this);
        Tab reportTab = new Tab(getTranslation("missionreport.editor.title"));
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
            for (Operative o : report.getOperatives()) {
                if (!canViewOperative(userDetails, o)) {
                    LOG.debug(
                            "Person is not allowed to display operative. view={}, user='{}', operative='{}, {}'",
                            this,
                            userDetails.getName(),
                            o.getLastName(), o.getFirstName()
                    );
                } else {
                    LOG.debug("Adding tab for operative. view={}, operative='{}, {}'", this, o.getLastName(), o.getFirstName());

                    Tab tab = new Tab(getTranslation("missionreport.operative.title",
                            o.getLastName(), o.getFirstName().charAt(0) + "."));
                    Div c = new Div();

                    selectableTabs.put(tab, c);
                    tabList.add(tab);
                }
            }
        }

        Tabs tabs = new Tabs(tabList.toArray(Tab[]::new));
        tabs.setSelectedTab(missionTab);
        add(tabs);

        tabs.addSelectedChangeListener(event -> {

            selectableTabs.values().forEach(p -> p.setVisible(false));

            Component selectedPage = selectableTabs.get(tabs.getSelectedTab());
            selectedPage.setVisible(true);
        });

        selectableTabs.values().forEach(this::add);
    }


    private boolean canViewOperative(Authentication userDetails, Operative operative) {
        return userDetails != null
                && (
                operative.getPlayer().getName().equals(userDetails.getName())
                        || report.getGameMaster().getName().equals(userDetails.getName())
                        || userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ORGA"))
                        || userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))
                        || userDetails.getAuthorities().contains(new SimpleGrantedAuthority("JUDGE"))
        );
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
        LOG.trace("Closing form. view={}", this);

        missionForm.close();

        removeAll();
        LOG.debug("Closed form. view={}", this);
    }
}
