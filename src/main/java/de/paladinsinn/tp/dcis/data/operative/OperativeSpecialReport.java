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
import de.paladinsinn.tp.dcis.data.HasName;
import de.paladinsinn.tp.dcis.data.specialmissions.SpecialMission;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.StringJoiner;

/**
 * OperativeSpecialReport -- The special report for a local table game.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.3.0  2021-04-18
 */
@Entity
@Audited
@Table(
        name = "SPECIALREPORTS_OPERATIVES",
        uniqueConstraints = {
                @UniqueConstraint(name = "SPECIALREPORTS_OPERATIVES_UK", columnNames = {"SPECIALMISSION_ID", "OPERATIVE_ID"}),
        }
)
@Getter
@Setter
public class OperativeSpecialReport extends AbstractRevisionedEntity implements Comparable<OperativeSpecialReport>, HasName, HasOperative {
    @Column(name = "NOTES", length = 4000)
    private String notes;

    @ManyToOne(
            targetEntity = SpecialMission.class,
            fetch = FetchType.EAGER,
            cascade = {CascadeType.REFRESH},
            optional = false
    )
    @JoinColumn(name = "SPECIALMISSION_ID", nullable = false)
    private SpecialMission specialMission;

    @ManyToOne(
            targetEntity = Operative.class,
            fetch = FetchType.EAGER,
            cascade = {CascadeType.REFRESH},
            optional = false
    )
    @JoinColumn(name = "OPERATIVE_ID", nullable = false)
    private Operative operative;


    /**
     * @param specialMission special mission report for this operative.
     */
    @Transient
    public void setSpecialMission(SpecialMission specialMission) {
        SpecialMission old = this.specialMission;
        this.specialMission = specialMission;

        if (specialMission != null && !specialMission.equals(old)) {
            specialMission.addOperativeReport(this);

            if (old != null) {
                old.removeOperativeReport(this);
            }
        }
    }

    @Override
    @Transient
    public void setOperative(Operative operative) {
        Operative old = this.operative;
        this.operative = operative;

        if (operative != null && !operative.equals(old)) {
            operative.addSpecialReport(this);

            if (old != null) {
                old.removeSpecialReport(this);
            }
        }
    }

    @Override
    @Transient
    public String getName() {
        return operative.getName();
    }

    @Override
    @Transient
    public void setName(final String name) {
    }


    @Override
    public int compareTo(OperativeSpecialReport o) {
        return new CompareToBuilder()
                .append(getSpecialMission(), o.getSpecialMission())
                .append(getOperative(), o.getOperative())
                .toComparison();
    }


    @Override
    public OperativeSpecialReport clone() throws CloneNotSupportedException {
        OperativeSpecialReport result = (OperativeSpecialReport) super.clone();

        result.operative = operative.clone();
        result.specialMission = specialMission.clone();

        result.notes = notes;

        return result;
    }


    @Override
    public String toString() {
        return new StringJoiner(", ", OperativeSpecialReport.class.getSimpleName() + "[", "]")
                .merge(super.getToStringJoiner())

                .add("report=" + specialMission.getId())
                .add("operative='" + operative.getName() + "'")

                .toString();
    }
}
