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

package de.paladinsinn.delphicouncil.app.i18n;

import com.sun.istack.NotNull;
import com.vaadin.flow.i18n.I18NProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.*;

/**
 * Translator -- Provides a nice way to read translations from Resource bundles.
 *
 * @author klenkes74 {literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-03-27
 */
public class Translator implements I18NProvider, Serializable, AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(Translator.class);


    private static final String DEFAULT_BUNDLE = "messages";

    @Value("${spring.web.locale}")
    private String defaultLocale;

    private final HashMap<String, HashMap<Locale, ResourceBundle>> bundles = new HashMap<>();

    /**
     * Reads the translation from the default bundle {@value #DEFAULT_BUNDLE}.
     *
     * @param key       The key of the bundle entry.
     * @param locale    the locale to use.
     * @param arguments Arguments for the translation.
     * @return The translated text or {@literal !<key>}
     */
    @Override
    public String getTranslation(final String key, final Locale locale, Object... arguments) {
        return getMessage(DEFAULT_BUNDLE, key, locale, arguments);
    }


    /**
     * Reads the translation from the bundle with the given name.
     *
     * @param bundleName The name of the bundle. The locale and the postfix {@literal .properties} will be appended.
     * @param key        The key of the bundle entry.
     * @param locale     the locale to use.
     * @param arguments  Arguments for the translation.
     * @return The translated text or {@literal !<key>}
     */
    public String getMessage(final String bundleName, final String key, final Locale locale, Object... arguments) {
        loadBundle(bundleName, locale);

        try {
            final String pattern = bundles.get(bundleName).get(locale).getString(key);
            final MessageFormat format = new MessageFormat(pattern, locale);
            return format.format(arguments);
        } catch (MissingResourceException ex) {
            LOG.warn(
                    "Translation failed. bundle={}, locale={}, key={}",
                    bundleName, locale, key
            );
            return "!" + key;
        }
    }

    /**
     * Returns the translation from a resource accompanying the class given as bundleObject.
     *
     * @param bundleObject The class to be translated.
     * @param key          The key for the translation.
     * @param locale       The locale to use.
     * @param arguments    Arguments to the translation.
     * @return The translated string or {@literal !<key>}.
     */
    public String getMessage(final Object bundleObject, final String key, final Locale locale, final Object... arguments) {
        String bundleName = bundleObject.getClass().getCanonicalName().replace(".", "/");

        return getMessage(bundleName, key, locale, arguments);
    }

    /**
     * Loads the bundle into the cache.
     *
     * @param bundleName The base filename for the translation bundle.
     * @param locale     The locale to load the bundle for.
     */
    private void loadBundle(@NotNull String bundleName, @NotNull Locale locale) {
        if (!bundles.containsKey(bundleName)) {
            LOG.debug("Adding bundle. baseName='{}'", bundleName);

            bundles.put(bundleName, new HashMap<>());
        }

        if (!bundles.get(bundleName).containsKey(locale)) {
            LOG.info("Loading bundle. baseName='{}', locale='{}'", bundleName, locale.getDisplayName());

            ResourceBundle bundle;
            try {
                bundle = ResourceBundle.getBundle(bundleName, locale, new UnicodeResourceBundleControl());
            } catch (MissingResourceException e) {
                LOG.warn("Translator did not find the wanted locale for the bundle. bundle={}, locale={}",
                        bundleName, locale);
                try {
                    bundle = ResourceBundle.getBundle(bundleName, Locale.forLanguageTag(defaultLocale),
                            new UnicodeResourceBundleControl());
                } catch (MissingResourceException e1) {
                    LOG.warn("Translator did not find the wanted bundle. Using default bundle. bundle={}", bundleName);

                    bundle = ResourceBundle.getBundle(DEFAULT_BUNDLE, Locale.forLanguageTag(defaultLocale),
                            new UnicodeResourceBundleControl());
                }
            }
            bundles.get(bundleName).put(locale, bundle);
        }
    }

    @Override
    public void close() throws Exception {
        LOG.info("Closing all bundles.");
        bundles.clear();
    }

    @Override
    public List<Locale> getProvidedLocales() {
        return Arrays.asList(Locale.forLanguageTag("de_DE"), Locale.forLanguageTag("en_UK"));
    }

    /**
     * @author peholmst
     * @since 0.1.0
     */
    private static class UnicodeResourceBundleControl extends ResourceBundle.Control {
        @Override
        public ResourceBundle newBundle(
                final String baseName,
                final Locale locale,
                final String format,
                final ClassLoader loader,
                final boolean reload
        ) throws IllegalAccessException, InstantiationException, IOException {

            String bundleName = toBundleName(baseName, locale);
            String resourceName = toResourceName(bundleName, "properties");
            final URL resourceURL = loader.getResource(resourceName);
            if (resourceURL == null)
                return null;

            try (BufferedReader in = new BufferedReader(new InputStreamReader(resourceURL.openStream(), StandardCharsets.UTF_8))) {
                return new PropertyResourceBundle(in);
            }
        }
    }

}
