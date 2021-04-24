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
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.StreamResource;
import de.paladinsinn.tp.dcis.data.*;
import de.paladinsinn.tp.dcis.data.person.Person;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

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
public class Operative extends AbstractRevisionedEntity implements HasAvatar, HasToken, HasName, HasCosm, HasClearance {
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
            cascade = {CascadeType.REFRESH},

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
            cascade = {CascadeType.REFRESH},
            orphanRemoval = true
    )
    private Set<OperativeReport> reports = new HashSet<>();

    @OneToMany(
            targetEntity = OperativeSpecialReport.class,
            mappedBy = "operative",
            fetch = FetchType.EAGER,
            cascade = {CascadeType.REFRESH},
            orphanRemoval = true
    )
    private Set<OperativeSpecialReport> specialReports = new HashSet<>();


    @Column(name = "DELETED")
    @Audited
    private OffsetDateTime deleted;

    @Lob
    @Column(name = "AVATAR", length = 16777215)
    private byte[] avatar;

    @Lob
    @Column(name = "TOKEN", length = 16777215)
    private byte[] token;


    /**
     * @param report to add to the operatives file.
     */
    public void addOperativeReport(@NotNull OperativeReport report) {
        if (report != null & !reports.contains(report)) {
            reports.add(report);

            report.setOperative(this);
        }
    }

    /**
     * @param report to remove from the operatives file.
     */
    public void removeOperativeReport(@NotNull OperativeReport report) {
        if (report != null && reports.contains(report)) {
            reports.remove(report);

            report.setOperative(null);
        }
    }


    /**
     * @param report to add to the operatives file.
     */
    public void addSpecialReport(@NotNull OperativeSpecialReport report) {
        if (report != null & !specialReports.contains(report)) {
            specialReports.add(report);

            report.setOperative(this);
        }
    }

    /**
     * @param report to remove from the operatives file.
     */
    public void removeSpecialReport(@NotNull OperativeSpecialReport report) {
        if (report != null && specialReports.contains(report)) {
            specialReports.remove(report);

            report.setOperative(null);
        }
    }


    @Override
    public Image getAvatarImage() {
        Image result = new Image();

        StreamResource data = getAvatar();
        if (data != null) {
            result.setSrc(data);
        }

        return result;
    }

    @Override
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
    @Override
    public void setAvatar(InputStream data) throws IOException {
        avatar = data.readAllBytes();
    }


    @Override
    public Image getTokenImage() {
        Image result = new Image();

        StreamResource data = getToken();
        if (data != null) {
            result.setSrc(data);
        }

        return result;
    }

    @Override
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
    @Override
    public void setToken(InputStream data) throws IOException {
        token = data.readAllBytes();
    }

    /**
     * @param xp sets the new XP of this operative.
     */
    public void setXp(@NotNull final int xp) {
        this.xp = xp;
        clearance = Clearance.valueOf(xp);
    }

    @Override
    public Operative clone() throws CloneNotSupportedException {
        Operative result = (Operative) super.clone();

        result.name = name;
        result.firstName = firstName;
        result.lastName = lastName;

        if (avatar != null) {
            result.avatar = Arrays.copyOf(avatar, avatar.length);
        }

        if (token != null) {
            result.token = Arrays.copyOf(token, token.length);
        }

        result.player = player.clone();

        result.cosm = cosm;
        result.clearance = clearance;
        result.xp = xp;

        result.reports = Set.copyOf(reports);

        if (deleted != null) {
            result.deleted = OffsetDateTime.from(deleted);
        }

        return result;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Operative.class.getSimpleName() + "[", "]")
                .merge(super.getToStringJoiner())

                .add("player='" + player.getName() + "'")
                .add("name='" + name + "'")
                .add("cosm=" + cosm)
                .add("clearance=" + clearance)

                .toString();
    }
}
