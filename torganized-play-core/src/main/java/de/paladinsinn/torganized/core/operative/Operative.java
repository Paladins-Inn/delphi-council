/*
 * Copyright (c) 2022. Roland T. Lichti
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.paladinsinn.torganized.core.operative;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.kaiserpfalzedv.commons.core.files.FileData;
import de.kaiserpfalzedv.commons.core.files.FileDescription;
import de.kaiserpfalzedv.commons.core.jpa.AbstractRevisionedJPAEntity;
import de.kaiserpfalzedv.commons.core.resources.HasId;
import de.kaiserpfalzedv.commons.core.resources.HasName;
import de.kaiserpfalzedv.commons.fileserver.jpa.JPAFileData;
import de.kaiserpfalzedv.rpg.torg.model.actors.Clearance;
import de.kaiserpfalzedv.rpg.torg.model.core.Cosm;
import de.paladinsinn.torganized.core.common.HasAvatar;
import de.paladinsinn.torganized.core.common.HasClearance;
import de.paladinsinn.torganized.core.common.HasCosm;
import de.paladinsinn.torganized.core.common.HasToken;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
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
@Jacksonized
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class Operative extends AbstractRevisionedJPAEntity implements HasName, HasId, HasCosm, HasClearance, HasAvatar, HasToken {
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
    @Column(name = "PLAYER", length = 100, nullable = false)
    private String player;

    @OneToMany(
            targetEntity = OperativeReport.class,
            mappedBy = "operative",
            fetch = FetchType.EAGER,
            cascade = {CascadeType.REFRESH},
            orphanRemoval = true
    )
    @Builder.Default
    private Set<OperativeReport> reports = new HashSet<>();

    @OneToMany(
            targetEntity = OperativeSpecialReport.class,
            mappedBy = "operative",
            fetch = FetchType.EAGER,
            cascade = {CascadeType.REFRESH},
            orphanRemoval = true
    )
    @Builder.Default
    private Set<OperativeSpecialReport> specialReports = new HashSet<>();


    @Column(name = "DELETED")
    @Audited
    private OffsetDateTime deleted;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "name", column = @Column(name = "AVATAR_FILENAME")),
            @AttributeOverride(name = "mediaType", column = @Column(name = "AVATAR_MEDIATYPE")),
            @AttributeOverride(name = "data", column = @Column(name = "AVATAR_DATA"))
    })
    private JPAFileData avatar;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "name", column = @Column(name = "TOKEN_FILENAME")),
            @AttributeOverride(name = "mediaType", column = @Column(name = "TOKEN_MEDIATYPE")),
            @AttributeOverride(name = "data", column = @Column(name = "TOKEN_DATA"))
    })
    private JPAFileData token;


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

    @Transient
    public FileData getAvatar() {
        return createFileData(avatar);
    }

    @Transient
    public FileData getToken() {
        return createFileData(token);
    }
    @Transient
    private FileData createFileData(JPAFileData data) {
        return FileData.builder()
                .file(FileDescription.builder()
                        .name(data.getName())
                        .mediaType(data.getMediaType())
                        .data(data.getData())
                        .build())
                .preview(FileDescription.builder()
                        .name(data.getName())
                        .mediaType(data.getMediaType())
                        .data(data.getData())
                        .build())
                .build();
    }


    @PreUpdate
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
            result.avatar = avatar.clone();
        }

        if (token != null) {
            result.token = token.clone();
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
