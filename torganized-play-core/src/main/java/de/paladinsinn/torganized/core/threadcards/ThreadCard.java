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

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.*;

/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 2.0.0 2021-12-25
 * @since 2.0.0 2021-12-25
 */
@RegisterForReflection
@Jacksonized
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@Getter
public class ThreadCard {
    @ToString.Include
    @NotEmpty
    @NotNull
    @NotBlank
    @Size(min = 2, max = 20)
    private String name;

    @Min(0)
    private int intimidation, maneuver, taunt, trick,
                melee, dodge, unarmedCombat,
                toughness, armor;

    @Builder.Default
    @NotNull
    private SizeModifier size = SizeModifier.Normal;

    @Override
    public ThreadCard clone() throws CloneNotSupportedException {
        ThreadCard result = (ThreadCard) super.clone();

        result.name = name;

        result.intimidation = intimidation;
        result.maneuver = maneuver;
        result.taunt = taunt;
        result.trick = trick;

        result.melee = melee;
        result.dodge = dodge;
        result.unarmedCombat = unarmedCombat;

        result.toughness = toughness;
        result.armor = armor;

        return result;
    }
}
