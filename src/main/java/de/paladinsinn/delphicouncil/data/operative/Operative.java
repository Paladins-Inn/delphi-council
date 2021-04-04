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

package de.paladinsinn.delphicouncil.data.operative;

import com.sun.istack.NotNull;
import com.vaadin.flow.server.StreamResource;
import de.paladinsinn.delphicouncil.data.AbstractRevisionedEntity;
import de.paladinsinn.delphicouncil.data.Clearance;
import de.paladinsinn.delphicouncil.data.Cosm;
import de.paladinsinn.delphicouncil.data.missions.MissionReport;
import de.paladinsinn.delphicouncil.data.person.Person;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "OPERATIVES",
        uniqueConstraints = {
                @UniqueConstraint(name = "OPERATIVES_ID_UK", columnNames = "ID"),
                @UniqueConstraint(name = "OPERATIVES_REVID_UK", columnNames = "REVID"),
                @UniqueConstraint(name = "OPERATIVES_NAME_UK", columnNames = {"LAST_NAME", "FIRST_NAME"})
        }
)
@Getter
@Setter
public class Operative extends AbstractRevisionedEntity {
    @Column(name = "NAME", length = 100, nullable = false)
    @Audited
    private String name;

    @Column(name = "LAST_NAME", length = 100, nullable = false)
    @Audited
    private String lastName;

    @Column(name = "FIRST_NAME", length = 100, nullable = false)
    @Audited
    private String firstName;

    @Column(name = "COSM", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    @Audited
    private Cosm cosm;

    @Column(name = "CLEARANCE", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    @Audited
    private Clearance clearance;

    @ManyToOne(
            targetEntity = Person.class,
            fetch = FetchType.EAGER,
            cascade = {CascadeType.PERSIST, CascadeType.REFRESH},

            optional = false
    )
    @JoinColumn(
            name = "PERSON_ID",
            nullable = false,
            foreignKey = @ForeignKey(name = "OPERATIVES_PERSON_FK")
    )
    @Audited
    private Person player;

    @OneToMany(
            targetEntity = OperativeReport.class,
            mappedBy = "operative",
            fetch = FetchType.EAGER,
            cascade = {CascadeType.PERSIST, CascadeType.REFRESH},
            orphanRemoval = true
    )
    private Set<OperativeReport> reports = new HashSet<>();

    @Column(name = "DELETED")
    @Audited
    private OffsetDateTime deleted;

    @Column(name = "AVATAR")
    private byte[] avatar;


    public void addReport(@NotNull OperativeReport report) {
        if (report != null & !reports.contains(report)) {
            reports.add(report);

            report.setOperative(this);
        }
    }

    public void removeReport(@NotNull OperativeReport report) {
        if (report != null && reports.contains(report)) {
            reports.remove(report);

            report.setOperative(null);
        }
    }

    public StreamResource getAvatar() {
        if (avatar != null) {
            return new StreamResource(getId().toString() + "png", () -> new ByteArrayInputStream(avatar));
        } else {
            return null;
        }
    }

    /**
     * @param data data to read.
     * @throws IOException If the data can't be read.
     */
    public void setAvatar(InputStream data) throws IOException {
        avatar = data.readAllBytes();
    }
}
