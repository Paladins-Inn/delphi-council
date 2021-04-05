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
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
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
import java.io.Serializable;
import java.time.OffsetDateTime;
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
    private Authentication userDetails;

    /**
     * The locale of this form.
     */
    private Locale locale;

    /**
     * The mission report to edit.
     */
    private Person person;

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
        if (person == null) {
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

        roles.setItems(RoleName.PERSON.getRoleNames());
        roles.setReadOnly(!isAdmin && !isOrga);

        save.getStyle().set("marginRight", "10px");
        save.addClickListener(event -> {
            if (person == null) person = new Person();

            person.setUsername(username.getValue());

            if (!password.isEmpty()) {
                person.setPassword(password.getValue());
                person.getStatus().setCredentialsChange();
            }

            person.setEmail(email.getValue());

            person.setName(name.getValue());
            person.setLastname(lastName.getValue());
            person.setFirstname(firstName.getValue());

            person.getStatus().setLocked(false);
            person.getStatus().setEnabled(true);
            for (String b : status.getValue()) {
                if (getTranslation("person.status-locked.caption").equals(b)) {
                    person.getStatus().setLocked(true);
                } else if (getTranslation("person.status-enabled.caption").equals(b)) {
                    person.getStatus().setEnabled(true);
                }
            }
            person.getStatus().setLastLogin(lastLogin.getValue().atOffset(UTC));
            person.getStatus().setCredentialsChange(lastPasswordChange.getValue().atOffset(UTC));
            person.getStatus().setExpiry(expiryDate.getValue().atOffset(UTC));

            person.getRoles().clear();
            for (String b : roles.getValue()) {
                person.getRoles().add(new Role(RoleName.valueOf(b)));
            }

            if (deleted.getValue() != null) {
                person.getStatus().setDeleted(deleted.getValue().atOffset(UTC));
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

    public void setPerson(@NotNull Person person) {
        if (this.person != null && this.person.equals(person)) {
            LOG.info("Person didn't change. Ignoring event. form={}, id={}, name={}",
                    this, this.person.getId(), this.person.getName());

            return;
        }

        this.person = person;

        LOG.debug("Set person. form={}, id={}, name={}",
                this, this.person.getId(), this.person.getName());


        init();
        populate();
        translate();
    }

    private void populate() {
        if (person == null) {
            LOG.warn("Tried to polulate form data without a person defined. form={}", this);
            return;
        }

        if (person.getId() != null) {
            id.setValue(person.getId().toString());
        }
        username.setValue(person.getUsername());
        email.setValue(person.getEmail());

        name.setValue(person.getName());
        lastName.setValue(person.getLastname());
        firstName.setValue(person.getFirstname());

        lastLogin.setValue(person.getStatus().getLastLogin().toLocalDateTime());
        expiryDate.setValue(person.getStatus().getExpiry().toLocalDateTime());
        lastPasswordChange.setValue(person.getStatus().getCredentialsChange().toLocalDateTime());

        roles.setValue(RoleName.PERSON.getActiveRoleNames(person));

        calculateReadOnly();
    }

    public void initializeReport() {
        LOG.debug("Creating a new person.");

        person = new Person();
        person.setCreated();
        person.setModified();

        AccountSecurityStatus status = new AccountSecurityStatus();
        status.setEnabled(true);
        status.setLocked(false);
        status.setExpiry(OffsetDateTime.now(UTC).plusYears(5));
        person.setStatus(status);

        init();
        populate();
        translate();
    }


    public Optional<Person> getPerson() {
        return Optional.ofNullable(person);
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
                && !(person != null && userDetails.getName().equals(person.getUsername()));

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

        LOG.debug("Building person edit form. form={}, report={}, locale={}", this, person, locale);

        LOG.trace("Remove and add all form elements. form={}", this);
        form.removeAll();

        // Form fields
        id.setTitle(getTranslation("input.id.caption"));
        id.setHelperText(getTranslation("input.id.help"));


        LOG.trace("Adding all form elements. form={}", this);
        id.setTitle(getTranslation("input.id.caption"));
        id.setHelperText(getTranslation("input.id.help"));
        form.addFormItem(id, id.getTitle());

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
        if (person.isEnabled()) {
            statusOptionsSelected.add(getTranslation("person.status-enabled.caption"));
        }
        if (!person.isAccountNonLocked()) {
            statusOptionsSelected.add(getTranslation("person.status-locked.caption"));
        }
        status.removeAll();
        status.setItems(Arrays.asList(statusEnabled, statusLocked));
        status.setValue(statusOptionsSelected);
        form.addFormItem(status, getTranslation("person.status.caption"));

        lastName.setTitle(getTranslation("person.last-name.caption"));
        lastName.setHelperText(getTranslation("person.last-name.help"));
        form.addFormItem(lastName, lastName.getTitle());
        firstName.setTitle(getTranslation("person.first-name.caption"));
        firstName.setHelperText(getTranslation("person.first-name.help"));
        form.addFormItem(firstName, firstName.getTitle());
        deleted.setLabel(getTranslation("person.deleted.caption"));
        deleted.setHelperText(getTranslation("person.deleted.help"));
        form.addFormItem(deleted, deleted.getLabel());
        expiryDate.setLabel(getTranslation("person.expiry-date.caption"));
        expiryDate.setHelperText(getTranslation("person.expiry-date.help"));
        form.addFormItem(expiryDate, expiryDate.getLabel());
        lastPasswordChange.setLabel(getTranslation("person.last-password-change.caption"));
        lastPasswordChange.setHelperText(getTranslation("person.last-password-change.help"));
        form.addFormItem(lastPasswordChange, lastPasswordChange.getLabel());
        lastLogin.setLabel(getTranslation("person.last-login.caption"));
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
