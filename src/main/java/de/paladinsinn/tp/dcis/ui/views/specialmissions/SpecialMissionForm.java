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
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import de.paladinsinn.tp.dcis.data.Clearance;
import de.paladinsinn.tp.dcis.data.operative.Operative;
import de.paladinsinn.tp.dcis.data.operative.OperativeRepository;
import de.paladinsinn.tp.dcis.data.specialmissions.SpecialMission;
import de.paladinsinn.tp.dcis.security.LoggedInUser;
import de.paladinsinn.tp.dcis.ui.components.OperativeSelector;
import de.paladinsinn.tp.dcis.ui.components.TorgActionBar;
import de.paladinsinn.tp.dcis.ui.components.TorgForm;
import de.paladinsinn.tp.dcis.ui.i18n.I18nDatePicker;
import de.paladinsinn.tp.dcis.ui.views.operativespecialreports.AddOperativeToSpecialMissionEvent;
import de.paladinsinn.tp.dcis.ui.views.operativespecialreports.AddOperativeToSpecialMissionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

/**
 * SpecialMissionForm -- Edits/displays the data for a mission.
 *
 * @author klenkes74 {literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.3.0  2021-04-18
 */
@Service
@Scope(SCOPE_PROTOTYPE)
public class SpecialMissionForm extends TorgForm<SpecialMission> {
    private static final Logger LOG = LoggerFactory.getLogger(SpecialMissionForm.class);
    public static final int DEFAULT_PAYMENT = 250;
    public static final int DEFAULT_XP = 5;

    /**
     * service for writing data to.
     */
    private final SpecialMissionService missionService;
    private final OperativeRepository operativeRepository;

    private final AddOperativeToSpecialMissionListener addOperativeToMissionListener;


    private final TextField title = new TextField();

    private final TextField id = new TextField();
    private final TextField code = new TextField();
    private final ComboBox<Clearance> clearance = new ComboBox<>();
    private final DatePicker missionDate = new I18nDatePicker();
    private final TextField publication = new TextField();
    private final TextField image = new TextField();
    private final TextArea description = new TextArea();
    private final IntegerField payment = new IntegerField();
    private final IntegerField xp = new IntegerField();

    private OperativeSelector operatives;


    @Autowired
    public SpecialMissionForm(
            @NotNull final SpecialMissionService missionService,
            @NotNull final AddOperativeToSpecialMissionListener addOperativeToMissionListener,
            @NotNull final OperativeRepository operativeRepository,
            @NotNull final LoggedInUser user
    ) {
        super(user);

        this.missionService = missionService;
        this.operativeRepository = operativeRepository;
        this.addOperativeToMissionListener = addOperativeToMissionListener;
    }

    protected void init() {
        if (data == null || user == null || initialized) {
            LOG.debug(
                    "Can't initialize or already initialized. initialized={}, data={}, user={}",
                    initialized, data, user
            );
            return;
        }

        super.init();

        addListener(SpecialMissionSaveEvent.class, missionService);
        addListener(AddOperativeToSpecialMissionEvent.class, addOperativeToMissionListener);

        actions = new TorgActionBar(
                "buttons",
                event -> { // save
                    scrape();
                    getEventBus().fireEvent(new SpecialMissionSaveEvent(this, data));

                    for (Operative o : operatives.getValue()) {
                        fireEvent(new AddOperativeToSpecialMissionEvent(this, data, o));
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
        actions.setReadOnly(
                user.isReadonly()
                        && !user.getPerson().equals(data.getGameMaster())
                        && !user.isAdmin()
                        && !user.isOrga()
                        && !user.isJudge()
        );

        id.setReadOnly(true);

        code.setReadOnly(true);

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

        operatives = new OperativeSelector("missionreport.add-operatives", operativeRepository);
        operatives.setReadonly(user.isReadonly());
        operatives.setVisible(!user.isReadonly());

        LOG.trace("adding all form elements.");
        form.add(
                code, clearance, missionDate,
                title,
                publication,
                payment, xp, image,
                description,
                operatives);

        form.add(actions);
        form.setColspan(actions, 3);


        form.setColspan(title, 3);
        form.setColspan(publication, 3);
        form.setColspan(description, 3);

        getContent().add(form);

        initialized = true;
    }


    public void populate() {
        if (data == null) {
            LOG.warn("Tried to populate form data without a mission defined.");
            return;
        }

        if (data.getId() != null) {
            id.setValue(data.getId().toString());
        }

        if (data.getCode() != null) {
            code.setValue(data.getCode().toString());
        } else {
            code.setValue(UUID.randomUUID().toString());
        }

        if (data.getGameMaster() != null) {
            data.setGameMaster(user.getPerson());
        }

        bindData(data.getMissionDate(), missionDate);

        bindData(data.getTitle(), title);
        bindData(data.getClearance(), clearance);
        bindData(data.getPublication(), publication);
        bindData(data.getImageUrl(), image);
        bindData(data.getDescription(), description);
        bindData(data.getPayment(), payment);
        bindData(data.getXp(), xp);
    }

    protected void scrape() {
        if (data == null) data = new SpecialMission();

        data.setCode(UUID.fromString(code.getValue()));
        data.setTitle(title.getValue());
        data.setClearance(clearance.getValue());
        data.setMissionDate(missionDate.getValue());
        data.setPublication(publication.getValue());
        data.setImageUrl(image.getValue());
        data.setDescription(description.getValue());
        data.setPayment(payment.getValue());
        data.setXp(xp.getValue());
        data.preUpdate();
    }

    @Override
    public void translate() {
        if (user == null || data == null || locale == null) {
            LOG.warn(
                    "Can't build special mission form. user={}, mission={}, locale={}",
                    user, data, locale
            );

            return;
        }

        LOG.debug("Building special mission edit form. locale={}, mission={}", locale, data);

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

        missionDate.setLabel(getTranslation("specialmission.date.caption"));
        missionDate.setHelperText(getTranslation("specialmission.date.help"));

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
    }

}
