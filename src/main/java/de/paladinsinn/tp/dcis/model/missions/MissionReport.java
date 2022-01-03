/*
 * Copyright (c) &today.year Kaiserpfalz EDV-Service, Roland T. Lichti
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

package de.paladinsinn.tp.dcis.model.missions;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.kaiserpfalzedv.rpg.torg.model.core.SuccessState;
import de.paladinsinn.tp.dcis.AbstractRevisionedEntity;
import de.paladinsinn.tp.dcis.model.operative.OperativeReport;
import io.quarkus.runtime.annotations.RegisterForReflection;
import liquibase.repackaged.org.apache.commons.lang3.builder.CompareToBuilder;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * MissionReport -- A report of the gaming results.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-03-26
 */
@RegisterForReflection
@Entity
@Audited
@Table(
        name = "MISSIONREPORTS",
        uniqueConstraints = {
                @UniqueConstraint(name = "MISSIONREPORTS_ID_UK", columnNames = "ID"),
                @UniqueConstraint(name = "MISSIONREPORTS_TITLE_UK", columnNames = "TITLE")
        }
)
@SuperBuilder(toBuilder = true, setterPrefix = "with")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@JsonDeserialize(builder = MissionReport.MissionReportBuilder.class)
public class MissionReport extends AbstractRevisionedEntity implements Comparable<MissionReport>, Cloneable {
    @ManyToOne(
            cascade = {CascadeType.REFRESH},
            fetch = FetchType.EAGER,
            optional = false,
            targetEntity = Mission.class
    )
    @JoinColumn(
            name = "MISSION_ID",
            referencedColumnName = "ID",
            nullable = false,
            foreignKey = @ForeignKey(name = "REPORTS_MISSIONS_FK")
    )
    @Setter
    private Mission mission;

    @Audited
    @Setter
    @Column(name = "GAME_MASTER", length = 100, nullable = false)
    private String gameMaster;

    @OneToMany(
            targetEntity = OperativeReport.class,
            mappedBy = "report",
            fetch = FetchType.EAGER,
            cascade = {CascadeType.REFRESH},
            orphanRemoval = true
    )
    @Builder.Default
    private Set<OperativeReport> operatives = new HashSet<>();

    @Column(name = "MISSION_DATE", nullable = false)
    @Audited
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Audited
    @Column(name = "OBJECTIVES_MET", length=20, nullable = false)
    private SuccessState objectivesMet;

    @Column(name = "ACHIEVEMENTS", length = 4000)
    @Audited
    private String achievements;

    @Column(name = "NOTES", length = 4000)
    @Audited
    private String notes;

    public void addOperativeReport(OperativeReport operative) {
        if (operative != null && !operatives.contains(operative)) {
            operatives.add(operative);

            operative.setMissionReport(this);
        }
    }

    public void removeOperativeReport(OperativeReport operative) {
        if (operative != null && operatives.contains(operative)) {
            operatives.remove(operative);

            operative.setMissionReport(null);
        }
    }

    @Override
    public int compareTo(MissionReport o) {
        return new CompareToBuilder()
                .append(0, mission.compareTo(o.getMission()))
                .append(getDate(), o.getDate())
                .append(getGameMaster(), o.getGameMaster())
                .toComparison();
    }


    @Override
    public MissionReport clone() throws CloneNotSupportedException {
        MissionReport result = (MissionReport) super.clone();

        result.achievements = achievements;
        result.notes = notes;

        result.gameMaster = gameMaster;
        result.mission = mission.clone();
        result.objectivesMet = objectivesMet;

        if (date != null) {
            result.date = LocalDate.from(date);
        }

        return result;
    }
}
