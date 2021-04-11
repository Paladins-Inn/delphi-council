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
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.selection.MultiSelectionListener;
import com.vaadin.flow.function.SerializablePredicate;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.shared.Registration;
import de.paladinsinn.tp.dcis.data.operative.Operative;
import de.paladinsinn.tp.dcis.data.operative.OperativeRepository;
import de.paladinsinn.tp.dcis.ui.i18n.TranslatableComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * PersonSelector -- A ComboBox for Person with database query for data.
 *
 * @author klenkes74 {literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-04-07
 */
public class OperativeSelector extends VerticalLayout implements LocaleChangeObserver, TranslatableComponent, AutoCloseable, Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(OperativeSelector.class);

    private final OperativeRepository dataService;
    private final String i18nPrefix;
    private Locale locale;

    private final Label label;
    private final MultiSelectListBox<Operative> selector;
    private final Span help;

    public OperativeSelector(
            @NotNull final String i18nPrefix,
            @NotNull final OperativeRepository dataService
    ) {
        this.i18nPrefix = i18nPrefix;
        this.dataService = dataService;

        label = new Label(getTranslation(String.format("%s.%s", i18nPrefix, "caption")));
        help = new Span(getTranslation(String.format("%s.%s", i18nPrefix, "help")));

        selector = new MultiSelectListBox<>();
        selector.setDataProvider(DataProvider.fromFilteringCallbacks(
                query -> {
                    Object filter = query.getFilter().orElse(null);
                    LOG.trace("item query. filter='{}'", filter);

                    int offset = query.getOffset();
                    int limit = query.getLimit();

                    Pageable page = PageRequest.of(offset / limit, limit);
                    Page<Operative> operatives = dataService.findAll(page);

                    return operatives.stream();
                },
                query -> {
                    Object filter = query.getFilter().orElse(null);
                    LOG.trace("count query. filter='{}'", filter);

                    return (int) dataService.count();
                }
        ));
        selector.setRenderer(new ComponentRenderer<>(item -> {
            Div result = new Div();
            result.setText(item.getName());
            return result;
        }));

        add(label, selector, help);
    }

    public void setReadonly(boolean readOnly) {
        selector.setReadOnly(readOnly);
    }

    public void translate() {
        label.setText(getTranslation(String.format("%s.%s", i18nPrefix, "caption")));
        help.setText(getTranslation(String.format("%s.%s", i18nPrefix, "help")));
    }

    @Override
    public void setLocale(Locale locale) {

    }

    public void setValue(Set<Operative> value) {
        selector.setValue(value);
    }

    public void updateSelection(Set<Operative> addedItems, Set<Operative> removedItems) {
        selector.updateSelection(addedItems, removedItems);
    }

    public Set<Operative> getSelectedItems() {
        return selector.getSelectedItems();
    }

    public Registration addSelectionListener(MultiSelectionListener<MultiSelectListBox<Operative>, Operative> listener) {
        return selector.addSelectionListener(listener);
    }

    public void setDataProvider(DataProvider<Operative, ?> dataProvider) {
        selector.setDataProvider(dataProvider);
    }

    public DataProvider<Operative, ?> getDataProvider() {
        return selector.getDataProvider();
    }

    public ComponentRenderer<? extends Component, Operative> getItemRenderer() {
        return selector.getItemRenderer();
    }

    public void setRenderer(ComponentRenderer<? extends Component, Operative> itemRenderer) {
        selector.setRenderer(itemRenderer);
    }

    public void setItemEnabledProvider(SerializablePredicate<Operative> itemEnabledProvider) {
        selector.setItemEnabledProvider(itemEnabledProvider);
    }

    public SerializablePredicate<Operative> getItemEnabledProvider() {
        return selector.getItemEnabledProvider();
    }

    public void setRequiredIndicatorVisible(boolean requiredIndicatorVisible) {
        selector.setRequiredIndicatorVisible(requiredIndicatorVisible);
    }

    public Registration addValueChangeListener(HasValue.ValueChangeListener<? super AbstractField.ComponentValueChangeEvent<MultiSelectListBox<Operative>, Set<Operative>>> listener) {
        return selector.addValueChangeListener(listener);
    }

    public boolean isEmpty() {
        return selector.isEmpty();
    }

    public Set<Operative> getValue() {
        return selector.getValue();
    }

    public Set<Operative> getEmptyValue() {
        return selector.getEmptyValue();
    }

    public boolean isRequiredIndicatorVisible() {
        return selector.isRequiredIndicatorVisible();
    }

    public void setReadOnly(boolean readOnly) {
        setReadonly(readOnly);
    }

    public boolean isReadOnly() {
        return selector.isReadOnly();
    }

    public Optional<Set<Operative>> getOptionalValue() {
        return selector.getOptionalValue();
    }

    public void clear() {
        selector.clear();
    }

    public void addComponents(Operative afterItem, Component... components) {
        selector.addComponents(afterItem, components);
    }

    public void prependComponents(Operative beforeItem, Component... components) {
        selector.prependComponents(beforeItem, components);
    }

    public int getItemPosition(Operative item) {
        return selector.getItemPosition(item);
    }

    public void setItems(Collection<Operative> items) {
        selector.setItems(items);
    }

    public void setItems(Operative... items) {
        selector.setItems(items);
    }

    public void setItems(Stream<Operative> streamOfItems) {
        selector.setItems(streamOfItems);
    }

    public void select(Operative... items) {
        selector.select(items);
    }

    public void deselect(Operative... items) {
        selector.deselect(items);
    }

    public void select(Iterable<Operative> items) {
        selector.select(items);
    }

    public void deselect(Iterable<Operative> items) {
        selector.deselect(items);
    }

    public void deselectAll() {
        selector.deselectAll();
    }

    public boolean isSelected(Operative item) {
        return selector.isSelected(item);
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        setLocale(event.getLocale());
    }

    @Override
    public void close() throws Exception {
    }
}
