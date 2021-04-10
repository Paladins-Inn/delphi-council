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

package de.paladinsinn.tp.dcis.ui.i18n;

import ch.carnet.kasparscherrer.LanguageSelect;
import com.sun.istack.NotNull;
import com.vaadin.flow.server.VaadinSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Locale;

/**
 * I18nSelector -- A small wrapper around {@link LanguageSelect} for setting the available languages.
 *
 * @author klenkes74 {literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-03-27
 */
public class I18nSelector extends LanguageSelect {
    private static final Logger LOG = LoggerFactory.getLogger(I18nSelector.class);

    private static final Locale[] VALID_LOCALES = new Locale[] {
            new Locale("de_DE"),
            new Locale("en_UK")
    };

    public I18nSelector(@NotNull final String i18nPrefix, @NotNull final Locale selectedLocale) {
        super(
                VALID_LOCALES[0],
                VALID_LOCALES[1]
        );

        setValue(selectedLocale);
        setLabel(getTranslation(i18nPrefix + ".caption", getValue()));
        setHelperText(getTranslation(i18nPrefix + ".help", getValue()));

        if (!Arrays.asList(VALID_LOCALES).contains(VaadinSession.getCurrent().getLocale())) {
            LOG.info("Selected locale is not known. Changing locale to the default locale. locale={}", VALID_LOCALES[0]);

            VaadinSession.getCurrent().setLocale(VALID_LOCALES[0]);
        }

        setRequiredIndicatorVisible(true);
        setEmptySelectionAllowed(false);
        setRequiredIndicatorVisible(true);

        addValueChangeListener(ev -> {
            setLabel(getTranslation(i18nPrefix + ".caption", ev.getValue()));
            setHelperText(getTranslation(i18nPrefix + ".help", ev.getValue()));
        });
    }
}
