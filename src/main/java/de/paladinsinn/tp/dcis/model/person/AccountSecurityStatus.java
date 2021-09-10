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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.Period;
import java.time.ZoneOffset;
import java.util.StringJoiner;

/**
 * AccountSecurityStatus -- Spring security states of an account.
 *
 * <ul>
 *     <li>Accounts are valid for {@link #ACCOUNT_IS_VALID_FOR} (by default 10 years).</li>
 *     <li>Credentials are valid for {@link #CREDENTIALS_VALID_FOR} (by default 5 years).</li>
 * </ul>
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-03-29
 */
@Embeddable
@Getter
@Builder(toBuilder = true, setterPrefix = "with")
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = AccountSecurityStatus.AccountSecurityStatusBuilder.class)
@Schema(description = "Security status of an acoount.")
public class AccountSecurityStatus implements Serializable, Cloneable {
    /** Default validity of credentials. */
    private static final Period CREDENTIALS_VALID_FOR = Period.ofYears(5);
    /** Default validity of an account. If not logged in within this timespan, the account will be removed. */
    private static final Period ACCOUNT_IS_VALID_FOR = Period.ofYears(10);

    @Column(name = "ACCOUNT_ENABLED", nullable = false)
    @Schema(description = "The account is enabled.", example = "true")
    @Builder.Default
    private boolean enabled = true;

    @Column(name = "ACCOUNT_LOCKED", nullable = false)
    @Schema(description = "The account is locked.", example = "false")
    @Builder.Default
    private boolean locked = false;

    @Column(name = "ACCOUNT_EXPIRY", nullable = false)
    @Schema(description = "The account will expire on this instant.", example = "2031-09-10T10:00:00.000000-02:00")
    @Builder.Default
    private OffsetDateTime expiry = OffsetDateTime.now(ZoneOffset.UTC).plus(ACCOUNT_IS_VALID_FOR);

    @Column(name = "CREDENTIALS_CHANGED", nullable = false)
    @Schema(description = "Last change of credentials.", example = "2021-09-10T10:43:40.143341-02:00")
    @Builder.Default
    private OffsetDateTime credentialsChange = OffsetDateTime.now(ZoneOffset.UTC);

    @Column(name = "LAST_LOGIN")
    @Schema(description = "Last login timestamp.", example = "2021-09-10T10.43:40.143341-02:00")
    private OffsetDateTime lastLogin;

    @Column(name = "DELETED")
    @Schema(description = "The account is deleted. The person object is still there for reference reasons but all personal data is deleted.", example = "2021-09-08T20:43:23.521312")
    private OffsetDateTime deleted;

    /**
     * @return If the account is not deleted.
     */
    public boolean isAccountNonDeleted() {
        return deleted == null;
    }

    /**
     * @return If the acount is not expired.
     */
    public boolean isAccountNonExpired() {
        return OffsetDateTime.now(ZoneOffset.UTC)
                .isBefore(expiry);
    }

    /**
     * @return If the account is not locked.
     */
    public boolean isAccountNonLocked() {
        return !locked;
    }

    /**
     * Marks the date of the credential change.
     */
    public void setCredentialsChange() {
        credentialsChange = OffsetDateTime.now(ZoneOffset.UTC);
    }

    /**
     * @return If the credential is not expired.
     */
    public boolean isCredentialsNonExpired() {
        return OffsetDateTime.now(ZoneOffset.UTC)
                .isBefore(credentialsChange.plus(CREDENTIALS_VALID_FOR));
    }

    /**
     * @return If the account is enabled.
     */
    public boolean isEnabled() {
        return enabled;
    }

    public AccountSecurityStatus clone() throws CloneNotSupportedException {
        AccountSecurityStatus result = (AccountSecurityStatus) super.clone();

        result.enabled = enabled;
        result.locked = locked;

        if (credentialsChange != null) {
            result.credentialsChange = OffsetDateTime.from(credentialsChange);
        }

        if (lastLogin != null) {
            result.lastLogin = OffsetDateTime.from(lastLogin);
        }

        if (expiry != null) {
            result.expiry = OffsetDateTime.from(expiry);
        }

        if (deleted != null) {
            result.deleted = OffsetDateTime.from(deleted);
        }

        log.trace("Cloned AccountSecurityStatus. orig={}, clone={}", this, result);
        return result;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", AccountSecurityStatus.class.getSimpleName() + "[", "]")
                .add("enabled=" + enabled)
                .add("locked=" + locked)
                .add("credentialsChange=" + credentialsChange)
                .add("lastLogin=" + lastLogin)
                .add("expiry=" + expiry)
                .add("deleted=" + deleted)
                .toString();
    }
}
