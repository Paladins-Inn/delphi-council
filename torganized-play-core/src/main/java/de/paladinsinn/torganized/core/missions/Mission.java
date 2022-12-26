/*
 * Copyright (c) 2022. Roland T. Lichti
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.paladinsinn.torganized.core.missions;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.kaiserpfalzedv.commons.core.jpa.AbstractRevisionedJPAEntity;
import de.kaiserpfalzedv.commons.core.resources.HasId;
import de.kaiserpfalzedv.commons.core.resources.HasName;
import de.kaiserpfalzedv.rpg.torg.model.actors.Clearance;
import de.paladinsinn.torganized.core.common.HasClearance;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

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
@JsonDeserialize(builder = Mission.MissionBuilder.class)
@Schema(description = "Delphi Council Mission")
public class Mission extends AbstractRevisionedJPAEntity implements Comparable<Mission>, HasId, HasName, HasClearance, Cloneable {
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

    @OneToMany(
            targetEntity = MissionReport.class,
            fetch = FetchType.EAGER,
            cascade = {CascadeType.REFRESH},
            mappedBy = "mission",
            orphanRemoval = true
    )
    @Schema(description = "Planned and finished missions.", minItems = 0, maxItems = 214783647)
    @Builder.Default
    private Set<MissionReport> reports = new HashSet<>();

    public void addReport(@NotNull MissionReport report) {
        if (reports.contains(report))
            return;

        reports.add(report);
        report.setMission(this);
    }

    public void removeReport(@NotNull MissionReport report) {
        if (! reports.contains(report))
            return;

        reports.remove(report);
        report.setMission(null);
    }

    @Override
    public int compareTo(final Mission o) {
        return new CompareToBuilder()
                .append(getCode(), o.getCode())
                .toComparison();
    }

    @SneakyThrows
    @Override
    public Mission clone() {
        Mission result = (Mission) super.clone();

        result.code = code;
        result.name = name;

        result.description = description;
        result.clearance = clearance;
        result.payment = payment;
        result.xp = xp;

        result.publication = publication;
        result.image = image;

        result.objectivesSuccess = objectivesSuccess;
        result.objectivesGood = objectivesGood;
        result.objectivesOutstanding = objectivesOutstanding;

        result.reports = Set.copyOf(reports);

        return result;
    }
}
