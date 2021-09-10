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

package de.paladinsinn.tp.dcis.model.person;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import de.paladinsinn.tp.dcis.model.AbstractRevisionedEntity;
import de.paladinsinn.tp.dcis.model.HasAvatar;
import de.paladinsinn.tp.dcis.model.missionreports.MissionReport;
import de.paladinsinn.tp.dcis.model.operative.Operative;
import de.paladinsinn.tp.dcis.model.specialmissions.SpecialMission;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.OutputStream;
import java.security.Principal;
import java.util.*;

/**
 * User -- The User data.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-03-13
 */
@UserDefinition
@Entity
@Table(
        name = "PERSONS",
        uniqueConstraints = {
                @UniqueConstraint(name = "PERSONS_USERNAME_UK", columnNames = "USERNAME"),
                @UniqueConstraint(name = "PERSONS_ID_UK", columnNames = "ID"),
                @UniqueConstraint(name = "PERSONS_EMAIL_UK", columnNames = "EMAIL")
        }
)
@SuperBuilder(toBuilder = true, setterPrefix = "with")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@JsonDeserialize(builder = Person.PersonBuilder.class)
@Schema(description = "A person playing or organising events")
public class Person extends AbstractRevisionedEntity implements HasAvatar, Principal {

    @Username
    @Column(name = "USERNAME", length = 20, nullable = false, unique = true, updatable = false)
    @Audited
    @Schema(description = "Username of this account", maxLength = 20, minLength = 2, example = "rlichti")
    private String username;


    @Column(name = "NAME", length = 100, nullable = false, unique = true)
    @Audited
    @Schema(description = "Full name of the user.", uniqueItems = true, maxLength = 100, minLength = 1, example = "Roland T. Lichti")
    private String name;

    @Column(name = "FIRST_NAME", length = 100, nullable = false)
    @Audited
    @Schema(description = "First (given) name of the user.", maxLength = 100, minLength = 1, example = "Roland")
    private String firstname;

    @Column(name = "LAST_NAME", length = 100, nullable = false)
    @Audited
    @Schema(description = "Last (family) name of the user.", maxLength = 100, minLength = 1, example = "Lichti")
    private String lastname;


    @Column(name = "EMAIL", length = 100, nullable = false, unique = true)
    @Audited
    @Schema(description = "Email address of the user", maxLength = 100, minLength = 4, uniqueItems = true, example = "rlichti@kaiserpfalz-edv.de")
    private String email;

    @Password
    @Column(name = "PASSWORD", length = 100, nullable = false)
    @Audited
    @Schema(description = "Crypted password of the user.", maxLength = 100)
    @JsonIgnore
    private String password;

    @Roles
    @Column(name = "ROLES", length = 200, nullable = false)
    @Audited
    @Schema(description = "Roles of this user (separated by comma).", maxLength = 100, minLength = 2, example = "PERSON,GM,ORGA", defaultValue = "PERSON", enumeration = {"PERSON", "GM", "ORGA", "JUDGE", "ADMIN"})
    private String roles;

    @Embedded
    @Builder.Default
    private AccountSecurityStatus status = new AccountSecurityStatus();

    @Embedded
    @Builder.Default
    private AvatarInformation avatar = new AvatarInformation();

    @Column(name = "LOCALE", length = 20, nullable = false)
    @Schema(description = "The language of the user.", maxLength = 20, defaultValue = "de", enumeration = {"de", "en"}, example = "de")
    private String locale;

    @OneToMany(
            targetEntity = Operative.class,
            mappedBy = "player",
            fetch = FetchType.EAGER,
            cascade = {CascadeType.REFRESH},

            orphanRemoval = true
    )
    @Schema(description = "Storm Knights of this user.", minItems = 0, maxItems = 20, uniqueItems = true)
    @Builder.Default
    private Set<Operative> operatives = new HashSet<>();

    @OneToMany(
            targetEntity = MissionReport.class,
            mappedBy = "gameMaster",
            fetch = FetchType.EAGER,
            cascade = {CascadeType.REFRESH},

            orphanRemoval = true
    )
    @JsonIgnore
    @Builder.Default
    private Set<MissionReport> reports = new HashSet<>();

    @OneToMany(
            targetEntity = SpecialMission.class,
            mappedBy = "gameMaster",
            fetch = FetchType.EAGER,
            cascade = {CascadeType.REFRESH},

            orphanRemoval = true
    )
    @JsonIgnore
    @Builder.Default
    private Set<SpecialMission> specialMissions = new HashSet<>();


    /**
     * Looking up a user by it's username.
     * @param username Username to look up.
     * @return The person object or NULL
     */
    public static Person findByUsername(@NotBlank final String username) {
        return find("username", username).firstResult();
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
    public boolean isEnabled() {
        return getStatus().isEnabled();
    }

    /**
     * @param state TRUE=this user is enabled, FALSE=the user is disabled.
     */
    private void setEnabled(boolean state) {
        status = status.toBuilder()
                    .withEnabled(state)
                    .build();
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
    private void setLocked(boolean state) {
        status = status.toBuilder()
                .withLocked(state)
                .build();
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
        avatar = avatar.toBuilder().withGravatar(false).build();
    }

    /**
     * enables the gravatar service.
     */
    public void enableGravatar() {
        avatar = avatar.toBuilder().withGravatar(true).build();
    }

    @Override
    public String getAvatarImage() {
        return avatar.getAvatarImage();
    }

    @Override
    public OutputStream getAvatar() {
        return avatar.getAvatar();
    }

    /**
     * Checks the password handed in.
     *
     * @param pw The password to check.
     * @return TRUE if the passwords match.
     */
    @SuppressWarnings("unused")
    public boolean checkPassword(final String pw) {
        boolean result = BcryptUtil.bcryptHash(pw).matches(password);

        if (result)
            status = status.toBuilder()
                    .withLastLogin(getNowUTC())
                    .build();
        return result;
    }

    /**
     * Updates the password of the user.
     *
     * @param pw The new password.
     */
    @SuppressWarnings("unused")
    public void setPassword(final String pw) {
        password = BcryptUtil.bcryptHash(pw);

        status = status.toBuilder()
                .withCredentialsChange(getNowUTC())
                .build();
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
        status = status.toBuilder()
                .withDeleted(getNowUTC())
                .build();

        username = UUID.randomUUID().toString();
        name = username;
        firstname = username;
        lastname = username;
        email = username;


    }


    /**
     * @param role role to check for this person.
     * @return If the person holds the requested role.
     */
    public boolean matchRole(@NotBlank String role) {
        return Arrays.asList(roles.split(",")).contains(role);
    }

    public boolean isAccountNonExpired() {
        return status.isAccountNonExpired();
    }

    public boolean isAccountNonLocked() {
        return status.isAccountNonLocked();
    }

    public boolean isCredentialsNonExpired() {
        return status.isCredentialsNonExpired();
    }

    @Override
    public Person clone() throws CloneNotSupportedException {
        Person result = (Person) super.clone();

        result.username = username;
        result.password = password;
        result.roles = roles;

        result.name = name;
        result.firstname = firstname;
        result.lastname = lastname;

        result.email = email;
        result.locale = locale;

        result.operatives.addAll(operatives);
        result.reports.addAll(reports);

        result.avatar = avatar;
        result.status = status;

        return result;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", getClass().getSimpleName() + "[", "]")
                .merge(super.getToStringJoiner())

                .add("userName='" + getName() + "'")
                .add("status=" + getStatus())
                .add("roles='" + roles + "'")

                .toString();
    }
}
