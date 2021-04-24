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

package de.paladinsinn.tp.dcis.security;

import com.vaadin.flow.server.VaadinSession;
import de.paladinsinn.tp.dcis.data.person.Person;
import de.paladinsinn.tp.dcis.data.person.PersonRepository;
import de.paladinsinn.tp.dcis.data.person.RoleName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.validation.constraints.NotNull;
import java.util.Locale;
import java.util.StringJoiner;

/**
 * LoggedInUser --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-04-07
 */
public class LoggedInUser {
    private static final Logger LOG = LoggerFactory.getLogger(LoggedInUser.class);

    private final PersonRepository repository;
    private Person person;

    private boolean readonly;
    private boolean isGm;
    private boolean isOrga;
    private boolean isAdmin;
    private boolean isJudge;

    private boolean allow = false;
    private boolean calculated = false;

    private final String defaultLocale;

    /**
     * @param repository repository to request person data from.
     * @param locale     locale of the current session for this user.
     */
    public LoggedInUser(
            @NotNull final PersonRepository repository,
            @NotNull @Value("${spring.web.locale:de}") final String locale
    ) {
        this.repository = repository;
        this.defaultLocale = locale;
    }

    /**
     * @return the logged in person.
     */
    public Person getPerson() {
        if (person != null) {
            return person;
        }

        person = repository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());

        enforceSavedLocale();

        return person;
    }

    private void enforceSavedLocale() {
        Locale savedLocale = null;

        if (person != null) {
            savedLocale = person.getLocale();
        }

        if (savedLocale == null) {
            savedLocale = VaadinSession.getCurrent().getLocale();
        }
        if (savedLocale == null) {
            savedLocale = Locale.forLanguageTag(defaultLocale);
        }
        if (savedLocale == null) {
            savedLocale = Locale.getDefault();
        }

        LOG.info("Changing locale. locale={}", savedLocale);
        VaadinSession.getCurrent().setLocale(savedLocale);
    }

    /**
     * @param person logged in user.
     */
    public void setPerson(@NotNull final Person person) {
        LOG.info("Replacing logged in user. oldUser={}, newUser={}", this.person, person);

        this.person = person;
    }

    /**
     * @param condition critera to add to future calls to check r/o status.
     */
    public void allow(boolean condition) {
        this.allow = condition;

        recalculate();
    }

    private void calculateReadOnly() {
        if (calculated) {
            return;
        }

        recalculate();
    }

    private void recalculate() {
        if (person == null) {
            getPerson();
        }

        isGm = person.matchRole(RoleName.GM);
        isOrga = person.matchRole(RoleName.ORGA);
        isAdmin = person.matchRole(RoleName.ADMIN);
        isJudge = person.matchRole(RoleName.JUDGE);

        readonly = !isOrga
                && !isAdmin
                && !allow;

        calculated = true;

        LOG.debug("User access calculation done. user='{}', readonly={}, gm={}, orga={}, admin={}, judge={}",
                person.getName(),
                readonly, isGm, isOrga, isAdmin, isJudge);
    }

    /**
     * @return if the user has only read-only access.
     */
    public boolean isReadonly() {
        calculateReadOnly();
        return readonly;
    }

    /**
     * @return if the user is a registered GM.
     */
    public boolean isGm() {
        calculateReadOnly();
        return isGm;
    }

    /**
     * @return if the user is part of the organisers of the campaign.
     */
    public boolean isOrga() {
        calculateReadOnly();
        return isOrga;
    }

    /**
     * @return if the user is admin of this system.
     */
    public boolean isAdmin() {
        calculateReadOnly();
        return isAdmin;
    }

    /**
     * @return if the user is a judge of the campaign.
     */
    public boolean isJudge() {
        calculateReadOnly();
        return isJudge;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", LoggedInUser.class.getSimpleName() + "[", "]")
                .add("person=" + person)
                .add("readonly=" + readonly)
                .add("isGm=" + isGm)
                .add("isOrga=" + isOrga)
                .add("isAdmin=" + isAdmin)
                .add("isJudge=" + isJudge)
                .add("allow=" + allow)
                .add("calculated=" + calculated)
                .add("defaultLocale='" + defaultLocale + "'")
                .toString();
    }
}
