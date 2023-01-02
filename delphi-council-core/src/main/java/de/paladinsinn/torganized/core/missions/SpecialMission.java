/*
 * Copyright (c) 2022. Roland T. Lichti
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.paladinsinn.torganized.core.missions;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.kaiserpfalzedv.commons.core.jpa.AbstractRevisionedJPAEntity;
import de.kaiserpfalzedv.commons.core.resources.HasId;
import de.kaiserpfalzedv.rpg.torg.model.actors.Clearance;
import de.paladinsinn.torganized.core.common.HasClearance;
import de.paladinsinn.torganized.core.operative.OperativeSpecialReport;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * SpecialMission -- A private table mission.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-04-18
 */
@RegisterForReflection
@Entity
@Audited
@Table(
        name = "SPECIALMISSIONS",
        uniqueConstraints = {
                @UniqueConstraint(name = "SPECIALMISSIONS_CODE_UK", columnNames = "CODE"),
                @UniqueConstraint(name = "SPECIALMISSIONS_TITLE_UK", columnNames = "TITLE")
        }
)
@Jacksonized
@SuperBuilder(toBuilder = true)
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Slf4j
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class SpecialMission extends AbstractRevisionedJPAEntity implements HasId, HasClearance, Cloneable {
    @org.hibernate.annotations.Type(type = "org.hibernate.type.UUIDCharType")
    @Column(name = "CODE", length = 36, nullable = false, updatable = false, unique = true)
    @Builder.Default
    private UUID code = UUID.randomUUID();

    @Column(name = "TITLE", length = 100, nullable = false)
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

    @Column(name = "GAME_MASTER", length = 100)
    @Setter
    private String gameMaster;

    @OneToMany(
            targetEntity = OperativeSpecialReport.class,
            mappedBy = "specialMission",
            fetch = FetchType.EAGER,
            cascade = {CascadeType.REFRESH},
            orphanRemoval = true
    )
    @Builder.Default
    private Set<OperativeSpecialReport> operatives = new HashSet<>();


    @Column(name = "MISSION_DATE")
    private LocalDate missionDate;

    @Column(name = "NOTES", length = 4000)
    private String notes;


    /**
     * @param operative A new operative to this local table game.
     */
    public void addOperativeReport(OperativeSpecialReport operative) {
        if (operative != null && !operatives.contains(operative)) {
            operatives.add(operative);

            operative.setSpecialMission(this);
        }
    }

    /**
     * @param operative The operative to remove from this local table game.
     */
    public void removeOperativeReport(OperativeSpecialReport operative) {
        if (operative != null && operatives.contains(operative)) {
            operatives.remove(operative);

            operative.setSpecialMission(null);
        }
    }


    @PrePersist
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
        result.gameMaster = gameMaster;
        result.notes = notes;
        result.missionDate = missionDate;

        return result;
    }
}
