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

package de.paladinsinn.delphicouncil.data.person;

import com.sun.istack.NotNull;
import de.paladinsinn.delphicouncil.data.AbstractRevisionedEntity;
import de.paladinsinn.delphicouncil.data.missions.MissionReport;
import de.paladinsinn.delphicouncil.data.operative.Operative;
import de.paladinsinn.delphicouncil.data.operative.OperativeReport;
import liquibase.util.MD5Util;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * User -- The User data.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-03-13
 */
@Entity
@Table(
        name = "PERSONS",
        uniqueConstraints = {
                @UniqueConstraint(name = "PERSONS_USERNAME_UK", columnNames = "USERNAME"),
                @UniqueConstraint(name = "PERSONS_ID_UK", columnNames = "ID"),
                @UniqueConstraint(name = "PERSONS_EMAIL_UK", columnNames = "EMAIL")
        }
)
@Getter
@Setter
public class Person extends AbstractRevisionedEntity implements UserDetails {
    public static final int ITERATION_COUNT = 10;

    @Column(name = "USERNAME", length = 20, nullable = false, unique = true, updatable = false)
    @Audited
    private String username;

    @Column(name = "FIRST_NAME", length = 100, nullable = false)
    @Audited
    private String firstname;

    @Column(name = "LAST_NAME", length = 100, nullable = false)
    @Audited
    private String lastname;

    @Column(name = "EMAIL", length = 100, nullable = false, unique = true)
    @Audited
    private String email;

    @Column(name = "PASSWORD", length = 100, nullable = false)
    @Audited
    private String password;

    @Embedded
    private AccountSecurityStatus status = new AccountSecurityStatus();

    @ElementCollection(
            targetClass = Role.class,
            fetch = FetchType.EAGER
    )
    @CollectionTable(
            name = "ROLES",
            joinColumns = @JoinColumn(name = "PERSON_ID")
    )
    private Set<Role> roles = new HashSet<>();

    @OneToMany(
            targetEntity = Operative.class,
            mappedBy = "player",
            fetch = FetchType.EAGER,
            cascade = {CascadeType.PERSIST, CascadeType.REFRESH},

            orphanRemoval = true
    )
    private Set<Operative> operatives = new HashSet<>();

    @OneToMany(
            targetEntity = MissionReport.class,
            mappedBy = "gameMaster",
            fetch = FetchType.EAGER,
            cascade = {CascadeType.PERSIST, CascadeType.REFRESH},

            orphanRemoval = true
    )
    private Set<MissionReport> reports = new HashSet<>();


    public String getName() {
        return status.isAccountNonDeleted() ? username : "-deleted-";
    }

    public void setName(final String name) {
        if (name != null && !name.isBlank() && !"-deleted-".equals(name)) {
            this.username = name;
        }
    }

    /**
     * <a href="https://www.gravatar.com/">Gravatar</a> delivers an avatar by using the email address.
     * @return the gravatar of the email address of this user.
     */
    public String getGravatar() {
        return "https://www.gravatar.com/avatar/" + MD5Util.computeMD5(email.trim());
    }

    /**
     * Checks the password handed in.
     *
     * @param pw The password to check.
     * @return TRUE if the passwords match.
     */
    @SuppressWarnings("unused")
    public boolean checkPassword(final String pw) {
        boolean result = BCrypt.checkpw(pw, password);

        if (result) status.setLastLogin(getNowUTC());
        return result;
    }

    /**
     * Updates the password of the user.
     *
     * @param pw The new password.
     */
    @SuppressWarnings("unused")
    public void setPassword(final String pw) {
        password = BCrypt.hashpw(pw, BCrypt.gensalt(ITERATION_COUNT));

        status.setCredentialsChange(getNowUTC());
        modified = status.getCredentialsChange();
    }


    public void addOperative(@NotNull Operative operative) {
        if (operatives.contains(operative))
            return;

        operatives.add(operative);
        operative.setPlayer(this);
    }

    public void removeOperative(@NotNull Operative operative) {
        if (!operatives.contains(operative))
            return;

        operatives.remove(operative);
        operative.setPlayer(null);
    }


    public void addGameMasterReport(@NotNull MissionReport report) {
        if (reports.contains(report))
            return;

        reports.add(report);
        report.setGameMaster(this);
    }

    public void removeGameMasterReport(@NotNull MissionReport report) {
        if (!reports.contains(report))
            return;

        reports.remove(report);
        report.setGameMaster(null);
    }


    public Set<OperativeReport> getOperativeReports() {
        SortedSet<OperativeReport> result = new ConcurrentSkipListSet<>();

        operatives.forEach(operative -> result.addAll(operative.getReports()));

        return Collections.unmodifiableSet(result);
    }


    /**
     * Marks the user as deleted.
     */
    @SuppressWarnings("unused")
    public void markDeleted() {
        status.setDeleted(getNowUTC());
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public boolean isAccountNonExpired() {
        return status.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return status.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return status.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return status.isEnabled();
    }


    @Override
    public String toString() {
        return new StringJoiner(", ", getClass().getSimpleName() + "[", "]")
                .merge(super.getToStringJoiner())
                .add("userName='" + getName() + "'")
                .add("status=" + getStatus())
                .toString();
    }
}
