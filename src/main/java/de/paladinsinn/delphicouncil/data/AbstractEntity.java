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

package de.paladinsinn.delphicouncil.data;

import de.paladinsinn.delphicouncil.data.person.Person;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
public abstract class AbstractEntity {
    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(generator = "uuid2")
    @org.hibernate.annotations.Type(type = "org.hibernate.type.UUIDCharType")
    @Column(name = "ID", length = 36, nullable = false, updatable = false, unique = true)
    UUID id;

    @Version
    int version;

    @GeneratedValue
    int revId;
    @Column(name = "CREATED", nullable = false, updatable = false)
    public OffsetDateTime created;
    @Column(name = "MODIFIED")
    public OffsetDateTime modified;

    public int getVersion() {
        return version;
    }

    @PrePersist
    public void setCreated() {
        created = getNowUTC();
    }

    @PreUpdate
    public void setModified() {
        modified = getNowUTC();
    }


    protected OffsetDateTime getNowUTC() {
        return OffsetDateTime.now(ZoneOffset.UTC);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;
        Person person = (Person) o;
        return id.equals(person.getId());
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
                .add("id=" + id);

        result.add("created=" + created);

        if (modified != null) {
            result.add("modified=" + modified);
        }
        return result;
    }
}