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

package de.paladinsinn.tp.dcis.model.confirmationtokens;

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

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.StringJoiner;

/**
 * ConfirmationToken --
 *
 * @author Kamer Elciar (<a href="https://kamer-dev.medium.com/spring-boot-user-registration-and-login-43a33ea19745">https://kamer-dev.medium.com/spring-boot-user-registration-and-login-43a33ea19745</a>)
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-04-10
 */
@RegisterForReflection
@Entity(name = "token")
@Table(name = "CONFIRMATIONTOKENS")
@SuperBuilder(toBuilder = true, setterPrefix = "with")
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@JsonDeserialize(builder = ConfirmationToken.ConfirmationTokenBuilder.class)
@Schema(description = "A confirmation Token")
@Getter
@Slf4j
public class ConfirmationToken extends AbstractEntity {
    @OneToOne(
            targetEntity = Person.class,
            fetch = FetchType.EAGER
    )
    @JoinColumn(name = "PERSON_ID", nullable = false)
    private Person person;

    @Override
    public String toString() {
        return new StringJoiner(", ", ConfirmationToken.class.getSimpleName() + "[", "]")
                .merge(super.getToStringJoiner())
                .add("person=" + person)
                .toString();
    }
}
