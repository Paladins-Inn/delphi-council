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

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.wontlost.ckeditor.Constants;
import com.wontlost.ckeditor.VaadinCKEditor;
import com.wontlost.ckeditor.VaadinCKEditorBuilder;
import de.paladinsinn.tp.dcis.model.Dispatch;
import de.paladinsinn.tp.dcis.ui.components.mvp.BasicTab;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.Locale;

/**
 * MissionDataFormBaseData -- The basic data to any mission.
 *
 * @author klenkes {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2023-01-05
 */
@Dependent
@ToString(onlyExplicitlyIncluded = true)
@Getter
@Setter
@Slf4j
public class DispatchDataFormBaseData extends BasicTab<Dispatch> {
    private static final String I18N_KEY = "dispatch.form.tab.basedata";

    private TextField dispatchId;
    private TextField code;
    private TextField name;

    // FIXME 2023-01-09 klenkes Add clearance to this form.

    private NumberField xp;
    private NumberField payment;

    private VaadinCKEditor description;

    private Image imageDisplay;
    private TextField image;


    @Inject
    public DispatchDataFormBaseData(final DispatchPresenter presenter) {
        super(presenter);
    }

    @Override
    public void onAttach(AttachEvent attachEvent) {
        if (attachEvent.isInitialAttach()) {
            createAndAttachId();
            createAndAttachCode();
            createAndAttachName();
            createAndAttachDescription();
            createAndAttachXp();
            createAndAttachPayment();
            createAndAttachImage();

            attachFields();
        }

        updateFields(presenter.getLocale());
    }

    private void attachFields() {
        layout.add(code, dispatchId, name,description,xp,payment);
        layout.setColspan(dispatchId, 3);

        layout.setColspan(name, 4);
        layout.setColspan(description, 4);
    }

    private void createAndAttachId() {
        dispatchId = new TextField();
        binder.bind(dispatchId, "id");
    }

    private void createAndAttachCode() {
        code = new TextField();
        binder.bind(code, "code");
    }

    private void createAndAttachName() {
        name = new TextField();
        binder.bind(name, "name");
    }

    private void createAndAttachDescription() {
        description = new VaadinCKEditorBuilder().with(builder -> {
            builder.editorType = Constants.EditorType.CLASSIC;
            builder.theme = Constants.ThemeType.DARK;
        }).createVaadinCKEditor();
        binder.bind(description, "description");
    }

    private void createAndAttachXp() {
        xp = new NumberField();
        binder.bind(xp, "xp");
    }

    private void createAndAttachPayment() {
        payment = new NumberField();
        binder.bind(payment, "payment");
    }

    private void createAndAttachImage() {
        // TODO 2023-01-09 klenkes Implement image handling for detachments.
        image = new TextField();
        binder.bind(image, "image");
    }

    private void updateFields(final Locale locale) {
        translate(locale, dispatchId, "input.id");
        translate(locale, code, "dispatch.code");
        translate(locale, name, "dispatch.title");
        translate(locale, description, "dispatch.description");
        translate(locale, xp, "input.xp");
        translate(locale, payment, "input.payment");
        translate(locale, image, "dispatch.cover_url");

        log.trace("component translated. presenter={}, view={}, tab={}", presenter, view, this);
    }

    private void translate(final Locale locale, Component component, final String code) {
        if (component == null) {
            return;
        }

        if (HasLabel.class.isAssignableFrom(component.getClass())) {
            ((HasLabel)component).setLabel(getTranslation(locale, code + ".caption"));
        }

        if (HasHelper.class.isAssignableFrom(component.getClass())) {
            ((HasHelper)component).setHelperText(getTranslation(locale, code + ".help"));
        }
    }

    @Override
    public void onDetach(DetachEvent detachEvent) {

    }


    @Override
    public String getI18nKey() {
        return I18N_KEY;
    }
}
