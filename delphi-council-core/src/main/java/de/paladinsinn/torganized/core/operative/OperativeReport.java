/*
 * Copyright (c) 2022. Roland T. Lichti
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.paladinsinn.torganized.core.operative;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.kaiserpfalzedv.commons.core.jpa.AbstractRevisionedJPAEntity;
import de.paladinsinn.torganized.core.common.HasOperative;
import de.paladinsinn.torganized.core.missions.MissionReport;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * OperativeReport --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-04-04
 */
@RegisterForReflection
@Entity
@Audited
@Table(
        name = "MISSIONREPORTS_OPERATIVES",
        uniqueConstraints = {
                @UniqueConstraint(name = "MISSIONREPORTS_OPERATIVES_UK", columnNames = {"MISSIONREPORT_ID", "OPERATIVE_ID"})
        }
)
@Jacksonized
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class OperativeReport extends AbstractRevisionedJPAEntity implements Comparable<OperativeReport>, HasOperative {
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
}
