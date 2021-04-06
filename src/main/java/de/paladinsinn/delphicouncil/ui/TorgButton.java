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

package de.paladinsinn.delphicouncil.ui;

import com.sun.istack.NotNull;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.RouteParameters;
import de.paladinsinn.delphicouncil.app.i18n.TranslatableComponent;

import java.util.Locale;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.UUID;

/**
 * TorgButton -- A l10n button with torg background.
 *
 * @author klenkes74 {literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-04-06
 */
@CssImport("./views/torg.css")
public class TorgButton extends NativeButton implements TranslatableComponent, LocaleChangeObserver {
    private final String i18nBase;
    private Object[] i18nParameters;

    public TorgButton(@NotNull final String i18nBase) {
        this.i18nBase = i18nBase.endsWith(".caption") ? i18nBase.substring(0, i18nBase.length() - 8) : i18nBase;
        addClassName("v-nativebutton-torg");

        translate();
    }

    public TorgButton(@NotNull final String i18nBase, @NotNull Class<? extends Component> target) {
        this(i18nBase);

        addClickListener(e -> e.getSource().getUI().ifPresent(ui -> ui.navigate(target)));
    }

    public TorgButton(@NotNull final String i18nBase, @NotNull Class<? extends Component> target, @NotNull final UUID id) {
        this(i18nBase);

        addClickListener(e -> e.getSource().getUI().ifPresent(
                ui -> ui.navigate(target, new RouteParameters("id", id.toString()))
        ));
    }

    public TorgButton(
            @NotNull final String i18nBase,
            @NotNull Class<? extends Component> target,
            @NotNull final UUID id,
            final Object... i18nParameters
    ) {
        this(i18nBase);
        this.i18nParameters = i18nParameters;

        addClickListener(e -> e.getSource().getUI().ifPresent(
                ui -> ui.navigate(target, new RouteParameters("id", id.toString()))
        ));
    }

    @Override
    public void translate() {
        if (i18nParameters != null) {
            setText(getTranslation(i18nBase + ".caption", i18nParameters));
            setTitle(getTranslation(i18nBase + ".help", i18nParameters));
        } else {
            setText(getTranslation(i18nBase + ".caption"));
            setTitle(getTranslation(i18nBase + ".help"));
        }
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        setLocale(event.getLocale());
    }

    @Override
    public void setLocale(Locale locale) {
        if (getLocale() != null && !getLocale().equals(locale)) {
            return;
        }

        translate();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TorgButton)) return false;
        TorgButton that = (TorgButton) o;
        return i18nBase.equals(that.i18nBase);
    }

    @Override
    public int hashCode() {
        return Objects.hash(i18nBase);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", TorgButton.class.getSimpleName() + "[", "]")
                .add("i18n='" + i18nBase + "'")
                .toString();
    }
}
