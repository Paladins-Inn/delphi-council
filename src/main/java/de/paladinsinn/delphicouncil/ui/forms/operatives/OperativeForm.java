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

package de.paladinsinn.delphicouncil.ui.forms.operatives;

import com.sun.istack.NotNull;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.server.VaadinSession;
import de.paladinsinn.delphicouncil.app.events.OperativeSaveEvent;
import de.paladinsinn.delphicouncil.app.i18n.TranslatableComponent;
import de.paladinsinn.delphicouncil.data.Clearance;
import de.paladinsinn.delphicouncil.data.Cosm;
import de.paladinsinn.delphicouncil.data.operative.Operative;
import de.paladinsinn.delphicouncil.data.operative.OperativeService;
import de.paladinsinn.delphicouncil.data.person.Person;
import de.paladinsinn.delphicouncil.data.person.PersonService;
import de.paladinsinn.delphicouncil.data.person.RoleName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.Serializable;
import java.util.Locale;
import java.util.Optional;

import static com.vaadin.flow.component.Unit.PIXELS;
import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

/**
 * PersonForm -- Edits/displays the data for a person.
 *
 * @author klenkes74 {literal <rlichti@kaiserpfalz-edv.de>}
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
    private Operative data;

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

    private final TextField name = new TextField();
    private final TextField lastName = new TextField();
    private final TextField firstName = new TextField();

    private final Image avatar = new Image();
    private final MemoryBuffer avatarBuffer = new MemoryBuffer();
    private final Upload avatarUpload = new Upload(avatarBuffer);

    private final Image token = new Image();
    private final MemoryBuffer tokenBuffer = new MemoryBuffer();
    private final Upload tokenUpload = new Upload(tokenBuffer);

    private final ComboBox<Person> player = new ComboBox<>();
    private final ComboBox<Clearance> clearance = new ComboBox<>();
    private final ComboBox<Cosm> cosm = new ComboBox<>();

    private final HorizontalLayout actions = new HorizontalLayout();
    private final Button save = new Button();
    private final Button reset = new Button();


    @Autowired
    public OperativeForm(
            @NotNull final OperativeService operativeService,
            @NotNull final PersonService personService
    ) {
        this.operativeService = operativeService;
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

        addListener(OperativeSaveEvent.class, operativeService);

        readUserDetails();

        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("1px", 1),
                new FormLayout.ResponsiveStep("400px", 2),
                new FormLayout.ResponsiveStep("800px", 3)
        );

        id.setReadOnly(true);

        name.setReadOnly(readonly);
        name.setRequired(true);
        lastName.setReadOnly(readonly);
        lastName.setRequired(true);
        firstName.setReadOnly(readonly);
        firstName.setRequired(true);

        cosm.setReadOnly(readonly);
        cosm.setDataProvider(Cosm.CORE_EARTH.dataProvider());
        cosm.setItemLabelGenerator((ItemLabelGenerator<Cosm>) item -> getTranslation("torg.cosm." + item.name()));
        cosm.setAllowCustomValue(false);
        cosm.setRequired(true);

        clearance.setReadOnly(!isAdmin && !isOrga && !isJudge);
        clearance.setDataProvider(Clearance.ANY.dataProvider());
        clearance.setItemLabelGenerator((ItemLabelGenerator<Clearance>) item -> getTranslation("torg.clearance." + item.name()));
        clearance.setAllowCustomValue(false);
        clearance.setRequired(true);

        player.setReadOnly(!isAdmin && !isOrga && !isJudge);
        player.setDataProvider(DataProvider.fromFilteringCallbacks(
                query -> {
                    String filter = query.getFilter().orElse(null);
                    LOG.trace("player query. form={}, filter='{}'", this, filter);

                    int offset = query.getOffset();
                    int limit = query.getLimit();

                    Pageable page = PageRequest.of(offset / limit, limit);
                    Page<Person> players = personService.findAll(page);

                    return players.stream();
                },
                query -> {
                    String filter = query.getFilter().orElse(null);
                    LOG.trace("player count query. form={}, filter='{}'", this, filter);

                    return personService.count();
                }
        ));
        player.setItemLabelGenerator((ItemLabelGenerator<Person>) Person::getName);

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

        token.setMaxWidth(70, PIXELS);
        token.setMaxHeight(70, PIXELS);
        tokenUpload.setMaxFiles(1);
        tokenUpload.setMaxFileSize(16777215);
        tokenUpload.setDropAllowed(true);
        tokenUpload.setAcceptedFileTypes("image/png", "image/jpg", "image/jpeg", "image/gif");
        tokenUpload.addFinishedListener(e -> {
            try {
                data.setToken(tokenBuffer.getInputStream());

                Notification.show(
                        getTranslation("input.upload.success", tokenBuffer.getFileName()),
                        1000,
                        Notification.Position.BOTTOM_STRETCH
                );
            } catch (IOException ioException) {
                LOG.error(
                        "Upload of the avatar failed. form={}, file='{}', type='{}'",
                        this, tokenBuffer.getFileData(), tokenBuffer.getFileData().getMimeType()
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
            if (data == null) data = new Operative();

            data.setPlayer(player.getValue());
            data.setClearance(clearance.getValue());
            data.setCosm(cosm.getValue());
            data.setName(name.getValue());
            data.setFirstName(firstName.getValue());
            data.setLastName(lastName.getValue());

            getEventBus().fireEvent(new OperativeSaveEvent(this, false));
        });

        reset.addClickListener(event -> {
            // FIXME 2021-04-04 rlichti implement resetting the data.
        });

        getContent().add(form);

        // mark as initialized.
        initialized = true;
    }

    public void setData(@NotNull Operative data) {
        if (this.data != null && this.data.equals(data)) {
            LOG.info("Data didn't change. Ignoring event. form={}, id={}, name={}",
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

        player.setValue(data.getPlayer());
        clearance.setValue(data.getClearance());
        cosm.setValue(data.getCosm());

        name.setValue(data.getName());
        lastName.setValue(data.getLastName());
        firstName.setValue(data.getFirstName());

        if (data.getAvatar() != null)
            avatar.setSrc(data.getAvatar());

        if (data.getToken() != null)
            token.setSrc(data.getToken());

        calculateReadOnly();
    }

    public void initializeReport(@NotNull final Person player) {
        LOG.debug("Creating a new person.");

        data = new Operative();
        data.setCreated();
        data.setModified();

        data.setPlayer(player);

        init();
        populate();
        translate();
    }


    public Optional<Operative> getData() {
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
                && !(data != null && userDetails.getName().equals(data.getPlayer().getUsername()));

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

        name.setTitle(getTranslation("operative.name.caption"));
        name.setHelperText(getTranslation("operative.name.help"));
        form.addFormItem(name, name.getTitle());


        clearance.setHelperText(getTranslation("torg.clearance.help"));
        form.addFormItem(clearance, getTranslation("torg.clearance.caption"));

        cosm.setHelperText(getTranslation("torg.cosm.help"));
        form.addFormItem(cosm, getTranslation("torg.cosm.caption"));

        lastName.setTitle(getTranslation("operative.last-name.caption"));
        lastName.setHelperText(getTranslation("operative.last-name.help"));
        firstName.setTitle(getTranslation("operative.first-name.caption"));
        firstName.setHelperText(getTranslation("operative.first-name.help"));
        form.addFormItem(lastName, lastName.getTitle());
        form.addFormItem(firstName, firstName.getTitle());

        player.setHelperText(getTranslation("operative.player.help"));
        form.addFormItem(player, getTranslation("operative.player.caption"));

        avatarUpload.setDropLabelIcon(avatar);
        form.addFormItem(avatarUpload, getTranslation("operative.avatar.caption"));

        tokenUpload.setDropLabelIcon(token);
        form.addFormItem(tokenUpload, getTranslation("operative.token.caption"));

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

    public void displayNote(@NotNull final String i18nkey, @NotNull final String type, String... parameters) {
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
        form.removeAll();
    }
}
