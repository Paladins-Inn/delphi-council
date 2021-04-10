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

package de.paladinsinn.tp.dcis.ui.views.missions;

import com.sun.istack.NotNull;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.server.VaadinSession;
import de.paladinsinn.tp.dcis.data.Clearance;
import de.paladinsinn.tp.dcis.data.missions.Mission;
import de.paladinsinn.tp.dcis.security.LoggedInUser;
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
 * MissionEditForm -- Edits/displays the data for a mission.
 *
 * @author klenkes74 {literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-03-29
 */
@Service
@Scope(SCOPE_PROTOTYPE)
public class MissionForm extends Composite<Div> implements LocaleChangeObserver, TranslatableComponent, Serializable, AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(MissionForm.class);
    public static final int DEFAULT_PAYMENT = 250;
    public static final int DEFAULT_XP = 5;

    /**
     * service for writing data to.
     */
    private final MissionService missionService;

    /**
     * The locale of the form.
     */
    private Locale locale;

    private final LoggedInUser user;

    private final TextField title = new TextField();
    private final FormLayout form = new FormLayout();

    private final TextField id = new TextField();
    private final TextField code = new TextField();
    private final ComboBox<Clearance> clearance = new ComboBox<>();
    private final TextField publication = new TextField();
    private final TextField image = new TextField();
    private final TextArea description = new TextArea();
    private final IntegerField payment = new IntegerField();
    private final IntegerField xp = new IntegerField();
    private final TextArea objectivesSuccess = new TextArea();
    private final TextArea objectivesGood = new TextArea();
    private final TextArea objectivesOutstanding = new TextArea();

    private TorgActionBar actions;

    /**
     * mission data
     */
    private Mission data;

    /**
     * If the form is already initialized.
     */
    private boolean initialized = false;

    @Autowired
    public MissionForm(
            @NotNull final MissionService missionService,
            @NotNull final LoggedInUser user
    ) {
        this.missionService = missionService;
        this.user = user;
    }

    private void init() {
        if (data == null || user == null || initialized) {
            LOG.debug(
                    "Can't initialize or already initialized. initialized={}, data={}, user={}",
                    initialized, data, user
            );
            return;
        }

        if (locale == null) {
            locale = VaadinSession.getCurrent().getLocale();
        }

        addListener(MissionSaveEvent.class, missionService);

        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("1px", 1),
                new FormLayout.ResponsiveStep("400px", 2),
                new FormLayout.ResponsiveStep("800px", 3)
        );

        actions = new TorgActionBar(
                "buttons",
                event -> {
                    scrape();
                    getEventBus().fireEvent(new MissionSaveEvent(this, false));
                },
                null,
                null,
                null
        );
        actions.setReadOnly(
                user.isReadonly()
                        && !user.isAdmin()
                        && !user.isOrga()
                        && !user.isJudge()
        );


        id.setReadOnly(true);

        code.setRequired(true);
        code.setReadOnly(user.isReadonly());

        title.setRequired(true);
        title.setReadOnly(user.isReadonly());

        clearance.setDataProvider(Clearance.ANY.dataProvider());
        clearance.setAllowCustomValue(false);
        clearance.setRequired(true);
        clearance.setReadOnly(user.isReadonly());

        publication.setRequired(false);
        publication.setReadOnly(user.isReadonly());

        image.setRequired(false);
        image.setReadOnly(user.isReadonly());

        description.addClassName("long-text");
        description.setRequired(true);
        description.setReadOnly(user.isReadonly());

        payment.setMin(100);
        payment.setMax(1000);
        payment.setStep(50);
        payment.setValue(DEFAULT_PAYMENT);
        payment.setReadOnly(user.isReadonly());

        xp.setMin(1);
        xp.setMax(50);
        xp.setStep(1);
        xp.setValue(DEFAULT_XP);
        xp.setReadOnly(user.isReadonly());

        objectivesSuccess.addClassName("long-text");
        objectivesSuccess.setRequired(true);
        objectivesSuccess.setReadOnly(user.isReadonly());

        objectivesGood.addClassName("long-text");
        objectivesGood.setRequired(true);
        objectivesGood.setReadOnly(user.isReadonly());

        objectivesOutstanding.addClassName("long-text");
        objectivesOutstanding.setRequired(true);
        objectivesOutstanding.setReadOnly(user.isReadonly());

        getContent().add(form);

        initialized = true;
    }

    public void setData(@NotNull Mission data) {
        if (this.data != null && this.data.equals(data)) {
            LOG.info("Mission report didn't change. Ignoring event. code={}, id={}",
                    this.data.getCode(), this.data.getId());

            return;
        }

        LOG.debug("Set mission. id={}, code={}, title={}", data.getId(), data.getCode(), data.getTitle());

        this.data = data;

        init();
        populate();
        translate();
    }

    public void populate() {
        if (data == null) {
            LOG.warn("Tried to populate form data without a mission defined.");
            return;
        }

        if (data.getId() != null) {
            id.setValue(data.getId().toString());
        }

        bindData(data.getCode(), code);
        bindData(data.getTitle(), title);
        bindData(data.getClearance(), clearance);
        bindData(data.getPublication(), publication);
        bindData(data.getImage(), image);
        bindData(data.getDescription(), description);
        bindData(data.getPayment(), payment);
        bindData(data.getXp(), xp);
        bindData(data.getObjectivesSuccess(), objectivesSuccess);
        bindData(data.getObjectivesGood(), objectivesGood);
        bindData(data.getObjectivesOutstanding(), objectivesOutstanding);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void bindData(final Object data, @NotNull HasValue component) {
        if (data != null) {
            component.setValue(data);
        }
    }

    private void scrape() {
        if (data == null) data = new Mission();

        data.setCode(code.getValue());
        data.setTitle(title.getValue());
        data.setClearance(clearance.getValue());
        data.setPublication(publication.getValue());
        data.setImage(image.getValue());
        data.setDescription(description.getValue());
        data.setPayment(payment.getValue());
        data.setXp(xp.getValue());
        data.setObjectivesSuccess(objectivesSuccess.getValue());
        data.setObjectivesGood(objectivesGood.getValue());
        data.setObjectivesOutstanding(objectivesOutstanding.getValue());
        data.setModified();
    }


    public Optional<Mission> getData() {
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
                    "Can't build mission form. user={}, mission={}, locale={}",
                    user, data, locale
            );

            return;
        }

        LOG.debug("Building mission edit form. locale={}, mission={}", locale, data);

        LOG.trace("Removing all form elements.");
        form.removeAll();


        // Form fields
        LOG.trace("Translating form elements.");
        id.setLabel(getTranslation("input.id.caption"));
        id.setHelperText(getTranslation("input.id.help"));

        code.setLabel(getTranslation("mission.code.caption"));
        code.setHelperText(getTranslation("mission.code.help"));

        title.setLabel(getTranslation("mission.title.caption"));
        title.setHelperText(getTranslation("mission.title.help"));

        clearance.setLabel(getTranslation("torg.clearance.caption"));
        clearance.setHelperText(getTranslation("torg.clearance.help"));
        clearance.setItemLabelGenerator((ItemLabelGenerator<Clearance>) item -> getTranslation("torg.clearance." + item.name()));

        publication.setLabel(getTranslation("mission.publication.caption"));
        publication.setHelperText(getTranslation("mission.publication.help"));

        image.setLabel(getTranslation("mission.image.caption"));
        image.setHelperText(getTranslation("mission.image.help"));

        description.setLabel(getTranslation("mission.description.caption"));
        description.setHelperText(getTranslation("mission.description.help"));

        payment.setLabel(getTranslation("mission.payment.caption"));
        payment.setHelperText(getTranslation("mission.description.help"));

        xp.setLabel(getTranslation("mission.xp.caption"));
        xp.setHeight(getTranslation("mission.xp.help"));

        objectivesSuccess.setLabel(getTranslation("mission.objectives.success.caption"));
        objectivesSuccess.setHelperText(getTranslation("mission.objectives.success.help"));

        objectivesGood.setLabel(getTranslation("mission.objectives.good.caption"));
        objectivesGood.setHelperText(getTranslation("mission.objectives.good.help"));

        objectivesOutstanding.setLabel(getTranslation("mission.objectives.outstanding.caption"));
        objectivesOutstanding.setHelperText(getTranslation("mission.objectives.outstanding.help"));


        LOG.trace("adding all form elements.");
        form.add(code, clearance, title, publication, payment, xp, image, description,
                objectivesSuccess, objectivesGood, objectivesOutstanding);

        form.add(actions);
        form.setColspan(actions, 3);


        form.setColspan(title, 3);
        form.setColspan(publication, 3);
        form.setColspan(description, 3);
        form.setColspan(objectivesSuccess, 3);
        form.setColspan(objectivesGood, 3);
        form.setColspan(objectivesOutstanding, 3);
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
