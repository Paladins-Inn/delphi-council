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

package de.paladinsinn.tp.dcis.data.operative;

import de.paladinsinn.tp.dcis.data.AbstractRevisionedEntity;
import de.paladinsinn.tp.dcis.data.missions.MissionReport;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.StringJoiner;

/**
 * OperativeReport --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-04-04
 */
@Entity
@Audited
@Table(
        name = "MISSIONREPORTS_OPERATIVES",
        uniqueConstraints = {
                @UniqueConstraint(name = "MISSIONREPORTS_OPERATIVES_UK", columnNames = {"MISSIONREPORT_ID", "OPERATIVE_ID"})
        }
)
@Getter
@Setter
public class OperativeReport extends AbstractRevisionedEntity implements Comparable<OperativeReport>, HasOperative {
    @Column(name = "ACHIEVEMENTS", length = 4000)
    private String achievements;

    @Column(name = "NOTES", length = 4000)
    private String notes;

    @ManyToOne(
            targetEntity = MissionReport.class,
            fetch = FetchType.EAGER,
            cascade = {CascadeType.REFRESH},
            optional = false
    )
    @JoinColumn(name = "MISSIONREPORT_ID", nullable = false)
    private MissionReport report;

    @ManyToOne(
            targetEntity = Operative.class,
            fetch = FetchType.EAGER,
            cascade = {CascadeType.REFRESH},
            optional = false
    )
    @JoinColumn(name = "OPERATIVE_ID", nullable = false)
    private Operative operative;

    /**
     * @param report mission to set to this operative report.
     */
    @Transient
    public void setMissionReport(MissionReport report) {
        MissionReport old = this.report;
        this.report = report;

        if (report != null && !report.equals(old)) {
            report.addOperativeReport(this);

            if (old != null) {
                old.removeOperativeReport(this);
            }
        }
    }

    /**
     * @param operative New operative to this object.
     */
    @Transient
    public void setOperative(Operative operative) {
        Operative old = this.operative;
        this.operative = operative;

        if (operative != null && !operative.equals(old)) {
            operative.addOperativeReport(this);

            if (old != null) {
                old.removeOperativeReport(this);
            }
        }
    }

    @Override
    public int compareTo(OperativeReport o) {
        return new CompareToBuilder()
                .append(getReport(), o.getReport())
                .append(getOperative(), o.getOperative())
                .toComparison();
    }

    @Override
    public OperativeReport clone() throws CloneNotSupportedException {
        OperativeReport result = (OperativeReport) super.clone();

        result.operative = operative.clone();
        result.report = report.clone();

        result.achievements = achievements;
        result.notes = notes;

        return result;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", OperativeReport.class.getSimpleName() + "[", "]")
                .merge(super.getToStringJoiner())

                .add("report=" + report.getId())
                .add("operative='" + operative.getName() + "'")

                .toString();
    }
}
