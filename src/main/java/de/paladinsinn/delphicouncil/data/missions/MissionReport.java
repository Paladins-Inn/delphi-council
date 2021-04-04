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

package de.paladinsinn.delphicouncil.data.missions;

import com.sun.istack.NotNull;
import de.paladinsinn.delphicouncil.data.AbstractRevisionedEntity;
import de.paladinsinn.delphicouncil.data.operative.Operative;
import de.paladinsinn.delphicouncil.data.person.Person;
import lombok.Getter;
import lombok.Setter;
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
@Entity
@Audited
@Table(
        name = "MISSIONREPORTS",
        uniqueConstraints = {
                @UniqueConstraint(name = "MISSIONREPORTS_ID_UK", columnNames = "ID")
        }
)
@Getter
@Setter
public class MissionReport extends AbstractRevisionedEntity implements Comparable<MissionReport> {
    @ManyToOne(
            cascade = {CascadeType.PERSIST, CascadeType.REFRESH},
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
    private Mission mission;

    @ManyToOne(
            cascade = {CascadeType.PERSIST, CascadeType.REFRESH},
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
    private Person gameMaster;

    @ManyToMany(
            cascade = {CascadeType.PERSIST, CascadeType.REFRESH},
            fetch = FetchType.EAGER,
            targetEntity = Operative.class
    )
    @JoinTable(
            name = "MISSIONREPORTS_OPERATIVES",
            joinColumns = @JoinColumn(name = "MISSIONREPORT_ID"),
            inverseJoinColumns = @JoinColumn(name = "OPERATIVE_ID")
    )
    private Set<Operative> operatives = new HashSet<>();

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

    public void addOperative(Operative operative) {
        if (operative != null && !operatives.contains(operative)) {
            operative.addMissionReport(this);
            operatives.add(operative);
        }
    }

    public void removeOperative(Operative operative) {
        if (operative != null && operatives.contains(operative)) {
            operative.removeMissionReport(this);
            operatives.remove(operative);
        }
    }

    public void clearOperatives() {
        operatives.clear();
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
                .add("mission=" + mission.getCode())
                .add("gameMaster=" + gameMaster.getName())
                .add("date=" + date)
                .toString();
    }
}
