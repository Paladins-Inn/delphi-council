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

package de.paladinsinn.tp.dcis.data.specialmissions;

import de.paladinsinn.tp.dcis.data.AbstractRevisionedEntity;
import de.paladinsinn.tp.dcis.data.Clearance;
import de.paladinsinn.tp.dcis.data.HasClearance;
import de.paladinsinn.tp.dcis.data.HasId;
import de.paladinsinn.tp.dcis.data.operative.OperativeSpecialReport;
import de.paladinsinn.tp.dcis.data.person.Person;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;
import java.util.UUID;

/**
 * SpecialMission -- A private table mission.
 *
 * @author klenkes74 {literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-04-18
 */
@Entity
@Audited
@Table(
        name = "SPECIALMISSIONS",
        uniqueConstraints = {
                @UniqueConstraint(name = "SPECIALMISSIONS_CODE_UK", columnNames = "CODE"),
                @UniqueConstraint(name = "SPECIALMISSIONS_TITLE_UK", columnNames = "TITLE")
        }
)
@Getter
@Setter
public class SpecialMission extends AbstractRevisionedEntity implements HasId, HasClearance, Cloneable {
    private static final Logger LOG = LoggerFactory.getLogger(SpecialMission.class);

    @org.hibernate.annotations.Type(type = "org.hibernate.type.UUIDCharType")
    @Column(name = "CODE", length = 36, nullable = false, updatable = false, unique = true)
    private UUID code;

    @Column(name = "TITLE", nullable = false)
    private String title;

    @Column(name = "CLEARANCE", nullable = false)
    @Enumerated(EnumType.STRING)
    private Clearance clearance;

    @Column(name = "DESCRIPTION", length = 4000)
    private String description;

    @Column(name = "PAYMENT", nullable = false)
    private int payment;

    @Column(name = "XP", nullable = false)
    private int xp;

    @Column(name = "IMAGE")
    private String imageUrl;

    @Column(name = "PUBLICATION")
    private String publication;

    @ManyToOne(
            targetEntity = Person.class,
            fetch = FetchType.EAGER,
            cascade = {CascadeType.REFRESH},
            optional = false
    )
    @JoinColumn(name = "GM", nullable = false)
    private Person gameMaster;

    @OneToMany(
            targetEntity = OperativeSpecialReport.class,
            mappedBy = "specialMission",
            fetch = FetchType.EAGER,
            cascade = {CascadeType.REFRESH},
            orphanRemoval = true
    )
    private Set<OperativeSpecialReport> operatives = new HashSet<>();


    @Column(name = "MISSION_DATE")
    private LocalDate missionDate;

    @Column(name = "NOTES", length = 4000)
    private String notes;

    @Transient
    public void setGameMaster(Person gameMaster) {
        Person old = this.gameMaster;
        this.gameMaster = gameMaster;

        if (gameMaster != null && !gameMaster.equals(old)) {
            gameMaster.addSpecialMission(this);

            if (old != null) {
                old.removeSpecialMission(this);
            }
        }
    }


    public void addOperativeReport(OperativeSpecialReport operative) {
        if (operative != null && !operatives.contains(operative)) {
            operatives.add(operative);

            operative.setSpecialMission(this);
        }
    }

    public void removeOperativeReport(OperativeSpecialReport operative) {
        if (operative != null && operatives.contains(operative)) {
            operatives.remove(operative);

            operative.setSpecialMission(null);
        }
    }


    @Override
    public void prePersist() {
        if (code == null) {
            code = UUID.randomUUID();
        }
    }


    @Override
    public SpecialMission clone() throws CloneNotSupportedException {
        SpecialMission result = (SpecialMission) super.clone();

        result.code = code;
        result.title = title;
        result.clearance = clearance;
        result.description = description;
        result.payment = payment;
        result.xp = xp;
        result.imageUrl = imageUrl;
        result.publication = publication;

        return result;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SpecialMission.class.getSimpleName() + "[", "]")
                .add("code=" + code)
                .add("title='" + title + "'")
                .add("clearance=" + clearance)
                .add("payment=" + payment)
                .add("xp=" + xp)
                .toString();
    }
}
