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
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.StreamResource;
import de.paladinsinn.tp.dcis.data.AbstractRevisionedEntity;
import de.paladinsinn.tp.dcis.data.HasAvatar;
import de.paladinsinn.tp.dcis.data.missions.MissionReport;
import de.paladinsinn.tp.dcis.data.operative.Operative;
import de.paladinsinn.tp.dcis.data.specialmissions.SpecialMission;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

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
public class Person extends AbstractRevisionedEntity implements UserDetails, HasAvatar {
    /**
     * Iteration count for the BCrypt password encoding.
     */
    public static final int ITERATION_COUNT = 10;

    @Column(name = "NAME", length = 100, nullable = false, unique = true)
    @Audited
    private String name;

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

    @Embedded
    private AvatarInformation avatar = new AvatarInformation();

    @ElementCollection(
            targetClass = Role.class,
            fetch = FetchType.EAGER
    )
    @CollectionTable(
            name = "ROLES",
            joinColumns = @JoinColumn(name = "PERSON_ID")
    )
    private Set<Role> roles = new HashSet<>();

    @Column(name = "LOCALE", length = 20, nullable = false)
    private String locale;

    @OneToMany(
            targetEntity = Operative.class,
            mappedBy = "player",
            fetch = FetchType.EAGER,
            cascade = {CascadeType.REFRESH},

            orphanRemoval = true
    )
    private Set<Operative> operatives = new HashSet<>();

    @OneToMany(
            targetEntity = MissionReport.class,
            mappedBy = "gameMaster",
            fetch = FetchType.EAGER,
            cascade = {CascadeType.REFRESH},

            orphanRemoval = true
    )
    private Set<MissionReport> reports = new HashSet<>();

    @OneToMany(
            targetEntity = SpecialMission.class,
            mappedBy = "gameMaster",
            fetch = FetchType.EAGER,
            cascade = {CascadeType.REFRESH},

            orphanRemoval = true
    )
    private Set<SpecialMission> specialMissions = new HashSet<>();


    /**
     * @return The name of the person (or "-deleted-" if the account has been deleted).
     */
    public String getName() {
        return status.isAccountNonDeleted() ? name : "-deleted-";
    }

    /**
     * @param name the name of the person.
     */
    public void setName(final String name) {
        if (name != null && !name.isBlank() && !"-deleted-".equals(name)) {
            this.name = name;
        }
    }


    /**
     * @return the first name of the person (or "-deleted-" when the account is marked as deleted)
     */
    public String getFirstname() {
        return status.isAccountNonDeleted() ? firstname : "-deleted-";
    }

    /**
     * @param name the new first name of the person.
     */
    public void setFirstname(final String name) {
        if (name != null && !name.isBlank() && !"-deleted-".equals(name)) {
            this.firstname = name;
        }
    }


    /**
     * @return the family name of the person (or "-deleted-" when the account is marked as deleted)
     */
    public String getLastname() {
        return status.isAccountNonDeleted() ? lastname : "-deleted-";
    }

    /**
     * @param name the new family name of the person.
     */
    public void setLastname(final String name) {
        if (name != null && !name.isBlank() && !"-deleted-".equals(name)) {
            this.lastname = name;
        }
    }


    /**
     * @return the username of the person (or "-deleted-" if the account is marked as deleted)
     */
    public String getUsername() {
        return status.isAccountNonDeleted() ? username : "-deleted-";
    }

    /**
     * @param username the new username of the person.
     */
    public void setUsername(final String username) {
        if (username != null && !username.isBlank() && !"-deleted-".equals(username)) {
            this.username = username;
        }
    }

    /**
     * @return The locale of the user.
     */
    public Locale getLocale() {
        return Locale.forLanguageTag(locale);
    }

    /**
     * @param locale the new locale for this user.
     */
    public void setLocale(@NotNull final Locale locale) {
        this.locale = locale.getLanguage();
    }

    /**
     * Enables the user.
     */
    public void enable() {
        setEnabled(true);
    }

    /**
     * Disables the user.
     */
    public void disable() {
        setEnabled(false);
    }

    /**
     * @return If the user is enabled.
     */
    @Override
    public boolean isEnabled() {
        return getStatus().isEnabled();
    }

    /**
     * @param state TRUE=this user is enabled, FALSE=the user is disabled.
     */
    public void setEnabled(boolean state) {
        getStatus().setEnabled(state);
    }

    /**
     * Unlocks the person.
     */
    public void unlock() {
        setLocked(false);
    }

    /**
     * Locks the person.
     */
    public void lock() {
        setLocked(true);
    }

    /**
     * @return If the user is locked.
     */
    public boolean isLocked() {
        return getStatus().isLocked();
    }

    /**
     * @param state TRUE=the user is locked, FALSE=the user is unlocked.
     */
    public void setLocked(boolean state) {
        getStatus().setLocked(state);
    }

    /**
     * <a href="https://www.gravatar.com/">Gravatar</a> delivers an avatar by using the email address.
     *
     * @return the gravatar of the email address of this user.
     */
    public String getGravatar() {
        return avatar.getGravatarLink(email);
    }

    /**
     * @return If the gravatar service is alled to be used for this person.
     */
    public boolean isGravatarAllowed() {
        return avatar.isGravatar();
    }

    /**
     * disables the gravatar service.
     */
    public void disableGravatar() {
        avatar.setGravatar(false);
    }

    /**
     * enables the gravatar service.
     */
    public void enableGravatar() {
        avatar.setGravatar(true);
    }

    @Override
    public Image getAvatarImage() {
        return avatar.getAvatarImage(getId().toString() + ".png", email);
    }

    @Override
    public StreamResource getAvatar() {
        return avatar.getAvatar(getId() + ".png");
    }

    @Override
    public void setAvatar(InputStream stream) throws IOException {
        avatar.setAvatar(stream);
    }

    /**
     * Checks the password handed in.
     *
     * @param pw The password to check.
     * @return TRUE if the passwords match.
     */
    @SuppressWarnings("unused")
    public boolean checkPassword(final String pw) {
        boolean result = new BCryptPasswordEncoder().matches(pw, password);

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
        password = new BCryptPasswordEncoder().encode(pw);

        preUpdate();
        status.setCredentialsChange(getModified());
    }

    /**
     * @param roles of this person.
     */
    public void setRoles(Collection<Role> roles) {
        this.roles.clear();
        this.roles.addAll(roles);
    }

    /**
     * @return roles of this person.
     */
    public Set<Role> getRoles() {
        return new HashSet<>(roles);
    }


    /**
     * @param operative A new operative of this person.
     */
    public void addOperative(@NotNull Operative operative) {
        if (operatives.contains(operative))
            return;

        operatives.add(operative);
        operative.setPlayer(this);
    }

    /**
     * @param operative Operative to be removed.
     */
    public void removeOperative(@NotNull Operative operative) {
        if (!operatives.contains(operative))
            return;

        operatives.remove(operative);
        operative.setPlayer(null);
    }

    /**
     * @param report a new mission this person is GM for.
     */
    public void addGameMasterReport(@NotNull MissionReport report) {
        if (reports.contains(report))
            return;

        reports.add(report);
        report.setGameMaster(this);
    }

    /**
     * @param report deletes a mission execution for this GM.
     */
    public void removeGameMasterReport(@NotNull MissionReport report) {
        if (!reports.contains(report))
            return;

        reports.remove(report);
        report.setGameMaster(null);
    }


    /**
     * @param specialMission a local table play for this GM.
     */
    public void addSpecialMission(@NotNull SpecialMission specialMission) {
        if (specialMissions.contains(specialMission))
            return;

        specialMissions.add(specialMission);
        specialMission.setGameMaster(this);
    }

    /**
     * @param specialMission a local table play for this GM.
     */
    public void removeSpecialMission(@NotNull SpecialMission specialMission) {
        if (!specialMissions.contains(specialMission))
            return;

        specialMissions.remove(specialMission);
        specialMission.setGameMaster(null);
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

    /**
     * @param role role to check for this person.
     * @return If the person holds the requested role.
     */
    public boolean matchRole(RoleName role) {
        return roles.stream().anyMatch(r -> role.equals(RoleName.valueOf(r.getAuthority())));
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
    public Person clone() throws CloneNotSupportedException {
        Person result = (Person) super.clone();

        result.name = name;
        result.username = username;
        result.firstname = firstname;
        result.lastname = lastname;
        result.email = email;
        result.password = password;
        result.locale = locale;

        result.roles = Set.copyOf(roles);
        result.operatives = Set.copyOf(operatives);
        result.reports = Set.copyOf(reports);

        result.status = status.clone();
        result.avatar = avatar.clone();

        return result;
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
