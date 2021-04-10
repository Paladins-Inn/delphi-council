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

package de.paladinsinn.tp.dcis.ui.views.person;

import ch.carnet.kasparscherrer.LanguageSelect;
import com.sun.istack.NotNull;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.server.VaadinSession;
import de.paladinsinn.tp.dcis.data.person.Person;
import de.paladinsinn.tp.dcis.data.person.Role;
import de.paladinsinn.tp.dcis.data.person.RoleName;
import de.paladinsinn.tp.dcis.security.LoggedInUser;
import de.paladinsinn.tp.dcis.ui.components.Avatar;
import de.paladinsinn.tp.dcis.ui.components.TorgActionBar;
import de.paladinsinn.tp.dcis.ui.i18n.I18nSelector;
import de.paladinsinn.tp.dcis.ui.i18n.TranslatableComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;

import static java.time.ZoneOffset.UTC;
import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

/**
 * PersonForm -- Edits/displays the data for a person.
 *
 * @author klenkes74 {literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-04-05
 */
@Service
@Scope(SCOPE_PROTOTYPE)
public class PersonForm extends Composite<Div> implements LocaleChangeObserver, TranslatableComponent, Serializable, AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(PersonForm.class);

    /**
     * The service for writing data to.
     */
    private final PersonService personService;

    /**
     * Details of the logged in user.
     */
    private final LoggedInUser user;

    /**
     * The locale of this form.
     */
    private Locale locale;

    /**
     * The mission report to edit.
     */
    private Person data;



    /**
     * If this form is alread initialized.
     */
    private boolean initialized = false;

    // The form components.
    private final FormLayout form = new FormLayout();

    private final TextField id = new TextField();

    private final TextField username = new TextField();
    private final PasswordField password = new PasswordField();
    private final EmailField email = new EmailField();

    private final TextField name = new TextField();
    private final TextField lastName = new TextField();
    private final TextField firstName = new TextField();

    private final LanguageSelect language = new I18nSelector();

    private final CheckboxGroup<String> roles = new CheckboxGroup<>();
    private final CheckboxGroup<String> status = new CheckboxGroup<>();
    private final CheckboxGroup<String> flags = new CheckboxGroup<>();

    private Avatar avatar;

    private final DateTimePicker lastLogin = new DateTimePicker();
    private final DateTimePicker deleted = new DateTimePicker();
    private final DateTimePicker lastPasswordChange = new DateTimePicker();
    private final DateTimePicker expiryDate = new DateTimePicker();

    private TorgActionBar actions;


    @Autowired
    public PersonForm(
            @NotNull final PersonService personService,
            LoggedInUser user) {
        this.personService = personService;
        this.user = user;
    }


    @PostConstruct
    public void init() {
        if (data == null) {
            LOG.debug("Can't initialize without having a mission.");
            return;
        }

        if (initialized) {
            LOG.debug("Already initialized.");
            return;
        }

        if (locale == null) {
            locale = VaadinSession.getCurrent().getLocale();
        }

        addListener(PersonSaveEvent.class, personService);

        user.allow(user.getPerson().getName().equals(data.getUsername()));

        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("1px", 1),
                new FormLayout.ResponsiveStep("400px", 2),
                new FormLayout.ResponsiveStep("800px", 3)
        );

        id.setReadOnly(true);

        username.setReadOnly(user.isAdmin());
        username.setRequired(true);
        password.setReadOnly(user.isReadonly());
        password.setRequired(true);
        email.setReadOnly(user.isReadonly());

        name.setReadOnly(user.isReadonly());
        name.setRequired(true);
        lastName.setReadOnly(user.isReadonly());
        lastName.setRequired(true);
        firstName.setReadOnly(user.isReadonly());
        firstName.setRequired(true);

        language.setReadOnly(user.isReadonly());

        expiryDate.setReadOnly(!user.isAdmin());
        lastLogin.setReadOnly(true);
        lastPasswordChange.setReadOnly(true);
        deleted.setReadOnly(!user.isAdmin());

        status.setReadOnly(!user.isAdmin() && !user.isOrga() && !user.isJudge());

        flags.setReadOnly(user.isReadonly());

        roles.setItems(RoleName.PERSON.getRoleNamesWithoutGM());
        roles.setReadOnly(!user.isAdmin() && !user.isOrga());

        avatar = new Avatar(
                "person.avatar",
                data,
                50, 150,
                50, 150,
                16777215
        );

        actions = new TorgActionBar(
                "buttons",
                event -> {
                    scrape();
                    getEventBus().fireEvent(new PersonSaveEvent(this, false));
                },
                null,
                null,
                null
        );
        actions.setReadOnly(
                user.isReadonly()
                        && !user.getPerson().equals(data)
                        && !user.isAdmin()
        );

        getContent().add(form);

        // mark as initialized.
        initialized = true;
    }

    public void setData(@NotNull Person data) {
        if (this.data != null && this.data.equals(data)) {
            LOG.info("Person didn't change. Ignoring event. id={}, name={}",
                    this.data.getId(), this.data.getName());

            return;
        }

        this.data = data;

        LOG.debug("Set data. id={}, name={}",
                this.data.getId(), this.data.getName());


        init();
        populate();
        translate();
    }

    private void populate() {
        if (data == null) {
            LOG.warn("Tried to polulate form data without a person defined.");
            return;
        }

        if (data.getId() != null) {
            id.setValue(data.getId().toString());
        }
        username.setValue(data.getUsername());
        email.setValue(data.getEmail());

        avatar.setValue(data);

        name.setValue(data.getName());
        lastName.setValue(data.getLastname());
        firstName.setValue(data.getFirstname());

        lastLogin.setValue(data.getStatus().getLastLogin().toLocalDateTime());
        expiryDate.setValue(data.getStatus().getExpiry().toLocalDateTime());
        lastPasswordChange.setValue(data.getStatus().getCredentialsChange().toLocalDateTime());

        roles.setValue(RoleName.PERSON.getActiveRoleNames(data));

        if (roles.getValue().contains(RoleName.GM.name())) {
            flags.add(getTranslation("person.status-is-gm.caption"));
        }
        if (data.isGravatarAllowed()) {
            flags.add(getTranslation("person.status-allow-gravatar.caption"));
        }
        flags.addSelectionListener(e -> {
            roles.getValue().remove(RoleName.GM.name());

            for (String s : e.getAllSelectedItems()) {
                if (s.equals(getTranslation("person.status-is-gm.caption"))) {
                    roles.getValue().add(RoleName.GM.name());
                }
            }
        });

        language.setValue(data.getLocale());
    }

    private void scrape() {
        if (data == null) data = new Person();

        data.setUsername(username.getValue());

        if (!password.isEmpty()) {
            LOG.info("Changing password. user='{}'", data.getUsername());
            data.setPassword(password.getValue());
        }

        data.setEmail(email.getValue());

        data.setName(name.getValue());
        LOG.info("Changing name. name='{}'", data.getName());

        data.setLastname(lastName.getValue());
        data.setFirstname(firstName.getValue());

        data.getStatus().setLocked(false);
        data.getStatus().setEnabled(true);
        for (String b : status.getValue()) {
            if (getTranslation("person.status-locked.caption").equals(b)) {
                data.getStatus().setLocked(true);
            } else if (getTranslation("person.status-enabled.caption").equals(b)) {
                data.getStatus().setEnabled(true);
            }
        }
        data.getStatus().setLastLogin(lastLogin.getValue().atOffset(UTC));
        data.getStatus().setCredentialsChange(lastPasswordChange.getValue().atOffset(UTC));
        data.getStatus().setExpiry(expiryDate.getValue().atOffset(UTC));

        data.getRoles().clear();
        for (String b : roles.getValue()) {
            data.getRoles().add(new Role(RoleName.valueOf(b)));
        }

        data.disableGravatar();

        for (String b : flags.getValue()) {
            if (getTranslation("person.status-allow-gravatar.caption").equals(b)) {
                data.enableGravatar();
            }
        }

        if (deleted.getValue() != null) {
            data.getStatus().setDeleted(deleted.getValue().atOffset(UTC));
        }

        data.setLocale(language.getValue());
    }

    public Optional<Person> getData() {
        return Optional.ofNullable(data);
    }


    @Override
    public void localeChange(LocaleChangeEvent event) {
        LOG.trace("Change locale event. locale={}", event.getLocale());

        setLocale(event.getLocale());
    }

    @Override
    public void translate() {
        if (user == null || locale == null) {
            return;
        }

        LOG.debug("Building person edit form. data={}, locale={}", data, locale);

        LOG.trace("Remove and add all form elements.");
        form.removeAll();

        // Form fields
        LOG.trace("Adding all form elements.");
        id.setLabel(getTranslation("input.id.caption"));
        id.setHelperText(getTranslation("input.id.help"));

        name.setLabel(getTranslation("person.name.caption"));
        name.setHelperText(getTranslation("person.name.help"));

        email.setLabel(getTranslation("person.email.caption"));
        email.setHelperText(getTranslation("person.email.help"));

        username.setLabel(getTranslation("person.username.caption"));
        username.setHelperText(getTranslation("person.username.help"));
        password.setLabel(getTranslation("person.password.caption"));
        password.setHelperText(getTranslation("person.password.help"));

        roles.setLabel(getTranslation("person.roles.caption"));
        roles.setHelperText(getTranslation("person.roles.help"));

        String statusEnabled = getTranslation("person.status-enabled.caption");
        String statusLocked = getTranslation("person.status-locked.caption");
        HashSet<String> statusOptionsSelected = new HashSet<>(2);
        if (data.isEnabled()) {
            statusOptionsSelected.add(getTranslation("person.status-enabled.caption"));
        }
        if (!data.isAccountNonLocked()) {
            statusOptionsSelected.add(getTranslation("person.status-locked.caption"));
        }
        status.removeAll();
        status.setLabel(getTranslation("person.status.caption"));
        status.setHelperText(getTranslation("person.status.help"));
        status.setItems(Arrays.asList(statusEnabled, statusLocked));
        status.setValue(statusOptionsSelected);

        final String userChangeableFlagIsGm = getTranslation("person.status-is-gm.caption");
        final String userChangeableFlagAllowGravatar = getTranslation("person.status-allow-gravatar.caption");
        final HashSet<String> userChangeableFlagSelected = new HashSet<>(2);
        if (data.isGravatarAllowed()) {
            userChangeableFlagSelected.add(getTranslation("person.status-allow-gravatar.caption"));
        }
        if (roles.getValue().contains(RoleName.GM.name())) {
            userChangeableFlagSelected.add(getTranslation("person.status-is-gm.caption"));
        }
        flags.removeAll();
        flags.setLabel(getTranslation("person.user-changeable-flags.caption"));
        flags.setItems(Arrays.asList(userChangeableFlagIsGm, userChangeableFlagAllowGravatar));
        flags.setValue(userChangeableFlagSelected);

        lastName.setLabel(getTranslation("person.last-name.caption"));
        lastName.setHelperText(getTranslation("person.last-name.help"));
        firstName.setLabel(getTranslation("person.first-name.caption"));
        firstName.setHelperText(getTranslation("person.first-name.help"));
        deleted.setLabel(getTranslation("person.deleted.caption"));
        deleted.setHelperText(getTranslation("person.deleted.help"));
        expiryDate.setLabel(getTranslation("person.expiry-date.caption"));
        expiryDate.setHelperText(getTranslation("person.expiry-date.help"));
        lastPasswordChange.setLabel(getTranslation("person.last-password-change.caption"));
        lastPasswordChange.setHelperText(getTranslation("person.last-password-change.help"));
        lastLogin.setLabel(getTranslation("person.last-login.caption"));
        lastLogin.setHelperText(getTranslation("person.last-login.help"));


        FormLayout.FormItem avatarItem = form.addFormItem(avatar, getTranslation("person.avatar.caption"));
        form.add(username, password, status,
                email, flags,
                name, language,
                lastName, firstName, new Span(),
                roles, expiryDate,
                lastPasswordChange, lastLogin, deleted);

        actions.translate();
        form.add(actions);

        form.setColspan(avatarItem, 3);
        form.setColspan(email, 2);
        form.setColspan(name, 2);
        form.setColspan(roles, 2);
        form.setColspan(actions, 3);

        getContent().removeAll();
        getContent().add(form);
    }

    public void displayNote(@NotNull final String i18nkey, @NotNull final String type, Object... parameters) {
        LOG.trace("Displaying notification. i18nKey='{}', type='{}', parameter={}", i18nkey, type, parameters);

        Notification.show(
                getTranslation(i18nkey, getTranslation(type), parameters),
                2000,
                Notification.Position.BOTTOM_STRETCH
        );
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
        status.removeAll();
        roles.removeAll();
        form.removeAll();
    }
}
