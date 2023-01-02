/*
 * Copyright (c) 2022. Roland T. Lichti
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.paladinsinn.torganized.core.threadcards;

import lombok.Getter;

/**
 * The vulnerability modifier for a threat.
 *
 * @author rlichti
 * @version 1.0.0 2021-12-25
 * @since 1.0.0 2021-12-25
 */
@Getter
public enum SizeModifier {
    Normal(0),
    VerySmall(-4),
    Small(-2),
    Large(+2),
    VeryLarge(+4);

    int modifier;

    SizeModifier(int modifier) {
        this.modifier = modifier;
    }
}
