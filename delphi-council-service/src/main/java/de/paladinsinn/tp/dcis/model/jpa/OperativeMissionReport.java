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
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * OperativeSpecialReport -- The special report for a local table game.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.3.0  2021-04-18
 */
@RegisterForReflection
@Entity
@Audited
@Table(
        name = "MISSIONREPORTS_OPERATIVES",
        uniqueConstraints = {
                @UniqueConstraint(name = "MISSIONREPORTS_OPERATIVES_UK", columnNames = {"MISSIONREPORT_ID", "OPERATIVE_ID"}),
        }
)
@Jacksonized
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class OperativeMissionReport extends AbstractRevisionedJPAEntity implements de.paladinsinn.tp.dcis.model.OperativeMissionReport {
    @Column(name = "NOTES", length = 4000)
    private String notes;

    @ManyToOne(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.REFRESH},
            optional = false
    )
    @JoinColumn(name = "MISSIONREPORT_ID", nullable = false)
    private MissionReport mission;

    @ManyToOne(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.REFRESH},
            optional = false
    )
    @JoinColumn(name = "OPERATIVE_ID", nullable = false)
    private Operative operative;


    @Override
    @Transient
    public String getName() {
        return operative.getName();
    }


    @Override
    public OperativeMissionReport clone() throws CloneNotSupportedException {
        OperativeMissionReport result = (OperativeMissionReport) super.clone();

        result.operative = operative.clone();
        result.mission = mission.clone();

        result.notes = notes;

        return result;
    }
}
