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

package de.paladinsinn.tp.dcis.ui.components;

import com.sun.istack.NotNull;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.upload.*;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.shared.Registration;
import de.paladinsinn.tp.dcis.data.HasAvatar;
import de.paladinsinn.tp.dcis.data.HasToken;
import de.paladinsinn.tp.dcis.ui.i18n.TranslatableComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.vaadin.flow.component.Unit.PIXELS;

/**
 * Avatar -- A component for editing a token.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-04-07
 */
@SuppressWarnings("unused")
public class Token extends Span implements HasSize, LocaleChangeObserver, TranslatableComponent {
    private static final Logger LOG = LoggerFactory.getLogger(Token.class);

    private Locale locale;
    private final String i18nPrefix;

    private final Image token = new Image();
    private final MemoryBuffer tokenBuffer = new MemoryBuffer();
    private final Upload tokenUpload = new Upload(tokenBuffer);


    public Token(
            @NotNull final String i18nPrefix,

            @NotNull final HasToken data,
            @NotNull final int minWidth,
            @NotNull final int maxWidth,

            @NotNull final int minHeight,
            @NotNull final int maxHeight,

            @NotNull final int maxFileSize
    ) {
        this.i18nPrefix = i18nPrefix;

        token.setMinWidth(minWidth, PIXELS);
        token.setMaxWidth(maxWidth, PIXELS);

        token.setMinHeight(minHeight, PIXELS);
        token.setMaxHeight(maxHeight, PIXELS);

        tokenUpload.setMaxFiles(1);
        tokenUpload.setMaxFileSize(maxFileSize);
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

        locale = VaadinSession.getCurrent().getLocale();
    }


    public void setValue(@NotNull final HasAvatar data) {
        if (data.getAvatar() != null)
            token.setSrc(data.getAvatar());
    }

    @Override
    public void translate() {
        removeAll();

        token.setTitle(getTranslation(String.format("%s.%s", i18nPrefix, "caption")));
        tokenUpload.setDropLabelIcon(token);

        add(tokenUpload);
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        setLocale(event.getLocale());
    }

    @Override
    public void setLocale(Locale locale) {
        if (this.locale != null && this.locale.equals(locale)) {
            LOG.debug("locale did not change - ignoring event. old={}, new={}", this.locale, locale);
            return;
        }

        this.locale = locale;

        translate();
    }

    public Registration addAllFinishedListener(ComponentEventListener<AllFinishedEvent> listener) {
        return tokenUpload.addAllFinishedListener(listener);
    }

    public void setMaxFiles(int maxFiles) {
        tokenUpload.setMaxFiles(maxFiles);
    }

    public int getMaxFiles() {
        return tokenUpload.getMaxFiles();
    }

    public void setMaxFileSize(int maxFileSize) {
        tokenUpload.setMaxFileSize(maxFileSize);
    }

    public int getMaxFileSize() {
        return tokenUpload.getMaxFileSize();
    }

    public void setAutoUpload(boolean autoUpload) {
        tokenUpload.setAutoUpload(autoUpload);
    }

    public boolean isAutoUpload() {
        return tokenUpload.isAutoUpload();
    }

    public void setDropAllowed(boolean dropAllowed) {
        tokenUpload.setDropAllowed(dropAllowed);
    }

    public boolean isDropAllowed() {
        return tokenUpload.isDropAllowed();
    }

    public void setAcceptedFileTypes(String... acceptedFileTypes) {
        tokenUpload.setAcceptedFileTypes(acceptedFileTypes);
    }

    public List<String> getAcceptedFileTypes() {
        return tokenUpload.getAcceptedFileTypes();
    }

    public void setUploadButton(Component uploadButton) {
        tokenUpload.setUploadButton(uploadButton);
    }

    public Component getUploadButton() {
        return tokenUpload.getUploadButton();
    }

    public void setDropLabel(Component dropLabel) {
        tokenUpload.setDropLabel(dropLabel);
    }

    public Component getDropLabel() {
        return tokenUpload.getDropLabel();
    }

    public void setDropLabelIcon(Component dropLabelIcon) {
        tokenUpload.setDropLabelIcon(dropLabelIcon);
    }

    public Component getDropLabelIcon() {
        return tokenUpload.getDropLabelIcon();
    }

    public void interruptUpload() {
        tokenUpload.interruptUpload();
    }

    public boolean isUploading() {
        return tokenUpload.isUploading();
    }

    public Registration addProgressListener(ComponentEventListener<ProgressUpdateEvent> listener) {
        return tokenUpload.addProgressListener(listener);
    }

    public Registration addFailedListener(ComponentEventListener<FailedEvent> listener) {
        return tokenUpload.addFailedListener(listener);
    }

    public Registration addFinishedListener(ComponentEventListener<FinishedEvent> listener) {
        return tokenUpload.addFinishedListener(listener);
    }

    public Registration addStartedListener(ComponentEventListener<StartedEvent> listener) {
        return tokenUpload.addStartedListener(listener);
    }

    public Registration addSucceededListener(ComponentEventListener<SucceededEvent> listener) {
        return tokenUpload.addSucceededListener(listener);
    }

    public Registration addFileRejectedListener(ComponentEventListener<FileRejectedEvent> listener) {
        return tokenUpload.addFileRejectedListener(listener);
    }

    public Receiver getReceiver() {
        return tokenUpload.getReceiver();
    }

    public void setReceiver(Receiver receiver) {
        tokenUpload.setReceiver(receiver);
    }

    public void setI18n(UploadI18N i18n) {
        tokenUpload.setI18n(i18n);
    }

    public UploadI18N getI18n() {
        return tokenUpload.getI18n();
    }
}
