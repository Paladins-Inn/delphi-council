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
import de.kaiserpfalzedv.commons.core.jpa.AbstractRevisionedJPAEntity;
import de.kaiserpfalzedv.rpg.torg.model.actors.Clearance;
import de.kaiserpfalzedv.rpg.torg.model.core.Cosm;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
    @Column(name = "CODE", length = 50, nullable = false)
    @Audited
    @NotNull
    @Size(min=1, max=50)
    private String code;

    @Column(name = "NAME", length = 100, nullable = false)
    @Audited
    @NotNull
    @Size(min=1, max=100)
    private String name;

    @Column(name = "LAST_NAME", length = 100, nullable = false)
    @Audited
    @NotNull
    @Size(min=1, max=100)
    private String lastName;

    @Column(name = "FIRST_NAME", length = 100, nullable = false)
    @Audited
    @NotNull
    @Size(min=1, max=100)
    private String firstName;

    @Column(name = "COSM", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    @Audited
    @NotNull
    @Size(min=1, max=50)
    private Cosm cosm;

    @Column(name = "CLEARANCE", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    @Audited
    @NotNull
    @Size(min=1, max=50)
    private Clearance clearance;

    @Column(name = "AVATAR")
    @Size(min = 0, max = 255)
    private String avatar;

    @Column(name = "TOKEN")
    @Size(min = 0, max = 255)
    private String token;

    @Column(name = "XP", nullable = false)
    @Audited
    @Min(0)
    private int xp;

    @Column(name = "MONEY", nullable = false)
    @Audited
    private int money;

    @Audited
    @Setter
    @Column(name = "PLAYER", length = 100, nullable = false)
    @Size(min = 1, max = 100)
    private String player;


    @Column(name = "DELETED")
    @Audited
    private OffsetDateTime deleted;


    @PreUpdate
    public void preUpdate() {
        clearance = Clearance.valueOf(xp);
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
                .avatar(orig.getAvatar())
                .token(orig.getToken())

                .lastName(orig.getLastName())
                .firstName(orig.getFirstName())
                .cosm(orig.getCosm())
                .clearance(orig.getClearance())

                .xp(orig.getXp())
                .money(orig.getMoney())

                .player(orig.getPlayer())
                .deleted(orig.getDeleted())

                .build();
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public Operative clone() {
        return toBuilder().build();
    }
}
