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

package de.paladinsinn.delphicouncil.data.operative;

import com.sun.istack.NotNull;
import de.paladinsinn.delphicouncil.data.AbstractEntity;
import de.paladinsinn.delphicouncil.data.Clearance;
import de.paladinsinn.delphicouncil.data.Cosm;
import de.paladinsinn.delphicouncil.data.missions.MissionReport;
import de.paladinsinn.delphicouncil.data.person.Person;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "OPERATIVES",
        uniqueConstraints = {
                @UniqueConstraint(name = "OPERATIVES_ID_UK", columnNames = "ID"),
                @UniqueConstraint(name = "OPERATIVES_REVID_UK", columnNames = "REVID"),
                @UniqueConstraint(name = "OPERATIVES_NAME_UK", columnNames = {"LAST_NAME", "FIRST_NAME"})
        }
)
@Getter
@Setter
public class Operative extends AbstractEntity {
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

    @ManyToOne(
            targetEntity = Person.class,
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL,
            optional = false
    )
    @JoinColumn(
            name = "PERSON_ID",
            nullable = false,
            foreignKey = @ForeignKey(name = "OPERATIVES_PERSON_FK")
    )
    @Audited
    private Person player;

    @ManyToMany(
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL,
            targetEntity = MissionReport.class
    )
    @JoinTable(
            name = "MISSIONREPORTS_OPERATIVES",
            inverseJoinColumns = @JoinColumn(name = "MISSIONREPORT_ID"),
            joinColumns = @JoinColumn(name = "OPERATIVE_ID")
    )
    private Set<MissionReport> reports = new HashSet<>();

    @Column(name = "DELETED")
    @Audited
    private OffsetDateTime deleted;


    public void addMissionReport(@NotNull MissionReport report) {
        if (report != null & !reports.contains(report)) {
            report.addOperative(this);
            reports.add(report);
        }
    }

    public void removeMissionReport(@NotNull MissionReport report) {
        if (report != null && reports.contains(report)) {
            report.removeOperative(this);
            reports.remove(report);
        }
    }
}
