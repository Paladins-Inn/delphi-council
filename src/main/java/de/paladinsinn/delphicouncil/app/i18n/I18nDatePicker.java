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
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.time.temporal.WeekFields;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * I18nDatePicker -- An auto localized DatePicker for Vaadin.
 *
 * @author klenkes74 {literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-04-04
 */
@Service
@Scope("prototype")
public class I18nDatePicker extends DatePicker.DatePickerI18n implements LocaleChangeObserver {
    private static final Logger LOG = LoggerFactory.getLogger(I18nDatePicker.class);

    private final Translator translator;
    private Locale locale;

    public I18nDatePicker(@NotNull final Locale locale, Translator translator) {
        this.translator = translator;
        this.locale = locale;

        translate();
    }

    private void translate() {
        LOG.debug("Translating. component={}, locale={}", this, locale);
        setFirstDayOfWeek(WeekFields.of(locale).getFirstDayOfWeek().getValue());

        setCancel(getTranslation("buttons.cancel.caption"));
        setClear(getTranslation("buttons.reset.caption"));

        setCalendar(getTranslation("input.datepicker.calendar"));
        setWeek(getTranslation("input.datepicker.week"));
        setToday(getTranslation("input.datepicker.today"));

        List<String> months = Arrays.asList(getTranslation("input.datepicker.months.january"),
                getTranslation("input.datepicker.months.february"),
                getTranslation("input.datepicker.months.march"),
                getTranslation("input.datepicker.months.april"),
                getTranslation("input.datepicker.months.may"),
                getTranslation("input.datepicker.months.june"),
                getTranslation("input.datepicker.months.july"),
                getTranslation("input.datepicker.months.august"),
                getTranslation("input.datepicker.months.septembre"),
                getTranslation("input.datepicker.months.octobre"),
                getTranslation("input.datepicker.months.novembre"),
                getTranslation("input.datepicker.months.decembre"));
        setMonthNames(months);

        List<String> days = Arrays.asList(getTranslation("input.datepicker.days.long.sunday"),
                getTranslation("input.datepicker.days.long.monday"),
                getTranslation("input.datepicker.days.long.tuesday"),
                getTranslation("input.datepicker.days.long.wednesday"),
                getTranslation("input.datepicker.days.long.thursday"),
                getTranslation("input.datepicker.days.long.friday"),
                getTranslation("input.datepicker.days.long.saturday"));
        setWeekdays(days);

        List<String> daysShort = Arrays.asList(getTranslation("input.datepicker.days.short.sunday"),
                getTranslation("input.datepicker.days.short.monday"),
                getTranslation("input.datepicker.days.short.tuesday"),
                getTranslation("input.datepicker.days.short.wednesday"),
                getTranslation("input.datepicker.days.short.thursday"),
                getTranslation("input.datepicker.days.short.friday"),
                getTranslation("input.datepicker.days.short.saturday"));
        setWeekdaysShort(days);
    }

    private String getTranslation(@NotNull final String key) {
        return translator.getTranslation(key, locale);
    }

    @Override
    public void localeChange(@NotNull final LocaleChangeEvent event) {
        locale = event.getLocale();

        translate();
    }
}
