/*
 * Copyright (c) 2022. Roland T. Lichti
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.paladinsinn.torganized.core.person;

import de.paladinsinn.torganized.core.common.CommonData;
import de.paladinsinn.torganized.core.common.Picture;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "PERSONS")
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Person extends CommonData {
    @Column(name = "NAME", length = 100, nullable = false, unique = true, insertable = true, updatable = true)
    private String name;

    @ElementCollection(targetClass = PersonType.class)
    @JoinTable(name = "PERSON_PERSON_TYPES", joinColumns = @JoinColumn(name = "PERSON_ID"))
    @Column(name = "TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<PersonType> types;

    @OneToOne
    private Picture avatar;
}
