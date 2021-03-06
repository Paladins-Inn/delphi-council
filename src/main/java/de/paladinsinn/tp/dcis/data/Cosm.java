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
import lombok.Getter;

import java.util.Arrays;

/**
 * Realm -- The realms of the possibility wars with their axioms.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-03-26
 */
@Getter
public enum Cosm {
    AYSLE(24, 16, 18, 14),
    CORE_EARTH(9, 23, 10, 23),
    CYBERPAPACY(14, 18, 16, 26),
    LIVING_LAND(1, 7, 24, 6),
    NILE_EMPIRE(14, 20, 18, 20),
    ORRORSH(16, 18, 16, 18),
    PAN_PACIFICA(4, 24, 8, 24),
    THARKOLD(12, 25, 4, 25),

    AKASHA(1, 26, 1, 28),
    AYSLE_THARKOLD(24, 25, 18, 25),
    ELFAME(24, 11, 18, 14),
    FAIRY_TALE_AYSLE(24, 16, 18, 14),
    Mechopotamia(9, 9, 12, 8),
    TheDeadWorld(3, 7, 4, 8),
    Tombspace(0, 0, 0, 0),
    Ukhaan(8, 27, 5, 27),
    Waldeck(14, 11, 2, 12);

    private final int magic;
    private final int social;
    private final int spirit;
    private final int tech;

    Cosm(
            @NotNull final int magic,
            @NotNull final int social,
            @NotNull final int spirit,
            @NotNull final int tech
    ) {
        this.magic = magic;
        this.social = social;
        this.spirit = spirit;
        this.tech = tech;
    }

    public ListDataProvider<Cosm> dataProvider() {
        return new ListDataProvider<>(Arrays.asList(
                AYSLE, CORE_EARTH, CYBERPAPACY, LIVING_LAND, NILE_EMPIRE, ORRORSH, PAN_PACIFICA, THARKOLD,
                AKASHA, AYSLE_THARKOLD, ELFAME, FAIRY_TALE_AYSLE, Mechopotamia, TheDeadWorld, Tombspace, Ukhaan, Waldeck
        ));
    }


    public int[] axioms() {
        return new int[]{magic, social, spirit, tech};
    }

    public int magic() {
        return magic;
    }

    public int social() {
        return social;
    }

    public int spirit() {
        return spirit;
    }

    public int tech() {
        return tech;
    }
}
