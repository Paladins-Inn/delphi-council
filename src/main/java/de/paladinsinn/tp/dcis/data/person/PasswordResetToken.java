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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.StringJoiner;

/**
 * PasswordResetToken -- A token for password resets.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.1.0  2021-05-07
 */
@Entity(name = "pwreset")
@Table(name = "PWRESETTOKENS")
@Getter
@Setter
@Builder
@NoArgsConstructor
public class PasswordResetToken extends AbstractEntity {
    private static final Logger LOG = LoggerFactory.getLogger(PasswordResetToken.class);

    @OneToOne(
            targetEntity = Person.class,
            fetch = FetchType.EAGER
    )
    @JoinColumn(name = "PERSON_ID", nullable = false)
    private Person person;

    /**
     * Creates a ConfirmationToken for the given person.
     *
     * @param person the person the confirmation token is generated for.
     */
    public PasswordResetToken(@NotNull final Person person) {
        this.person = person;

        prePersist();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PasswordResetToken.class.getSimpleName() + "[", "]")
                .merge(super.getToStringJoiner())
                .add("person=" + person)
                .toString();
    }
}
