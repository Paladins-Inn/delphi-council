/*
 * Copyright (c) 2021 Kaiserpfalz EDV-Service, Roland T. Lichti
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package de.paladinsinn.tp.dcis.model.passwordresettokens;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.paladinsinn.tp.dcis.model.AbstractEntity;
import de.paladinsinn.tp.dcis.model.person.Person;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.persistence.*;
import java.util.StringJoiner;

/**
 * PasswordResetToken -- A token for password resets.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.1.0  2021-05-07
 */
@RegisterForReflection
@Entity(name = "pwreset")
@Table(name = "PWRESETTOKENS")
@SuperBuilder(toBuilder = true, setterPrefix = "with")
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@JsonDeserialize(builder = PasswordResetToken.PasswordResetTokenBuilder.class)
@Schema(description = "A person playing or organising events")
@Getter
@Slf4j
public class PasswordResetToken extends AbstractEntity {
    @OneToOne(
            targetEntity = Person.class,
            fetch = FetchType.EAGER
    )
    @JoinColumn(name = "PERSON_ID", nullable = false)
    private Person person;

    @Override
    public String toString() {
        return new StringJoiner(", ", PasswordResetToken.class.getSimpleName() + "[", "]")
                .merge(super.getToStringJoiner())
                .add("person=" + person)
                .toString();
    }
}
