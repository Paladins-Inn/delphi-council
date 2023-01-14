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
import de.kaiserpfalzedv.commons.jpa.AbstractRevisionedJPAEntity;
import de.kaiserpfalzedv.rpg.torg.model.core.SuccessState;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;

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
        name = "DISPATCHREPORTS",
        uniqueConstraints = {
                @UniqueConstraint(name = "DISPATCHREPORTS_ID_UK", columnNames = "ID")
        }
)
@Jacksonized
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class Engagment extends AbstractRevisionedJPAEntity implements de.paladinsinn.tp.dcis.model.Engagment {
    @ManyToOne(
            cascade = {CascadeType.REFRESH},
            fetch = FetchType.EAGER,
            optional = false,
            targetEntity = Dispatch.class
    )
    @JoinColumn(
            name = "DISPATCH_ID",
            referencedColumnName = "ID",
            nullable = false,
            foreignKey = @ForeignKey(name = "REPORTS_DISPATCHES_FK")
    )
    private Dispatch dispatch;

    @Audited
    @Column(name = "GAME_MASTER", length = 100, nullable = false)
    private String gameMaster;


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



    // FIXME 2023-01-06 klenkes Check if we need to load the data instead of copying it (I think _THIS_ implementation won't work).
    public void setDispatch(de.paladinsinn.tp.dcis.model.Dispatch dispatch) {
        dispatch = Dispatch.copyData(dispatch);
    }


    @Override
    public
    @Size(min = 3, max = 100, message = "The length of the string must be between 3 and 100 characters long.")
    @Pattern(
            regexp = "^[a-zA-Z][-a-zA-Z0-9]{1,61}(.[a-zA-Z][-a-zA-Z0-9]{1,61}){0,4}$",
            message = "The string must match the pattern '^[a-zA-Z][-a-zA-Z0-9]{1,61}(.[a-zA-Z][-a-zA-Z0-9]{1,61}){0,4}$'"
    )
    String getName() {
        if (dispatch == null || gameMaster == null || date == null) {
            return getId().toString();
        }

        return String.format("%s (%s, %s)", dispatch.getShortName(), gameMaster, date);
    }

    @Override
    public String getCode() {
        if (dispatch == null) {
            return getId().toString();
        }

        return dispatch.getCode();
    }


    public static Engagment copyData(final de.paladinsinn.tp.dcis.model.Engagment orig) {
        if (Engagment.class.isAssignableFrom(orig.getClass())) {
            return (Engagment) orig;
        }

        return Engagment.builder()
                .id(orig.getId())
                .version(orig.getVersion())
                .created(orig.getCreated())
                .modified(orig.getModified())
                .revisioned(orig.getRevisioned())



                .build();
    }


    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public Engagment clone() {
        return toBuilder().build();
    }
}
