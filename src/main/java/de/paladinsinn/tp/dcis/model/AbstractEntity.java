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

package de.paladinsinn.tp.dcis.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.UUID;

@MappedSuperclass
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true, setterPrefix = "with")
@Getter
@Setter
public abstract class AbstractEntity extends PanacheEntityBase implements Cloneable, HasId {
    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(generator = "uuid2")
    @org.hibernate.annotations.Type(type = "org.hibernate.type.UUIDCharType")
    @Column(name = "ID", length = 36, nullable = false, updatable = false, unique = true)
    protected UUID id;

    @Version
    @Column(name = "VERSION", nullable = false)
    @Builder.Default
    protected int version = 0;

    @Column(name = "CREATED", nullable = false, updatable = false)
    protected OffsetDateTime created;
    @Column(name = "MODIFIED")
    protected OffsetDateTime modified;

    public int getVersion() {
        return version;
    }

    protected OffsetDateTime getNowUTC() {
        return OffsetDateTime.now(ZoneOffset.UTC);
    }

    @PrePersist
    public void doPrePersist() {
        created = getNowUTC();
        modified = created;

        prePersist();
    }

    public void prePersist() {
    }

    @PreUpdate
    public void doPreUpdate() {
        modified = getNowUTC();

        preUpdate();
    }

    public void preUpdate() {
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
    public String toString() {
        StringJoiner result = getToStringJoiner();

        return result.toString();
    }

    protected StringJoiner getToStringJoiner() {
        StringJoiner result = new StringJoiner(", ", getClass().getSimpleName() + "[", "]")
                .add("hash=" + System.identityHashCode(this))
                .add("id=" + id)
                .add("created=" + created);

        if (modified != null) {
            result.add("modified=" + modified);
        }

        return result;
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