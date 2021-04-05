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

package de.paladinsinn.delphicouncil.ui.forms.persons;

import com.sun.istack.NotNull;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.server.VaadinSession;
import de.paladinsinn.delphicouncil.app.events.PersonSaveEvent;
import de.paladinsinn.delphicouncil.app.i18n.TranslatableComponent;
import de.paladinsinn.delphicouncil.data.person.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;

import static com.vaadin.flow.component.Unit.PIXELS;
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
    private Authentication userDetails;

    /**
     * The locale of this form.
     */
    private Locale locale;

    /**
     * The mission report to edit.
     */
    private Person data;

    /**
     * If the form is read-only.
     */
    private boolean readonly = true;

    /**
     * If the logged in user is an admin.
     */
    private boolean isAdmin = false;

    /**
     * If the logged in user is a member of the orga team.
     */
    private boolean isOrga = false;

    /**
     * if the logged in user is a judge.
     */
    private boolean isJudge = false;

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

    private final CheckboxGroup<String> roles = new CheckboxGroup<>();

    private final CheckboxGroup<String> status = new CheckboxGroup<>();

    private final CheckboxGroup<String> userChangeableFlags = new CheckboxGroup<>();

    private final Image avatar = new Image();
    private final MemoryBuffer avatarBuffer = new MemoryBuffer();
    private final Upload avatarUpload = new Upload(avatarBuffer);


    private final DateTimePicker lastLogin = new DateTimePicker();
    private final DateTimePicker deleted = new DateTimePicker();
    private final DateTimePicker lastPasswordChange = new DateTimePicker();
    private final DateTimePicker expiryDate = new DateTimePicker();

    private final HorizontalLayout actions = new HorizontalLayout();
    private final Button save = new Button();
    private final Button reset = new Button();


    @Autowired
    public PersonForm(
            @NotNull final PersonService personService
    ) {
        this.personService = personService;
    }


    @PostConstruct
    public void init() {
        if (data == null) {
            LOG.debug("Can't initialize without having a mission. form={}", this);
            return;
        }

        if (initialized) {
            LOG.debug("Already initialized. form={}", this);
            return;
        }

        if (locale == null) {
            locale = VaadinSession.getCurrent().getLocale();
        }

        addListener(PersonSaveEvent.class, personService);

        readUserDetails();

        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("1px", 1),
                new FormLayout.ResponsiveStep("400px", 2),
                new FormLayout.ResponsiveStep("800px", 3)
        );

        id.setReadOnly(true);

        username.setReadOnly(!isAdmin);
        username.setRequired(true);
        password.setReadOnly(readonly);
        password.setRequired(true);
        email.setReadOnly(readonly);

        name.setReadOnly(readonly);
        name.setRequired(true);
        lastName.setReadOnly(readonly);
        lastName.setRequired(true);
        firstName.setReadOnly(readonly);
        firstName.setRequired(true);

        expiryDate.setReadOnly(!isAdmin);
        lastLogin.setReadOnly(true);
        lastPasswordChange.setReadOnly(true);
        deleted.setReadOnly(!isAdmin);

        status.setReadOnly(!isAdmin && !isOrga && !isJudge);

        userChangeableFlags.setReadOnly(readonly);

        roles.setItems(RoleName.PERSON.getRoleNamesWithoutGM());
        roles.setReadOnly(!isAdmin && !isOrga);

        avatar.setMaxWidth(360, PIXELS);
        avatar.setMaxHeight(660, PIXELS);
        avatarUpload.setMaxFiles(1);
        avatarUpload.setMaxFileSize(16777215);
        avatarUpload.setDropAllowed(true);
        avatarUpload.setAcceptedFileTypes("image/png", "image/jpg", "image/jpeg", "image/gif");
        avatarUpload.addFinishedListener(e -> {
            try {
                data.setAvatar(avatarBuffer.getInputStream());

                Notification.show(
                        getTranslation("input.upload.success", avatarBuffer.getFileName()),
                        1000,
                        Notification.Position.BOTTOM_STRETCH
                );
            } catch (IOException ioException) {
                LOG.error(
                        "Upload of the avatar failed. form={}, file='{}', type='{}'",
                        this, avatarBuffer.getFileData(), avatarBuffer.getFileData().getMimeType()
                );
                Notification.show(
                        getTranslation("input.upload.failed", ioException.getLocalizedMessage()),
                        2000,
                        Notification.Position.BOTTOM_STRETCH
                );
            }
        });


        save.getStyle().set("marginRight", "10px");
        save.addClickListener(event -> {
            if (data == null) data = new Person();

            data.setUsername(username.getValue());

            if (!password.isEmpty()) {
                LOG.info("Changing password. form={}, user='{}'", this, data.getUsername());
                data.setPassword(password.getValue());
            }

            data.setEmail(email.getValue());

            data.setName(name.getValue());
            LOG.info("Changing name. form={}, name='{}'", this, data.getName());

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

            Role gmRole = new Role(RoleName.GM);
            data.disableGravatar();
            boolean isGm = false,
                    allowGravatar = false;
            for (String b : userChangeableFlags.getValue()) {
                if (getTranslation("person.status-is-gm.caption").equals(b)) {
                    isGm = true;
                } else if (getTranslation("person.status-allow-gravatar.caption").equals(b)) {
                    allowGravatar = true;
                }
            }
            if (isGm) {
                data.getRoles().add(gmRole);
            } else {
                data.getRoles().remove(gmRole);
            }
            if (allowGravatar) {
                data.enableGravatar();
            } else {
                data.disableGravatar();
            }

            if (deleted.getValue() != null) {
                data.getStatus().setDeleted(deleted.getValue().atOffset(UTC));
            }

            getEventBus().fireEvent(new PersonSaveEvent(this, false));
        });

        reset.addClickListener(event -> {
            // FIXME 2021-04-04 rlichti implement resetting the data.
        });

        getContent().add(form);

        // mark as initialized.
        initialized = true;
    }

    public void setData(@NotNull Person data) {
        if (this.data != null && this.data.equals(data)) {
            LOG.info("Person didn't change. Ignoring event. form={}, id={}, name={}",
                    this, this.data.getId(), this.data.getName());

            return;
        }

        this.data = data;

        LOG.debug("Set data. form={}, id={}, name={}",
                this, this.data.getId(), this.data.getName());


        init();
        populate();
        translate();
    }

    private void populate() {
        if (data == null) {
            LOG.warn("Tried to polulate form data without a person defined. form={}", this);
            return;
        }

        if (data.getId() != null) {
            id.setValue(data.getId().toString());
        }
        username.setValue(data.getUsername());
        email.setValue(data.getEmail());

        avatar.setSrc(data.getAvatarImage().getSrc());

        name.setValue(data.getName());
        lastName.setValue(data.getLastname());
        firstName.setValue(data.getFirstname());

        lastLogin.setValue(data.getStatus().getLastLogin().toLocalDateTime());
        expiryDate.setValue(data.getStatus().getExpiry().toLocalDateTime());
        lastPasswordChange.setValue(data.getStatus().getCredentialsChange().toLocalDateTime());

        roles.setValue(RoleName.PERSON.getActiveRoleNames(data));

        if (roles.getValue().contains(RoleName.GM.name())) {
            userChangeableFlags.add(getTranslation("person.status-is-gm.caption"));
        }
        if (data.isGravatarAllowed()) {
            userChangeableFlags.add(getTranslation("person.status-allow-gravatar.caption"));
        }

        if (data.getAvatar() != null)
            avatar.setSrc(data.getAvatar());

        calculateReadOnly();
    }

    public void initializeReport() {
        LOG.debug("Creating a new person.");

        data = new Person();
        data.setCreated();
        data.setModified();

        AccountSecurityStatus status = new AccountSecurityStatus();
        status.setEnabled(true);
        status.setLocked(false);
        status.setExpiry(OffsetDateTime.now(UTC).plusYears(5));
        data.setStatus(status);

        init();
        populate();
        translate();
    }


    public Optional<Person> getData() {
        return Optional.ofNullable(data);
    }


    private void readUserDetails() {
        if (userDetails == null) {
            try {
                userDetails = SecurityContextHolder.getContext().getAuthentication();
            } catch (NullPointerException e) {
                LOG.warn("No user is logged in. Can't load user details. view={}", this);
            }
        }

        calculateReadOnly();

    }

    private void calculateReadOnly() {
        if (userDetails == null) {
            LOG.warn("Can't calculate the permissions for this form. There is no logged in user. form={}", this);
            return;
        }

        readonly = !userDetails.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ORGA.name()))
                && !userDetails.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ADMIN.name()))
                && !(data != null && userDetails.getName().equals(data.getUsername()));

        isOrga = userDetails.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ORGA.name()));
        isAdmin = userDetails.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ADMIN.name()));
        isJudge = userDetails.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.JUDGE.name()));

        LOG.debug("User access calculation done. readonly={}, isAdmin={}", readonly, isAdmin);
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        LOG.trace("Change locale event. form={}, locale={}", this, event.getLocale());

        setLocale(event.getLocale());
    }

    @Override
    public void translate() {
        if (userDetails == null || locale == null) {
            return;
        }

        LOG.debug("Building person edit form. form={}, report={}, locale={}", this, data, locale);

        LOG.trace("Remove and add all form elements. form={}", this);
        form.removeAll();

        // Form fields
        id.setTitle(getTranslation("input.id.caption"));
        id.setHelperText(getTranslation("input.id.help"));


        LOG.trace("Adding all form elements. form={}", this);

        avatarUpload.setDropLabelIcon(avatar);
        form.addFormItem(avatarUpload, getTranslation("person.avatar.caption"));

        name.setTitle(getTranslation("person.name.caption"));
        name.setHelperText(getTranslation("person.name.help"));
        form.addFormItem(name, name.getTitle());

        email.setTitle(getTranslation("person.email.caption"));
        email.setHelperText(getTranslation("person.email.help"));
        form.addFormItem(email, email.getTitle());

        username.setTitle(getTranslation("person.username.caption"));
        username.setHelperText(getTranslation("person.username.help"));
        form.addFormItem(username, username.getTitle());
        password.setTitle(getTranslation("person.password.caption"));
        password.setHelperText(getTranslation("person.password.help"));
        form.addFormItem(password, password.getTitle());

        roles.setHelperText(getTranslation("person.roles.help"));
        form.addFormItem(roles, getTranslation("person.roles.caption"));

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
        status.setItems(Arrays.asList(statusEnabled, statusLocked));
        status.setValue(statusOptionsSelected);
        form.addFormItem(status, getTranslation("person.status.caption"));

        final String userChangeableFlagIsGm = getTranslation("person.status-is-gm.caption");
        final String userChangeableFlagAllowGravatar = getTranslation("person.status-allow-gravatar.caption");
        final HashSet<String> userChangeableFlagSelected = new HashSet<>(2);
        if (data.isGravatarAllowed()) {
            userChangeableFlagSelected.add(getTranslation("person.status-allow-gravatar.caption"));
        }
        if (roles.getValue().contains(RoleName.GM.name())) {
            userChangeableFlagSelected.add(getTranslation("person.status-is-gm.caption"));
        }
        userChangeableFlags.removeAll();
        userChangeableFlags.setItems(Arrays.asList(userChangeableFlagIsGm, userChangeableFlagAllowGravatar));
        userChangeableFlags.setValue(userChangeableFlagSelected);
        form.addFormItem(userChangeableFlags, getTranslation("person.user-changeable-flags.caption"));

        lastName.setTitle(getTranslation("person.last-name.caption"));
        lastName.setHelperText(getTranslation("person.last-name.help"));
        form.addFormItem(lastName, lastName.getTitle());
        firstName.setTitle(getTranslation("person.first-name.caption"));
        firstName.setHelperText(getTranslation("person.first-name.help"));
        form.addFormItem(firstName, firstName.getTitle());
        deleted.setHelperText(getTranslation("person.deleted.help"));
        form.addFormItem(deleted, deleted.getLabel());
        expiryDate.setHelperText(getTranslation("person.expiry-date.help"));
        form.addFormItem(expiryDate, expiryDate.getLabel());
        lastPasswordChange.setHelperText(getTranslation("person.last-password-change.help"));
        form.addFormItem(lastPasswordChange, lastPasswordChange.getLabel());
        lastLogin.setHelperText(getTranslation("person.last-login.help"));
        form.addFormItem(lastLogin, lastLogin.getLabel());


        // Buttons
        if (!readonly) {
            save.setText(getTranslation("buttons.save.caption"));
            reset.setText(getTranslation("buttons.reset.caption"));

            actions.removeAll();
            actions.add(save, reset);
            LOG.trace("Adding action buttons. form={}", this);
            form.add(actions);
        }

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
            LOG.debug("Locale has not changed. Ignoring event. form={}, locale={}", this, locale);
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
        LOG.debug("Closing form. form={}", this);
        getContent().removeAll();
        status.removeAll();
        roles.removeAll();
        form.removeAll();
    }
}
