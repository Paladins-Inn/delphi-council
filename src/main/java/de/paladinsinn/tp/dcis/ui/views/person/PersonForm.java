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
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
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
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-04-05
 */
@Service
@Scope(SCOPE_PROTOTYPE)
public class PersonForm extends Div implements LocaleChangeObserver, TranslatableComponent, Serializable, AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(PersonForm.class);

    /**
     * The service for writing data to.
     */
    private final PersonSaveService personSaveService;

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
    private Person oldData;


    /**
     * If this form is alread initialized.
     */
    private boolean initialized = false;

    // The form components.
    private FormLayout form;

    private TextField id;

    private TextField username;
    private PasswordField password;
    private EmailField email;

    private TextField name;
    private TextField lastName;
    private TextField firstName;

    private LanguageSelect language;

    private CheckboxGroup<String> roles;
    private CheckboxGroup<String> status;
    private CheckboxGroup<String> flags;

    private Avatar avatar;

    private DateTimePicker lastLogin;
    private DateTimePicker deleted;
    private DateTimePicker lastPasswordChange;
    private DateTimePicker expiryDate;

    private TorgActionBar actions;


    @Autowired
    public PersonForm(
            @NotNull final PersonSaveService personSaveService,
            LoggedInUser user) {
        this.personSaveService = personSaveService;
        this.user = user;
    }

    public void init() {
        if (initialized || data == null || user == null) {
            LOG.debug("Already initialized or not initializable. initialized={}, data={}, user={}, locale={}",
                    initialized, data, user, locale);
            return;
        }

        addListener(PersonSaveEvent.class, personSaveService);
        ensureLocale();

        LOG.debug("initializing. data={}, user={}, locale={}", data, user, locale);

        form = new FormLayout();
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("1px", 1),
                new FormLayout.ResponsiveStep("400px", 2),
                new FormLayout.ResponsiveStep("800px", 3)
        );

        id = generateTextField(true, false);
        username = generateTextField(!user.isAdmin(), true);
        password = generatePasswordField(user.isReadonly(), false);
        email = generateEmailField(user.isReadonly(), true);
        name = generateTextField(user.isReadonly(), true);
        lastName = generateTextField(user.isReadonly(), true);
        firstName = generateTextField(user.isReadonly(), true);

        language = new I18nSelector(data.getLocale());
        language.setRequiredIndicatorVisible(true);
        lastLogin = new DateTimePicker();
        deleted = new DateTimePicker();
        lastPasswordChange = new DateTimePicker();
        expiryDate = new DateTimePicker();
        expiryDate.setRequiredIndicatorVisible(true);

        status = new CheckboxGroup<>();

        flags = new CheckboxGroup<>();

        roles = new CheckboxGroup<>();
        roles.setItems(RoleName.PERSON.getRoleNamesWithoutGM());

        avatar = new Avatar(
                "person.avatar",
                data,
                50, 150,
                50, 150,
                16777215
        );
        FormLayout.FormItem avatarItem = form.addFormItem(avatar, getTranslation("person.avatar.caption"));

        actions = new TorgActionBar(
                "buttons",
                event -> { // save
                    scrape();
                    getEventBus().fireEvent(new PersonSaveEvent(this, false));
                },
                ev -> { // reset
                    data = oldData;
                    populate();
                },
                ev -> { // cancel
                    getUI().ifPresent(ui -> ui.getPage().getHistory().back());
                },
                null
        );


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
        add(form);

        // mark as initialized.
        initialized = true;
    }

    private EmailField generateEmailField(boolean readonly, boolean required) {
        EmailField result = new EmailField();

        result.setReadOnly(readonly);
        result.setRequiredIndicatorVisible(required);

        return result;
    }

    private PasswordField generatePasswordField(boolean readonly, boolean required) {
        PasswordField result = new PasswordField();

        result.setReadOnly(readonly);
        result.setRequired(required);
        result.setRequiredIndicatorVisible(required);

        return result;
    }

    private TextField generateTextField(boolean readonly, boolean required) {
        TextField result = new TextField();

        result.setReadOnly(readonly);
        result.setRequired(required);
        result.setRequiredIndicatorVisible(required);

        return result;
    }

    private void ensureLocale() {
        if (locale == null) {
            locale = VaadinSession.getCurrent().getLocale();
        }
    }

    public void setData(@NotNull Person data) {
        try {
            this.oldData = data.clone();
        } catch (CloneNotSupportedException e) {
            // should not happen
            LOG.error("All entities need to be clonable: data={}", data);
        }
        this.data = data;

        LOG.debug("Set data. id={}, name={}",
                this.data.getId(), this.data.getName());

        init();
        populate();
        recalculateReadOnly();
    }

    private void recalculateReadOnly() {
        user.allow(user.getPerson().getId().equals(data.getId()));

        password.setReadOnly(user.isReadonly());
        email.setReadOnly(user.isReadonly());
        name.setReadOnly(user.isReadonly());
        lastName.setReadOnly(user.isReadonly());
        firstName.setReadOnly(user.isReadonly());
        language.setReadOnly(user.isReadonly());
        lastLogin.setReadOnly(true);
        deleted.setReadOnly(!user.isAdmin());
        lastPasswordChange.setReadOnly(true);
        expiryDate.setReadOnly(!user.isAdmin());
        status.setReadOnly(!user.isAdmin() && !user.isOrga() && !user.isJudge());
        flags.setReadOnly(user.isReadonly());
        roles.setReadOnly(!user.isAdmin() && !user.isOrga());
        actions.setReadOnly(user.isReadonly()
                && !user.getPerson().equals(data)
                && !user.isAdmin()
        );
    }

    private void populate() {
        if (!initialized || data == null) {
            LOG.warn("Form is not initialized or data is not set. initialized={}, data={}", initialized, data);
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

        flags.addSelectionListener(e -> {
            roles.getValue().remove(RoleName.GM.name());
            data.disableGravatar();
            for (String s : e.getAllSelectedItems()) {
                if (s.equals(getTranslation("person.status-is-gm.caption"))) {
                    roles.getValue().add(RoleName.GM.name());
                }

                if (s.equals(getTranslation("person.status-allow-gravatar.caption"))) {
                    data.enableGravatar();
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
            Role role = new Role(RoleName.valueOf(b));
            data.addRole(role);

            LOG.debug("Add role: {} ({})", b, data.getRoles().contains(role));
        }

        data.disableGravatar();
        data.getRoles().remove(new Role(RoleName.GM));
        for (String b : flags.getValue()) {
            if (getTranslation("person.status-allow-gravatar.caption").equals(b)) {
                LOG.debug("Allow usage of gravatar");
                data.enableGravatar();
            }

            if (getTranslation("person.status-is-gm.caption").equals(b)) {
                Role role = new Role(RoleName.GM);
                data.addRole(role);

                LOG.debug("Add role: {} ({})", RoleName.GM, data.getRoles().contains(role));
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
    public void translate() {
        if (locale == null) {
            setLocale(VaadinSession.getCurrent().getLocale());
        }

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

        updateFlags();

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

        actions.translate();
    }

    private void updateFlags() {
        final HashSet<String> userChangeableFlagSelected = new HashSet<>(2);

        if (data.isGravatarAllowed()) {
            userChangeableFlagSelected.add(getTranslation("person.status-allow-gravatar.caption"));
        }
        if (roles.getValue().contains(RoleName.GM.name())) {
            userChangeableFlagSelected.add(getTranslation("person.status-is-gm.caption"));
        }

        flags.removeAll();
        flags.setLabel(getTranslation("person.user-changeable-flags.caption"));
        flags.setItems(Arrays.asList(
                getTranslation("person.status-is-gm.caption"),
                getTranslation("person.status-allow-gravatar.caption")
        ));
        flags.setValue(userChangeableFlagSelected);
    }


    @Override
    public void localeChange(LocaleChangeEvent event) {
        setLocale(event.getLocale());
    }

    @Override
    public void setLocale(Locale locale) {
        if (locale == null || (locale.equals(this.locale))) {
            LOG.debug("Locale already set or new locale would be null, old={}, new={}", this.locale, locale);
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
        removeAll();
        status.removeAll();
        roles.removeAll();
        form.removeAll();
    }
}
