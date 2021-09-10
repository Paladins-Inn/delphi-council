/*
 * Copyright (c) 2021 Kaiserpfalz EDV-Service, Roland T. Lichti
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package de.paladinsinn.tp.dcis.model.missionreports;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.kaiserpfalzedv.rpg.torg.model.core.SuccessState;
import de.paladinsinn.tp.dcis.model.missions.Mission;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.NotNull;
import de.paladinsinn.tp.dcis.model.AbstractRevisionedEntity;
import de.paladinsinn.tp.dcis.model.operativereports.OperativeReport;
import de.paladinsinn.tp.dcis.model.person.Person;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

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
                @UniqueConstraint(name = "MISSIONREPORTS_ID_UK", columnNames = "ID")
        }
)
@SuperBuilder(toBuilder = true, setterPrefix = "with")
@AllArgsConstructor
@NoArgsConstructor
@Getter
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

    @ManyToOne(
            cascade = {CascadeType.REFRESH},
            fetch = FetchType.EAGER,
            optional = false,
            targetEntity = Person.class
    )
    @JoinColumn(
            name = "GM_ID",
            referencedColumnName = "ID",
            nullable = false,
            foreignKey = @ForeignKey(name = "REPORTS_PERSONS_FK")
    )
    @Audited
    @Setter
    private Person gameMaster;

    @OneToMany(
            targetEntity = OperativeReport.class,
            mappedBy = "report",
            fetch = FetchType.EAGER,
            cascade = {CascadeType.REFRESH},
            orphanRemoval = true
    )
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

    public void setGameMaster(@NotNull final Person gameMaster) {
        if (this.gameMaster != null) {
            if (this.gameMaster.equals(gameMaster))
                return;

            Person oldGameMaster = this.gameMaster;
            this.gameMaster = gameMaster;
            oldGameMaster.removeGameMasterReport(this);
        } else {
            this.gameMaster = gameMaster;
            gameMaster.addGameMasterReport(this);
        }
    }

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
    public String toString() {
        return new StringJoiner(", ", MissionReport.class.getSimpleName() + "[", "]")
                .merge(super.getToStringJoiner())
                .add("mission=" + mission.getCode())
                .add("gameMaster='" + gameMaster.getName() + "'")
                .add("date=" + date)
                .toString();
    }

    @Override
    public MissionReport clone() throws CloneNotSupportedException {
        MissionReport result = (MissionReport) super.clone();

        result.achievements = achievements;
        result.notes = notes;

        result.gameMaster = gameMaster.clone();
        result.mission = mission.clone();
        result.objectivesMet = objectivesMet;

        if (date != null) {
            result.date = LocalDate.from(date);
        }

        return result;
    }
}
