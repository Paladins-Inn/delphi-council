/*
 * Copyright (c) 2022. Roland T. Lichti
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.paladinsinn.torganized.core.common;

import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.persistence.*;

@Entity
@Table(name = "DESCRIPTIONS")
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Description extends CommonData {
    @Enumerated(EnumType.STRING)
    private Language language;

    @Column(name = "TITLE_", length = 100, nullable = false)
    private String title = "";

    @Column(name = "SHORT_", length = 1000, nullable = false)
    private String shortDescription = "";

    @Column(name = "LONG_", length = 1000, nullable = false)
    private String longDescription = "";
}
