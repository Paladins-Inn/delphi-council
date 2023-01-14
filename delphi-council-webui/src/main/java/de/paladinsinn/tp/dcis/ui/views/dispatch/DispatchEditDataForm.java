/*
 * Copyright (c) 2023. Roland T. Lichti
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.paladinsinn.tp.dcis.ui.views.dispatch;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.quarkus.annotation.UIScoped;
import com.wontlost.ckeditor.Constants;
import com.wontlost.ckeditor.VaadinCKEditor;
import com.wontlost.ckeditor.VaadinCKEditorBuilder;
import de.kaiserpfalzedv.rpg.torg.model.actors.Clearance;
import de.paladinsinn.tp.dcis.common.Language;
import de.paladinsinn.tp.dcis.model.client.Dispatch;
import de.paladinsinn.tp.dcis.ui.components.mvp.BasicDataForm;
import de.paladinsinn.tp.dcis.ui.components.mvp.BasicDataFormTab;
import de.paladinsinn.tp.dcis.ui.components.users.FrontendUser;
import de.paladinsinn.tp.dcis.ui.components.users.Roles;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

import static de.kaiserpfalzedv.rpg.torg.model.actors.Clearance.*;


/**
 * MissionDataForm -- Displays the data for the mission in a hopefully easy to use manner.
 *
 * @author klenkes {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2023-01-01
 */
@UIScoped
@ToString(onlyExplicitlyIncluded = true)
@Setter
@Getter
@Slf4j
public class DispatchEditDataForm extends BasicDataForm<Dispatch> {

    private final DispatchEditDataFormBaseData baseData = new DispatchEditDataFormBaseData();
    private final DispatchEditDataFormResultQuestionnaire resultQuestionnaire = new DispatchEditDataFormResultQuestionnaire();
    private final DispatchEditDataFormResults results = new DispatchEditDataFormResults();
    private final DispatchEditDataFormDispatchedTeams dispatchedTeams = new DispatchEditDataFormDispatchedTeams();


    // BEGIN of data fields
    private TextField code;
    private TextField name;
    private RadioButtonGroup<Clearance> clearance;
    private RadioButtonGroup<Language> language;
    private NumberField xp;
    private NumberField payment;
    private VaadinCKEditor description;
    private TextField image;
    private TextField publication;
    private VaadinCKEditor objectivesSuccess;
    private VaadinCKEditor objectivesGood;
    private VaadinCKEditor objectivesOutstanding;
    // END of data fields


    @Inject
    public DispatchEditDataForm(
            final FrontendUser user
    ) {
        super(user, new BeanValidationBinder<>(Dispatch.class));

        baseData.setForm(this);
        resultQuestionnaire.setForm(this);
        results.setForm(this);
        dispatchedTeams.setForm(this);

        createLanguage();
        createCode();
        createName();
        createClearance();
        createDescription();
        createXp();
        createPayment();
        createImage();

        createObjectivesSuccess();
        createObjectivesGood();
        createObjectivesOutstanding();
    }


    public void setData(Dispatch data) {
        super.setData(data);


        updateValues();
    }

    private void updateValues() {
        if (data.getCode() != null)             code.setValue(data.getCode());
        if (data.getClearance() != null)        clearance.setValue(data.getClearance());
        if (data.getLanguage() != null)         language.setValue(data.getLanguage());
        if (data.getName() != null)             name.setValue(data.getName());
        if (data.getDescription() != null)      description.setValue(data.getDescription());
        xp.setValue((double) data.getXp());
        payment.setValue((double) data.getPayment());

        if (data.getObjectivesSuccess() != null)        objectivesSuccess.setValue(data.getObjectivesSuccess());
        if (data.getObjectivesGood() != null)           objectivesGood.setValue(data.getObjectivesGood());
        if (data.getObjectivesOutstanding() != null)    objectivesOutstanding.setValue(data.getObjectivesOutstanding());
    }


    protected void bind() {
        if (data == null) {
            data = Dispatch.builder().build();
        }
        binder.setBean(data);
    }

    private void createLanguage() {
        language = new RadioButtonGroup<>();
        language.setItems(Language.values());
    }

    private void createCode() {
        code = new TextField();
    }

    private void createClearance() {
        clearance = new RadioButtonGroup<>();
        clearance.setItems(ALPHA, BETA, GAMMA, DELTA, OMEGA);
    }

    private void createName() {
        name = new TextField();
    }

    private void createDescription() {
        description = createEditor();
    }

    private VaadinCKEditor createEditor() {
        return new VaadinCKEditorBuilder().with(builder -> {
            builder.editorType = Constants.EditorType.CLASSIC;
            builder.theme = Constants.ThemeType.DARK;
        }).createVaadinCKEditor();
    }

    private void createXp() {
        xp = new NumberField();
    }

    private void createPayment() {
        payment = new NumberField();
    }

    private void createImage() {
        // TODO 2023-01-09 klenkes Implement image handling for detachments.
        image = new TextField();
    }

    private void createObjectivesSuccess() {
        objectivesSuccess = createEditor();
    }

    private void createObjectivesGood() {
        objectivesGood = createEditor();
    }

    private void createObjectivesOutstanding() {
        objectivesOutstanding = createEditor();
    }


    @Override
    public void onAttach(AttachEvent event) {
        super.onAttach(event);

        addTab(baseData);

        if (user.isInRole(Roles.gm) || user.isInRole(Roles.orga) || user.isInRole(Roles.judge)) {
            addTab(resultQuestionnaire);
        }

        if (user.isInRole(Roles.orga) || user.isInRole(Roles.judge)) {
            addTab(results);
        }

        addTab(dispatchedTeams);

        bind();

        translateLabels();
        updateValues();
    }

    private void translateLabels() {
        translate(code, "dispatch.code");
        translate(name, "dispatch.title");
        translate(language, "dispatch.language");
        translate(clearance, "dispatch.clearance");
        translate(description, "dispatch.description");
        translate(xp, "input.xp");
        translate(payment, "input.payment");
        translate(image, "dispatch.cover_url");

        translate(objectivesSuccess, "dispatch.objectives.success");
        translate(objectivesGood, "dispatch.objectives.good");
        translate(objectivesOutstanding, "dispatch.objectives.outstanding");

        log.trace("component translated. form={}", this);
    }



    @Override
    public void onDetach(final DetachEvent event) {
        super.onDetach(event);

        clearTabs();
    }


    /**
     * MissionDataFormBaseData -- The basic data to any mission.
     *
     * @author klenkes {@literal <rlichti@kaiserpfalz-edv.de>}
     * @since 2.0.0  2023-01-05
     */
    private class DispatchEditDataFormBaseData extends BasicDataFormTab<Dispatch> {
        private static final String I18N_KEY = "dispatch.form.tab.base_data";

        private Image imageDisplay;

        protected void attachFields() {
            layout.add(code, clearance, language, name, description, xp, payment);
            layout.setColspan(clearance, 2);

            layout.setColspan(name, 4);
            layout.setColspan(description, 4);
        }


        @Override
        public String getI18nKey() {
            return I18N_KEY;
        }
    }

    /**
     * MissionDataFormBaseData -- The basic data to any mission.
     *
     * @author klenkes {@literal <rlichti@kaiserpfalz-edv.de>}
     * @since 2.0.0  2023-01-05
     */
    private class DispatchEditDataFormDispatchedTeams extends BasicDataFormTab<Dispatch> {
        private static final String I18N_KEY = "dispatch.form.tab.dispatches";

        @Override
        public String getI18nKey() {
            return I18N_KEY;
        }

        @Override
        protected void attachFields() {

        }
    }

    /**
     * MissionDataFormBaseData -- The basic data to any mission.
     *
     * @author klenkes {@literal <rlichti@kaiserpfalz-edv.de>}
     * @since 2.0.0  2023-01-05
     */
    private class DispatchEditDataFormResultQuestionnaire extends BasicDataFormTab<Dispatch> {
        private static final String I18N_KEY = "dispatch.form.tab.questionnaire";

        @Override
        public String getI18nKey() {
            return I18N_KEY;
        }

        @Override
        protected void attachFields() {
            layout.add(objectivesSuccess, objectivesGood, objectivesOutstanding);

            layout.setColspan(objectivesSuccess, 4);
            layout.setColspan(objectivesGood, 4);
            layout.setColspan(objectivesOutstanding, 4);
        }
    }

    /**
     * MissionDataFormBaseData -- The basic data to any mission.
     *
     * @author klenkes {@literal <rlichti@kaiserpfalz-edv.de>}
     * @since 2.0.0  2023-01-05
     */
    private class DispatchEditDataFormResults extends BasicDataFormTab<Dispatch> {
        private static final String I18N_KEY = "dispatch.form.tab.results";


        @Override
        public String getI18nKey() {
            return I18N_KEY;
        }

        @Override
        protected void attachFields() {

        }
    }
}


