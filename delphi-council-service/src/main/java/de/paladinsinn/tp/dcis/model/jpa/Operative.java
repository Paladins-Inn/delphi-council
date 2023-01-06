/*
 * Copyright (c) 2022-2023. Roland T. Lichti
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.paladinsinn.tp.dcis.model.jpa;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.kaiserpfalzedv.commons.core.files.FileData;
import de.kaiserpfalzedv.commons.core.files.FileDescription;
import de.kaiserpfalzedv.commons.core.jpa.AbstractRevisionedJPAEntity;
import de.kaiserpfalzedv.rpg.torg.model.actors.Clearance;
import de.kaiserpfalzedv.rpg.torg.model.core.Cosm;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.ws.rs.core.MediaType;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;

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
public class Operative extends AbstractRevisionedJPAEntity implements de.paladinsinn.tp.dcis.model.Operative {
    @Column(name = "CODE", length = 100, nullable = false)
    @Audited
    private String code;

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

    @Column(name = "MONEY", nullable = false)
    @Audited
    private int money;

    @Audited
    @Setter
    @Column(name = "PLAYER", length = 100, nullable = false)
    private String player;


    @Column(name = "DELETED")
    @Audited
    private OffsetDateTime deleted;


    @PreUpdate
    public void preUpdate() {
        clearance = Clearance.valueOf(xp);
    }

    // TODO 2023-01-06 klenkes Implement the Avatar handling.
    @Override
    public FileData getAvatar() {
        return FileData.builder()
                .file(FileDescription.builder()
                        .name(name + "-avatar")
                        .data("".getBytes(StandardCharsets.UTF_8))
                        .mediaType(MediaType.TEXT_PLAIN)
                        .build())
                .preview(FileDescription.builder()
                        .name(name + "-avatar")
                        .data("".getBytes(StandardCharsets.UTF_8))
                        .mediaType(MediaType.TEXT_PLAIN)
                        .build())
                .build();
    }

    // TODO 2023-01-06 klenkes Implement the Token handling.
    @Override
    public FileData getToken() {
        return FileData.builder()
                .file(FileDescription.builder()
                        .name(name + "-token")
                        .data("".getBytes(StandardCharsets.UTF_8))
                        .mediaType(MediaType.TEXT_PLAIN)
                        .build())
                .preview(FileDescription.builder()
                        .name(name + "-token")
                        .data("".getBytes(StandardCharsets.UTF_8))
                        .mediaType(MediaType.TEXT_PLAIN)
                        .build())
                .build();
    }


    public static Operative copyData(final de.paladinsinn.tp.dcis.model.Operative orig) {
        if (Operative.class.isAssignableFrom(orig.getClass())) {
            return (Operative) orig;
        }

        return Operative.builder()
                .id(orig.getId())
                .version(orig.getVersion())
                .created(orig.getCreated())
                .modified(orig.getModified())
                .revisioned(orig.getRevisioned())

                .code(orig.getCode())
                .name(orig.getName())

                .lastName(orig.getLastName())
                .firstName(orig.getFirstName())
                .cosm(orig.getCosm())
                .clearance(orig.getClearance())

                .xp(orig.getXp())
                .money(orig.getMoney())

                .player(orig.getPlayer())
                .deleted(orig.getDeleted())

                // TODO 2023-01-06 klenkes Needs to copy the avatar and token, too (as soon as they are implemented).
                .build();
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public Operative clone() {
        return toBuilder().build();
    }
}
