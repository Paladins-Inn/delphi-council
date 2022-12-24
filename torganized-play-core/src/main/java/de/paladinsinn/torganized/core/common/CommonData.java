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

import de.paladinsinn.torganized.core.person.Person;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import lombok.*;
import lombok.extern.jackson.Jacksonized;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@MappedSuperclass
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CommonData extends PanacheEntity implements Serializable {
    @CreationTimestamp
    @Column(name = "CREATED_", nullable = false, updatable = false)
    private LocalDateTime created = LocalDateTime.now(ZoneOffset.UTC);

    @UpdateTimestamp
    @Column(name = "MODIFIED_", nullable = false)
    private LocalDateTime modified = LocalDateTime.now(ZoneOffset.UTC);

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Column(name = "OWNER_", nullable = false)
    private Person owner;
}
