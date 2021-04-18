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

package de.paladinsinn.tp.dcis.ui.views.person;

import com.sun.istack.NotNull;
import com.vaadin.flow.component.ComponentEvent;
import de.paladinsinn.tp.dcis.data.person.Person;
import de.paladinsinn.tp.dcis.ui.views.login.RegistrationView;
import lombok.Getter;

import java.util.Locale;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * PersonRegistrationEvent -- The user submitted the registration form.
 *
 * @author klenkes74 {literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-04-04
 */
@Getter
public class PersonRegistrationEvent extends ComponentEvent<RegistrationView> {

    private final String name, lastname, firstname,
            email, username, password;
    private final Locale locale;

    /**
     * Creates a new event using the given source and indicator whether the
     * event originated from the client side or the server side.
     *
     * @param source the source component
     */
    public PersonRegistrationEvent(
            @NotNull final RegistrationView source,
            @NotNull final String name,
            @NotNull final String lastname,
            @NotNull final String firstname,
            @NotNull final String email,
            @NotNull final String username,
            @NotNull final String password,
            @NotNull final Locale locale
    ) {
        super(source, false);

        this.name = name;
        this.lastname = lastname;
        this.firstname = firstname;
        this.email = email;
        this.username = username;
        this.password = password;
        this.locale = locale;
    }

    public Person getPerson() {
        Person result = new Person();

        result.setName(name);
        result.setLastname(lastname);
        result.setFirstname(firstname);
        result.setEmail(email);
        result.setUsername(username);
        result.setPassword(password);
        result.setLocale(locale);
        result.prePersist();
        result.disable();
        result.unlock();

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PersonRegistrationEvent)) return false;
        PersonRegistrationEvent that = (PersonRegistrationEvent) o;
        return getName().equals(that.getName()) && getLastname().equals(that.getLastname()) && getFirstname().equals(that.getFirstname()) && getEmail().equals(that.getEmail()) && getUsername().equals(that.getUsername()) && getPassword().equals(that.getPassword()) && getLocale().equals(that.getLocale());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getLastname(), getFirstname(), getEmail(), getUsername(), getPassword(), getLocale());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PersonRegistrationEvent.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("lastname='" + lastname + "'")
                .add("firstname='" + firstname + "'")
                .add("email='" + email + "'")
                .add("username='" + username + "'")
                .add("locale=" + locale)
                .toString();
    }
}
