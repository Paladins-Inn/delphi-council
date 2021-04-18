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

package de.paladinsinn.tp.dcis.data.missions;

import com.sun.istack.NotNull;
import de.paladinsinn.tp.dcis.data.*;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

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
@Getter
@Setter
public class Mission extends AbstractRevisionedEntity implements Comparable<Mission>, HasId, HasName, HasClearance, Cloneable {
    @Column(name = "CODE", length = 20, nullable = false)
    @Audited
    private String code;

    @Column(name = "IMAGE", length = 100)
    @Audited
    private String image;

    @Column(name = "TITLE", length = 100, nullable = false)
    @Audited
    private String name;

    @Column(name = "DESCRIPTION", length = 4000, nullable = false)
    @Audited
    private String description;

    @Column(name = "PAYMENT", nullable = false)
    @Audited
    private int payment;

    @Column(name = "XP", nullable = false)
    @Audited
    private int xp;

    @Column(name = "OBJECTIVES_SUCCESS", length = 4000)
    @Audited
    private String objectivesSuccess;

    @Column(name = "OBJECTIVES_GOOD", length = 4000)
    @Audited
    private String objectivesGood;

    @Column(name = "OBJECTIVES_OUTSTANDING", length = 4000)
    @Audited
    private String objectivesOutstanding;

    @Column(name = "CLEARANCE", length=20, nullable = false)
    @Audited
    @Enumerated(EnumType.STRING)
    private Clearance clearance;

    @Column(name = "PUBLICATION", length = 100)
    @Audited
    private String publication;

    @OneToMany(
            targetEntity = MissionReport.class,
            fetch = FetchType.EAGER,
            cascade = {CascadeType.REFRESH},
            mappedBy = "mission",
            orphanRemoval = true
    )
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

    @Override
    public String toString() {
        return new StringJoiner(", ", Mission.class.getSimpleName() + "[", "]")
                .add("code='" + code + "'")
                .add("title='" + name + "'")
                .add("payment=" + payment)
                .add("xp=" + xp)
                .toString();
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
