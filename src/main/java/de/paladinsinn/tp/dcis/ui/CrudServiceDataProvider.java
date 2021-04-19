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

package de.paladinsinn.tp.dcis.ui;

import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.vaadin.artur.helpers.CrudService;
import org.vaadin.artur.spring.dataprovider.FilterablePageableDataProvider;

import java.util.Arrays;
import java.util.List;

/**
 * CrudServiceDataProvider --
 *
 * @param <T> Data Type.
 * @param <F> Filter.
 * @param <I> ID Type.
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-03-26
 */
public class CrudServiceDataProvider<T, F, I> extends FilterablePageableDataProvider<T, F> {
    private static final Logger LOG = LoggerFactory.getLogger(CrudServiceDataProvider.class);


    private final CrudService<T, I> service;
    private final List<QuerySortOrder> defaultSortOrders;

    public CrudServiceDataProvider(CrudService<T, I> service, QuerySortOrder... defaultSortOrders) {
        this.service = service;
        this.defaultSortOrders = Arrays.asList(defaultSortOrders);
    }

    @Override
    protected Page<T> fetchFromBackEnd(Query<T, F> query, Pageable pageable) {
        return service.list(pageable);
    }

    @Override
    protected List<QuerySortOrder> getDefaultSortOrders() {
        return defaultSortOrders;
    }

    @Override
    protected int sizeInBackEnd(Query<T, F> query) {
        return service.count();
    }
}
