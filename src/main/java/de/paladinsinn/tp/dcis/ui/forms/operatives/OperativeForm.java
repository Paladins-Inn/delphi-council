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

package de.paladinsinn.tp.dcis.ui.forms.operatives;

import com.sun.istack.NotNull;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.server.VaadinSession;
import de.paladinsinn.tp.dcis.data.Clearance;
import de.paladinsinn.tp.dcis.data.Cosm;
import de.paladinsinn.tp.dcis.data.operative.Operative;
import de.paladinsinn.tp.dcis.data.operative.OperativeService;
import de.paladinsinn.tp.dcis.data.person.Person;
import de.paladinsinn.tp.dcis.data.person.PersonService;
import de.paladinsinn.tp.dcis.events.OperativeSaveEvent;
import de.paladinsinn.tp.dcis.security.LoggedInUser;
import de.paladinsinn.tp.dcis.ui.components.TorgActionBar;
import de.paladinsinn.tp.dcis.ui.i18n.TranslatableComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    private final Image avatar = new Image();
    private final MemoryBuffer avatarBuffer = new MemoryBuffer();
    private final Upload avatarUpload = new Upload(avatarBuffer);

    private final Image token = new Image();
    private final MemoryBuffer tokenBuffer = new MemoryBuffer();
    private final Upload tokenUpload = new Upload(tokenBuffer);

    private final ComboBox<Person> player = new ComboBox<>();
    private final ComboBox<Clearance> clearance = new ComboBox<>();
    private final IntegerField xp = new IntegerField();
    private final ComboBox<Cosm> cosm = new ComboBox<>();

    private TorgActionBar actions;


    @Autowired
    public OperativeForm(
            @NotNull final OperativeService operativeService,
            @NotNull final PersonService personService,
            @NotNull final LoggedInUser user) {
        this.operativeService = operativeService;
        this.personService = personService;
        this.user = user;

        init();
    }


    private boolean init() {
        if (initialized || data == null || user == null) {
            LOG.debug(
                    "Already initialized or data missing. initialized={}, data={}, user={}",
                    initialized, data, user
            );

            return false;
        }

        if (locale == null) {
            setLocale(VaadinSession.getCurrent().getLocale());
        }

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

        player.setReadOnly(!user.isAdmin() && !user.isOrga() && !user.isJudge());
        player.setDataProvider(DataProvider.fromFilteringCallbacks(
                query -> {
                    String filter = query.getFilter().orElse(null);
                    LOG.trace("player query. filter='{}'", filter);

                    int offset = query.getOffset();
                    int limit = query.getLimit();

                    Pageable page = PageRequest.of(offset / limit, limit);
                    Page<Person> players = personService.findAll(page);

                    return players.stream();
                },
                query -> {
                    String filter = query.getFilter().orElse(null);
                    LOG.trace("player count query. filter='{}'", filter);

                    return personService.count();
                }
        ));
        player.setItemLabelGenerator((ItemLabelGenerator<Person>) Person::getName);
        player.setRequiredIndicatorVisible(true);

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
                        "Upload of the avatar failed. file='{}', type='{}'",
                        avatarBuffer.getFileData(), avatarBuffer.getFileData().getMimeType()
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
                        "Upload of the avatar failed. file='{}', type='{}'",
                        tokenBuffer.getFileData(), tokenBuffer.getFileData().getMimeType()
                );
                Notification.show(
                        getTranslation("input.upload.failed", ioException.getLocalizedMessage()),
                        2000,
                        Notification.Position.BOTTOM_STRETCH
                );
            }
        });


        actions = new TorgActionBar(
                "buttons",
                event -> {
                    scrape();
                    getEventBus().fireEvent(new OperativeSaveEvent(this, false));
                },
                null,
                null,
                null
        );

        getContent().add(form);

        // mark as initialized.
        initialized = true;
        return initialized;
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

        if (data.getAvatar() != null)
            avatar.setSrc(data.getAvatar());

        if (data.getToken() != null)
            token.setSrc(data.getToken());
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

        avatar.setTitle(getTranslation("operative.avatar.caption"));
        avatarUpload.setDropLabelIcon(avatar);

        token.setTitle(getTranslation("operative.token.caption"));
        tokenUpload.setDropLabelIcon(token);

        actions.setReadOnly(user.isReadonly() && !user.getPerson().equals(data.getPlayer()));
        actions.translate();


        form.add(name, cosm);
        form.setColspan(name, 2);

        form.add(xp, clearance);
        form.addFormItem(avatarUpload, getTranslation("operative.avatar.caption"));

        form.add(lastName, firstName);
        form.addFormItem(tokenUpload, getTranslation("operative.token.caption"));

        form.add(actions);
        form.setColspan(actions, 3);

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
