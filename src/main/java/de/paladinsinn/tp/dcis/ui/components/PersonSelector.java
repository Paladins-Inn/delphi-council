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
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.server.VaadinSession;
import de.paladinsinn.tp.dcis.data.person.Person;
import de.paladinsinn.tp.dcis.data.person.PersonRepository;
import de.paladinsinn.tp.dcis.security.LoggedInUser;
import de.paladinsinn.tp.dcis.ui.i18n.TranslatableComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Locale;

/**
 * PersonSelector -- A ComboBox for Person with database query for data.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-04-07
 */
public class PersonSelector extends ComboBox<Person> implements LocaleChangeObserver, TranslatableComponent {
    private static final Logger LOG = LoggerFactory.getLogger(PersonSelector.class);

    private Locale locale;
    private final String i18nPrefix;

    public PersonSelector(
            @NotNull final String i18nPrefix,
            @NotNull final PersonRepository personService,
            @NotNull final LoggedInUser user
    ) {
        this.i18nPrefix = i18nPrefix;

        if (locale == null) {
            locale = VaadinSession.getCurrent().getLocale();
        }

        setReadOnly(!user.isAdmin() && !user.isOrga() && !user.isJudge());
        setDataProvider(DataProvider.fromFilteringCallbacks(
                query -> {
                    String filter = query.getFilter().orElse(null);
                    LOG.trace("person query. filter='{}'", filter);

                    int offset = query.getOffset();
                    int limit = query.getLimit();

                    Pageable page = PageRequest.of(offset / limit, limit);
                    Page<Person> players = personService.findAll(page);

                    return players.stream();
                },
                query -> {
                    String filter = query.getFilter().orElse(null);
                    LOG.trace("person count query. filter='{}'", filter);

                    return (int) personService.count();
                }
        ));
        setItemLabelGenerator((ItemLabelGenerator<Person>) Person::getName);

        translate();
    }

    @Override
    public void setReadonly(boolean readOnly) {
        super.setReadonly(readOnly);
        super.setRequired(readOnly);
        super.setRequiredIndicatorVisible(readOnly);
    }

    @Override
    public void translate() {
        setLabel(getTranslation(String.format("%s.%s", i18nPrefix, "caption")));
        setHelperText(getTranslation(String.format("%s.%s", i18nPrefix, "help")));
    }


    @Override
    public void localeChange(LocaleChangeEvent event) {
        setLocale(event.getLocale());
    }


    @Override
    public void setLocale(Locale locale) {
        if (this.locale != null && this.locale.equals(locale)) {
            LOG.debug("Locale already set - ignoring event. old={}, new={}", this.locale, locale);
            return;
        }

        translate();
    }
}
