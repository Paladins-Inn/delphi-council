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
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@RegisterForReflection
@Entity
@Audited
@Table(
        name = "MISSIONS",
        uniqueConstraints = {
                @UniqueConstraint(name = "MISSIONS_ID_UK", columnNames = "ID"),
                @UniqueConstraint(name = "MISSIONS_CODE_UK", columnNames = "CODE"),
                @UniqueConstraint(name = "MISSIONS_TITLE_UK", columnNames = "TITLE")
        }
)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@Schema(description = "Delphi Council Mission")
public class Mission extends AbstractRevisionedJPAEntity implements de.paladinsinn.tp.dcis.model.Mission {
    @Column(name = "CODE", length = 20, nullable = false)
    @Audited
    @Schema(description = "Code of this mission.", maxLength = 20)
    private String code;

    @Column(name = "IMAGE", length = 100)
    @Audited
    @Schema(description = "Image URL for this mission.", maxLength = 100, nullable = true)
    private String image;

    @Column(name = "TITLE", length = 100, nullable = false)
    @Audited
    @Schema(description = "Name of this mission.")
    private String name;

    @Column(name = "DESCRIPTION", length = 4000, nullable = false)
    @Audited
    @Schema(description = "Short description of this mission.", maxLength = 4000)
    private String description;

    @Column(name = "PAYMENT", nullable = false)
    @Audited
    @Schema(description = "Payment for every storm knight taking this mission.")
    private int payment;

    @Column(name = "XP", nullable = false)
    @Audited
    @Schema(description = "XP for every storm knight taking this mission.")
    private int xp;

    @Column(name = "OBJECTIVES_SUCCESS", length = 4000)
    @Audited
    @Schema(description = "Objectives defined for a successful mission result.", maxLength = 4000, nullable = true)
    private String objectivesSuccess;

    @Column(name = "OBJECTIVES_GOOD", length = 4000)
    @Audited
    @Schema(description = "Objectives defined for a good mission result.", maxLength = 40000, nullable = true)
    private String objectivesGood;

    @Column(name = "OBJECTIVES_OUTSTANDING", length = 4000)
    @Audited
    @Schema(description = "Objectives defined for an outstanding mission result.", maxLength = 4000, nullable = true)
    private String objectivesOutstanding;

    @Column(name = "CLEARANCE", length=20, nullable = false)
    @Audited
    @Schema(description = "Minimum clearance for taking part in this mission.", maxLength = 20)
    @Enumerated(EnumType.STRING)
    private Clearance clearance;

    @Column(name = "PUBLICATION", length = 100)
    @Audited
    @Schema(description = "The publication this mission is defined in.", maxLength = 100, nullable = true)
    private String publication;


    public static Mission copyData(de.paladinsinn.tp.dcis.model.Mission orig) {
        if (Mission.class.isAssignableFrom(orig.getClass())) {
            return (Mission) orig;
        }

        return Mission.builder()
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
                .objectivesSuccess(orig.getObjectivesSuccess())
                .objectivesGood(orig.getObjectivesGood())
                .objectivesOutstanding(orig.getObjectivesOutstanding())

                .build();
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @SneakyThrows
    @Override
    public Mission clone() {
        return toBuilder().build();
    }
}
