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

package de.paladinsinn.tp.dcis.data;

import com.sun.istack.NotNull;
import com.vaadin.flow.data.provider.ListDataProvider;

import java.util.Arrays;

/**
 * Clearance --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-03-26
 */
public enum Clearance {
    ANY(-1),
    ALPHA(0),
    BETA(50),
    GAMMA(200),
    DELTA(500),
    OMEGA(1000);

    private final int xp;

    Clearance(@NotNull final int xp) {
        this.xp = xp;
    }

    public int getMinXP() {
        return xp;
    }

    /**
     * Calculates the clearance for the given amount of XP.
     *
     * @param xp The xp to match the security clearance level.
     * @return The Delphi Council Security Clearance level.
     */
    public static Clearance valueOf(int xp) {
        if (xp >= Clearance.OMEGA.getMinXP()) {
            return Clearance.OMEGA;
        } else if (xp >= Clearance.DELTA.getMinXP()) {
            return Clearance.DELTA;
        } else if (xp >= Clearance.GAMMA.getMinXP()) {
            return Clearance.GAMMA;
        } else if (xp >= Clearance.BETA.getMinXP()) {
            return Clearance.BETA;
        } else if (xp >= Clearance.ALPHA.getMinXP()) {
            return Clearance.ALPHA;
        } else {
            return Clearance.ANY;
        }
    }

    public ListDataProvider<Clearance> dataProvider() {
        return new ListDataProvider<>(Arrays.asList(ANY, ALPHA, BETA, GAMMA, DELTA, OMEGA));
    }
}
