/*
 * Copyright (c) 2022. Roland T. Lichti
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.paladinsinn.tp.dcis.ui.components.i18n;

import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.quarkus.annotation.VaadinServiceEnabled;
import com.vaadin.quarkus.annotation.VaadinServiceScoped;
import de.kaiserpfalzedv.commons.core.i18n.ResourceBundleTranslator;
import io.quarkus.arc.Unremovable;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.extern.slf4j.Slf4j;

import java.util.Locale;


@RegisterForReflection
@Unremovable
@VaadinServiceEnabled
@VaadinServiceScoped
@Slf4j
public class I18nTranslator extends ResourceBundleTranslator implements I18NProvider {
    public I18nTranslator() {
        super("/messages/msg");
    }

    /**
     * Returns the translation key. Since the view names are the original view names suffixed with "_Subclass" we replace
     * "_Subclass" with "" to counteract.
     *
     * @param key       The key of the bundle entry.
     * @param locale    the locale to use.
     * @param params Arguments for the translation.
     * @return The translation or "!{key}" if there is no translation.
     */
    @Override
    public String getTranslation(String key, Locale locale, Object... params) {
        return super.getTranslation(key.replace("_Subclass", ""), locale, params);
    }
}
