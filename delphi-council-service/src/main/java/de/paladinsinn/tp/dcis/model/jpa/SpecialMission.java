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
import de.kaiserpfalzedv.rpg.torg.model.core.SuccessState;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

/**
 * SpecialMission -- A private table mission.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-04-18
 */
@RegisterForReflection
@Entity
@Audited
@Table(
        name = "SPECIALMISSIONS",
        uniqueConstraints = {
                @UniqueConstraint(name = "SPECIALMISSIONS_CODE_UK", columnNames = "CODE"),
                @UniqueConstraint(name = "SPECIALMISSIONS_TITLE_UK", columnNames = "TITLE")
        }
)
@Jacksonized
@SuperBuilder(toBuilder = true)
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Slf4j
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class SpecialMission extends AbstractRevisionedJPAEntity implements de.paladinsinn.tp.dcis.model.SpecialMission {
    @Column(name = "CODE", length = 40, nullable = false, updatable = false, unique = true)
    @Builder.Default
    private String code = UUID.randomUUID().toString();

    @Column(name = "TITLE", length = 100, nullable = false)
    private String name;

    @Column(name = "IMAGE", length = 100)
    @Audited
    @Schema(description = "Image URL for this mission.", maxLength = 100, nullable = true)
    private String image;

    @Column(name = "CLEARANCE", nullable = false)
    @Enumerated(EnumType.STRING)
    private Clearance clearance;

    @Column(name = "DESCRIPTION", length = 4000)
    private String description;

    @Column(name = "PAYMENT", nullable = false)
    private int payment;

    @Column(name = "XP", nullable = false)
    private int xp;

    @Column(name = "PUBLICATION")
    private String publication;


    @Column(name = "GAME_MASTER", length = 100)
    @Setter
    private String gameMaster;

    @Column(name = "MISSION_DATE", nullable = false)
    @Audited
    private LocalDate date;


    @Enumerated(EnumType.STRING)
    @Audited
    @Column(name = "OBJECTIVES_MET", length=20, nullable = false)
    private SuccessState objectivesMet;

    @Column(name = "ACHIEVEMENTS", length = 4000)
    private String achievements;

    @Column(name = "NOTES", length = 4000)
    private String notes;




    @PrePersist
    public void prePersist() {
        if (code == null) {
            code = UUID.randomUUID().toString();
        }
    }


    public static SpecialMission copyData(de.paladinsinn.tp.dcis.model.SpecialMission orig) {
        if (SpecialMission.class.isAssignableFrom(orig.getClass())) {
            return (SpecialMission) orig;
        }

        return SpecialMission.builder()
                .id(orig.getId())
                .version(orig.getVersion())
                .created(orig.getCreated())
                .modified(orig.getModified())
                .revisioned(orig.getRevisioned())

                .code(orig.getCode())
                .name(orig.getName())

                .description(orig.getDescription())
                .clearance(orig.getClearance())
                .payment(orig.getPayment())
                .xp(orig.getXp())

                .publication(orig.getPublication())
                .image(orig.getImage())

                .objectivesMet(orig.getObjectivesMet())
                .achievements(orig.getAchievements())
                .notes(orig.getNotes())

                .build();
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public SpecialMission clone() {
        return toBuilder().build();
    }
}
