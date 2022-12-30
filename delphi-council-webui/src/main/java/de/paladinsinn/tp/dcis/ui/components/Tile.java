/*
 * Copyright (c) 2022. Roland T. Lichti
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.paladinsinn.tp.dcis.ui.components;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import de.kaiserpfalzedv.commons.core.text.MarkdownConverter;
import de.paladinsinn.tp.dcis.ui.components.notifications.ErrorNotification;
import lombok.*;
import lombok.extern.slf4j.Slf4j;


/**
 * Tile -- a small tile with a picture that links to another view.
 *
 * @param <T> The type of the view it is pointing to.
 */
@Builder(toBuilder = true)
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Slf4j
public class Tile<T> extends VerticalLayout {
    private final String DEFAULT_WIDTH = "100px";
    private final String DEFAULT_HEIGHT = "180px";

    @EqualsAndHashCode.Include
    private String title;
    @EqualsAndHashCode.Include
    private final String uri;

    @ToString.Exclude
    private H1 header;
    @ToString.Exclude
    private Image picture;
    @ToString.Exclude
    private MarkDownDiv description;

    public Tile(
            final String title,
            final String description,
            final String pictureUri,
            final String uri,
            final String width,
            final String height
    ) {
        this.title = title;
        this.uri = uri;

        header = new H1(this.title);
        picture = new Image();
        picture.setSrc(pictureUri);
        picture.setAlt(this.title);
        this.description = new MarkDownDiv(new MarkdownConverter().convert(description));

        setWidth(width != null ? width : DEFAULT_WIDTH);
        setHeight(height != null ? height : DEFAULT_HEIGHT);
        add(header, picture, this.description);

        addClickListener(e -> {
            getUI().ifPresentOrElse(
                    ui -> {
                        log.debug("Tile selected. tile={}, uri='{}'", this, uri);
                        ui.getPage().setLocation(uri);
                    },
                    () -> {
                        log.warn("No UI for tile found. tile={}", this);
                        ErrorNotification.showMarkdown("**Can not navigate to '" + uri + "'**");
                    }
            );
        });
    }
}
