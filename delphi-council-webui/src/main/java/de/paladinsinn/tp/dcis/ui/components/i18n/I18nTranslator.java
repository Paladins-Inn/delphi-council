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
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.util.ResourceBundle;


@Unremovable
@VaadinServiceEnabled
@VaadinServiceScoped
@Slf4j
public class I18nTranslator extends ResourceBundleTranslator implements I18NProvider {

    public I18nTranslator() {
        super("/messages/msg");
    }

    @PostConstruct
    public void init() {
        log.debug("Created I18N provider for Vaadin. i18n={}", this);

        ResourceBundle test = ResourceBundle.getBundle("/META-INF/messages/msg");

        log.trace("test,  bundle: bundle={}, application.title='{}'", test, test.getString("application.title"));
    }
}
