/*
 * Copyright (c) 2022. Roland T. Lichti
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.paladinsinn.torganized.core.stormknight;

import de.kaiserpfalzedv.rpg.torg.model.actors.Clearance;
import de.kaiserpfalzedv.rpg.torg.model.core.Cosm;
import de.paladinsinn.torganized.core.common.CommonData;
import de.paladinsinn.torganized.core.common.Picture;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.*;


@Entity
@Table(name = "STORM_KNIGHTS")
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class StormKnight extends CommonData {
    @Column(name = "NAME_", length = 100, nullable = false, unique = true)
    @NotBlank(message = "You have to give a name")
    @Size(min = 2, max=100, message = "A name has to be at least 2 characters and at most 100 characters long.")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "COSM_", length = 100, nullable = false)
    @NotNull(message = "You have to define the cosm of the storm knight")
    private Cosm cosm;

    @Enumerated(EnumType.STRING)
    @Column(name = "CLEARANCE_", length = 50, nullable = false)
    @NotNull(message = "You have to give a clearance")
    private Clearance clearance = Clearance.ALPHA;

    @Column(name = "XP_", nullable = false)
    @Min(value = 0, message = "XP has to be 0 or more.")
    private int xp = 0;

    @Column(name = "MONEY_", nullable = false)
    @Min(value = 0, message = "Money has to be 0 or more.")
    public int money = 0;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @Column(name = "AVATAR_")
    private Picture avatar;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @Column(name = "TOKEN_")
    private Picture token;
}
