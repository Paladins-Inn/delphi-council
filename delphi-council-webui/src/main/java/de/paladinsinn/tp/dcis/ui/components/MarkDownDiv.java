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

import com.vaadin.flow.component.html.Div;
import de.paladinsinn.tp.dcis.ui.Application;

public class MarkDownDiv extends Div {
    private final Application.MarkdownConverter converter = new Application.MarkdownConverter();

    public MarkDownDiv(final String text) {
        setText(text);
    }

    public void setText(final String text) {
        getElement().setProperty("innerHTML", converter.convert(text));
    }
}
