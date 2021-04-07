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

package de.paladinsinn.tp.dcis.data.operative;

import com.sun.istack.NotNull;
import com.vaadin.flow.server.StreamResource;
import de.paladinsinn.tp.dcis.data.AbstractRevisionedEntity;
import de.paladinsinn.tp.dcis.data.Clearance;
import de.paladinsinn.tp.dcis.data.Cosm;
import de.paladinsinn.tp.dcis.data.person.Person;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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
    // FIXME 2021-04-07 rlichti get the correct limits.
    private static final int DELTA_CLEARANCE_XP = 400;
    private static final int GAMMA_CLEARANCE_XP = 200;
    private static final int BETA_CLEARANCE_XP = 50;


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

    @Column(name = "XP", nullable = false)
    @Audited
    private int xp;

    @ManyToOne(
            targetEntity = Person.class,
            fetch = FetchType.EAGER,
            cascade = {CascadeType.MERGE, CascadeType.REFRESH},

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
            cascade = {CascadeType.MERGE, CascadeType.REFRESH},
            orphanRemoval = true
    )
    private Set<OperativeReport> reports = new HashSet<>();

    @Column(name = "DELETED")
    @Audited
    private OffsetDateTime deleted;

    @Lob
    @Column(name = "AVATAR", length = 16777215)
    private byte[] avatar;

    @Lob
    @Column(name = "TOKEN", length = 16777215)
    private byte[] token;


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

    public StreamResource getToken() {
        if (token != null) {
            return new StreamResource(getId().toString() + "png", () -> new ByteArrayInputStream(token));
        } else {
            return null;
        }
    }

    /**
     * @param data data to read.
     * @throws IOException If the data can't be read.
     */
    public void setToken(InputStream data) throws IOException {
        token = data.readAllBytes();
    }

    public void setXp(@NotNull final int xp) {
        this.xp = xp;

        calculateClearance();
    }

    /**
     * Calculates the security clearance according to the XP of the character.
     */
    private void calculateClearance() {
        if (xp >= DELTA_CLEARANCE_XP) {
            clearance = Clearance.DELTA;
        } else if (xp >= GAMMA_CLEARANCE_XP) {
            clearance = Clearance.GAMMA;
        } else if (xp >= BETA_CLEARANCE_XP) {
            clearance = Clearance.BETA;
        } else {
            clearance = Clearance.ALPHA;
        }
    }
}
