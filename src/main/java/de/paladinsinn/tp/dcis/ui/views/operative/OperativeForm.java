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

package de.paladinsinn.tp.dcis.ui.views.operative;

import com.sun.istack.NotNull;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.RouteParam;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.server.VaadinSession;
import de.paladinsinn.tp.dcis.data.Clearance;
import de.paladinsinn.tp.dcis.data.Cosm;
import de.paladinsinn.tp.dcis.data.operative.Operative;
import de.paladinsinn.tp.dcis.data.person.Person;
import de.paladinsinn.tp.dcis.data.person.PersonRepository;
import de.paladinsinn.tp.dcis.security.LoggedInUser;
import de.paladinsinn.tp.dcis.ui.components.Avatar;
import de.paladinsinn.tp.dcis.ui.components.PersonSelector;
import de.paladinsinn.tp.dcis.ui.components.Token;
import de.paladinsinn.tp.dcis.ui.components.TorgActionBar;
import de.paladinsinn.tp.dcis.ui.i18n.TranslatableComponent;
import de.paladinsinn.tp.dcis.ui.views.person.PersonEditView;
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
 * PersonForm -- Edits/displays the data for a person.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-04-05
 */
@Service
@Scope(SCOPE_PROTOTYPE)
public class OperativeForm extends Composite<Div> implements LocaleChangeObserver, TranslatableComponent, Serializable, AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(OperativeForm.class);

    /**
     * The service for writing data to.
     */
    private final OperativeService operativeService;

    private final PersonRepository personRepository;

    private final LoggedInUser user;

    /**
     * The locale of this form.
     */
    private Locale locale;

    /**
     * The mission report to edit.
     */
    private Operative data;

    /**
     * If this form is alread initialized.
     */
    private boolean initialized = false;

    // The form components.
    private final FormLayout form = new FormLayout();

    private final TextField id = new TextField();

    private final TextField name = new TextField();
    private final TextField lastName = new TextField();
    private final TextField firstName = new TextField();

    private Avatar avatar;
    private Token token;

    private ComboBox<Person> player;
    private final ComboBox<Clearance> clearance = new ComboBox<>();
    private final IntegerField xp = new IntegerField();
    private final ComboBox<Cosm> cosm = new ComboBox<>();

    private TorgActionBar actions;


    @Autowired
    public OperativeForm(
            @NotNull final OperativeService operativeService,
            @NotNull final PersonRepository personRepository,
            @NotNull final LoggedInUser user) {
        this.operativeService = operativeService;
        this.personRepository = personRepository;
        this.user = user;

        init();
    }


    private void init() {
        if (initialized || data == null || user == null) {
            LOG.debug(
                    "Already initialized or data missing. initialized={}, data={}, user={}",
                    initialized, data, user
            );

            return;
        }

        if (locale == null) {
            setLocale(VaadinSession.getCurrent().getLocale());
        }

        player = new PersonSelector("operative.player", personRepository, user);
        player.setReadOnly(!user.isAdmin() || !user.isOrga() || !user.isJudge());

        user.allow(data.getPlayer().equals(user.getPerson()));

        addListener(OperativeSaveEvent.class, operativeService);

        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("1px", 1),
                new FormLayout.ResponsiveStep("400px", 2),
                new FormLayout.ResponsiveStep("800px", 3)
        );

        id.setReadOnly(true);
        id.setRequiredIndicatorVisible(true);

        name.setReadOnly(user.isReadonly());
        name.setRequired(true);
        name.setRequiredIndicatorVisible(true);
        lastName.setReadOnly(user.isReadonly());
        lastName.setRequired(true);
        lastName.setRequiredIndicatorVisible(true);
        firstName.setReadOnly(user.isReadonly());
        firstName.setRequired(true);
        firstName.setRequiredIndicatorVisible(true);

        cosm.setReadOnly(user.isReadonly());
        cosm.setDataProvider(Cosm.CORE_EARTH.dataProvider());
        cosm.setItemLabelGenerator((ItemLabelGenerator<Cosm>) item -> getTranslation("torg.cosm." + item.name()));
        cosm.setAllowCustomValue(false);
        cosm.setRequired(true);
        cosm.setRequiredIndicatorVisible(true);

        xp.setReadOnly(!user.isAdmin() && !user.isOrga() && !user.isJudge());
        xp.setRequiredIndicatorVisible(true);

        clearance.setReadOnly(!user.isAdmin() && !user.isOrga() && !user.isJudge());
        clearance.setDataProvider(Clearance.ANY.dataProvider());
        clearance.setItemLabelGenerator((ItemLabelGenerator<Clearance>) item -> getTranslation("torg.clearance." + item.name()));
        clearance.setAllowCustomValue(false);
        clearance.setRequired(true);
        clearance.setRequiredIndicatorVisible(true);


        avatar = new Avatar(
                "operative.avatar",
                data,
                50, 500,
                50, 500,
                16777215
        );

        token = new Token(
                "operative.token",
                data,
                70, 210,
                70, 210,
                16777215
        );

        actions = new TorgActionBar(
                "buttons",
                event -> {
                    scrape();
                    getEventBus().fireEvent(new OperativeSaveEvent(this, false));
                    getUI().ifPresent(ui -> ui.getPage().getHistory().back());
                },
                null,
                null,
                null
        );
        actions.addAction(
                "person.editor",
                event -> {
                    getUI().ifPresent(ui -> ui.navigate(
                            PersonEditView.class,
                            new RouteParameters(
                                    new RouteParam("id", data.getPlayer().getId().toString())
                            )
                    ));
                }
        );
        actions.setReadOnly(
                user.isReadonly()
                        && !user.getPerson().equals(data.getPlayer())
                        && !user.isAdmin()
                        && !user.isOrga()
                        && !user.isJudge()
        );

        getContent().add(form);

        // mark as initialized.
        initialized = true;
    }

    private void scrape() {
        init();

        if (data == null) data = new Operative();

        data.setPlayer(player.getValue());
        data.setXp(xp.getValue());
        data.setClearance(clearance.getValue());
        data.setCosm(cosm.getValue());
        data.setName(name.getValue());
        data.setFirstName(firstName.getValue());
        data.setLastName(lastName.getValue());
    }

    public void setData(@NotNull Operative data) {
        if (this.data != null && this.data.equals(data)) {
            LOG.info("Data didn't change. Ignoring event. id={}, name={}",
                    this.data.getId(), this.data.getName());

            return;
        }

        this.data = data;

        LOG.debug("Set data. id={}, name={}", this.data.getId(), this.data.getName());

        init();
        populate();
        translate();
    }

    private void populate() {
        init();

        if (data == null || !initialized) {
            LOG.warn("Tried to polulate form data without a person defined.");
            return;
        }

        if (data.getId() != null) {
            id.setValue(data.getId().toString());
        }

        bindData(data.getPlayer(), player);
        bindData(data.getXp(), xp);
        bindData(data.getClearance(), clearance);
        bindData(data.getCosm(), cosm);

        bindData(data.getName(), name);
        bindData(data.getLastName(), lastName);
        bindData(data.getFirstName(), firstName);

        avatar.setValue(data);
        token.setValue(data);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void bindData(final Object data, @NotNull HasValue component) {
        if (data != null) {
            component.setValue(data);
        }
    }

    public Optional<Operative> getData() {
        return Optional.ofNullable(data);
    }


    @Override
    public void localeChange(LocaleChangeEvent event) {
        LOG.trace("Change locale event. locale={}", event.getLocale());

        setLocale(event.getLocale());
    }

    @Override
    public void translate() {
        init();

        if (!initialized || locale == null) {
            return;
        }

        LOG.debug("Operative edit form. operative={}, player={}, user={}, locale={}", data, data.getPlayer(), user.getPerson(), locale);

        LOG.trace("Remove and add all form elements.");
        form.removeAll();

        // Form fields
        id.setLabel(getTranslation("input.id.caption"));
        id.setHelperText(getTranslation("input.id.help"));


        LOG.trace("Adding all form elements.");

        name.setLabel(getTranslation("operative.name.caption"));
        name.setHelperText(getTranslation("operative.name.help"));


        clearance.setLabel(getTranslation("torg.clearance.caption"));
        clearance.setHelperText(getTranslation("torg.clearance.help"));

        xp.setLabel(getTranslation("operative.xp.caption"));
        xp.setHelperText(getTranslation("operative.xp.help"));

        cosm.setLabel(getTranslation("torg.cosm.caption"));
        cosm.setHelperText(getTranslation("torg.cosm.help"));

        lastName.setLabel(getTranslation("operative.last-name.caption"));
        lastName.setHelperText(getTranslation("operative.last-name.help"));
        firstName.setLabel(getTranslation("operative.first-name.caption"));
        firstName.setHelperText(getTranslation("operative.first-name.help"));

        player.setLabel(getTranslation("operative.player.caption"));
        player.setHelperText(getTranslation("operative.player.help"));

        avatar.setLocale(locale);
        token.setLocale(locale);

        actions.setLocale(locale);


        form.add(name, cosm);
        form.setColspan(name, 2);

        form.add(xp, clearance);
        form.addFormItem(avatar, getTranslation("operative.avatar.caption"));

        form.add(lastName, firstName);
        form.addFormItem(token, getTranslation("operative.token.caption"));

        form.add(actions);
        form.setColspan(actions, 3);

        getContent().removeAll();
        getContent().add(form);
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
