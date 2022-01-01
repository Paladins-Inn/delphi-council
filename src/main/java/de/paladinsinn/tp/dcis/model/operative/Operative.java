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

package de.paladinsinn.tp.dcis.model.operative;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.kaiserpfalzedv.rpg.torg.model.actors.Clearance;
import de.kaiserpfalzedv.rpg.torg.model.core.Cosm;
import de.paladinsinn.tp.dcis.AbstractRevisionedEntity;
import de.paladinsinn.tp.dcis.model.*;
import de.paladinsinn.tp.dcis.model.operativereports.OperativeReport;
import de.paladinsinn.tp.dcis.model.operativespecialreports.OperativeSpecialReport;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.OutputStream;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

@RegisterForReflection
@Entity
@Table(
        name = "OPERATIVES",
        uniqueConstraints = {
                @UniqueConstraint(name = "OPERATIVES_ID_UK", columnNames = "ID"),
                @UniqueConstraint(name = "OPERATIVES_NAME_UK", columnNames = {"LAST_NAME", "FIRST_NAME"})
        }
)
@SuperBuilder(toBuilder = true, setterPrefix = "with")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@JsonDeserialize(builder = Operative.OperativeBuilder.class)
public class Operative extends AbstractRevisionedEntity implements HasImage, HasToken, HasName, HasCosm, HasClearance {
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

    @Audited
    @Setter
    @Column(name = "PLAYER", nullable = false)
    private String player;

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
    public String getData() {
        return Base64.getEncoder().encodeToString(avatar);
    }

    @Override
    public OutputStream getDataStream() {
        return getOutputStream(avatar);
    }


    @Override
    public String getTokenImage() {
        return Base64.getMimeEncoder().encodeToString(token);
    }

    @Override
    public OutputStream getToken() {
        return getOutputStream(token);
    }


    @Override
    public void preUpdate() {
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

        result.player = player;

        result.cosm = cosm;
        result.clearance = clearance;
        result.xp = xp;

        result.reports = Set.copyOf(reports);

        if (deleted != null) {
            result.deleted = OffsetDateTime.from(deleted);
        }

        return result;
    }
}
