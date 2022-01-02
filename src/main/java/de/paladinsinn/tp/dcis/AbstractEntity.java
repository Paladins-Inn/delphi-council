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

package de.paladinsinn.tp.dcis;

import de.paladinsinn.tp.dcis.model.HasId;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.UUID;

@SuperBuilder(toBuilder = true, setterPrefix = "with")
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@MappedSuperclass
public abstract class AbstractEntity implements Serializable, Cloneable, HasId {
    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(generator = "uuid2")
    @org.hibernate.annotations.Type(type = "org.hibernate.type.UUIDCharType")
    @Column(name = "ID", length = 36, nullable = false, updatable = false, unique = true)
    protected UUID id;

    @Version
    @Column(name = "VERSION", nullable = false)
    @Builder.Default
    protected Integer version = 0;

    @CreationTimestamp
    @Column(name = "CREATED", nullable = false, updatable = false)
    protected OffsetDateTime created;
    @UpdateTimestamp
    @Column(name = "MODIFIED")
    protected OffsetDateTime modified;

    public int getVersion() {
        return version;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractEntity)) return false;
        AbstractEntity entity = (AbstractEntity) o;
        return id.equals(entity.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        AbstractEntity result = (AbstractEntity) super.clone();

        result.id = id;
        result.version = version;

        if (created != null) {
            result.created = created;
        }

        if (modified != null) {
            result.modified = modified;
        }

        return result;
    }
}