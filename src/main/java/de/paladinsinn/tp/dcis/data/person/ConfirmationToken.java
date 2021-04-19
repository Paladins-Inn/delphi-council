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

package de.paladinsinn.tp.dcis.data.person;

import com.sun.istack.NotNull;
import de.paladinsinn.tp.dcis.data.AbstractEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.StringJoiner;

/**
 * ConfirmationToken --
 *
 * @author Kamer Elciar (<a href="https://kamer-dev.medium.com/spring-boot-user-registration-and-login-43a33ea19745">https://kamer-dev.medium.com/spring-boot-user-registration-and-login-43a33ea19745</a>)
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-04-10
 */
@Entity(name = "token")
@Table(name = "CONFIRMATIONTOKENS")
@Getter
@Setter
@NoArgsConstructor
public class ConfirmationToken extends AbstractEntity {
    private static final Logger LOG = LoggerFactory.getLogger(ConfirmationToken.class);

    @OneToOne(
            targetEntity = Person.class,
            fetch = FetchType.EAGER
    )
    @JoinColumn(name = "PERSON_ID", nullable = false)
    private Person person;

    public ConfirmationToken(@NotNull final Person person) {
        this.person = person;

        prePersist();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ConfirmationToken.class.getSimpleName() + "[", "]")
                .merge(super.getToStringJoiner())
                .add("person=" + person)
                .toString();
    }
}
