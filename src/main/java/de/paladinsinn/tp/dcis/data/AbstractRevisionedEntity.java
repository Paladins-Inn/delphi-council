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

import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.StringJoiner;

@RevisionEntity
@MappedSuperclass
@Getter
@Setter
public class AbstractRevisionedEntity extends AbstractEntity {
    @RevisionNumber
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DbSequence")
    @SequenceGenerator(name = "DbSequence", sequenceName = "hibernate_sequence")
    @Column(name = "REVID", nullable = false, updatable = false)
    protected int revId;

    @RevisionTimestamp
    @Column(name = "REVISIONED")
    protected OffsetDateTime revisioned;

    @Override
    public String toString() {
        return getToStringJoiner().toString();
    }

    protected StringJoiner getToStringJoiner() {
        return new StringJoiner(", ", getClass().getSimpleName() + "[", "]")
                .merge(super.getToStringJoiner())
                .add("revId=" + revId)
                .add("revisioned=" + revisioned);
    }


    @Override
    public AbstractRevisionedEntity clone() throws CloneNotSupportedException {
        AbstractRevisionedEntity result = (AbstractRevisionedEntity) super.clone();

        result.revId = revId;

        if (revisioned != null) {
            result.revisioned = OffsetDateTime.from(revisioned);
        }

        return result;
    }
}
