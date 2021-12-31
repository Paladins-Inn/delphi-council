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

package de.paladinsinn.tp.dcis.model.images;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.paladinsinn.tp.dcis.model.HasAvatar;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.bson.internal.Base64;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Lob;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

// FIXME 2021-12-31 klenkes74 Refactor to generic image instead of avatar.
/**
 * Avatar -- An avatar of the stormknight or threat.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-04-05
 */
@Embeddable
@Builder(toBuilder = true, setterPrefix = "with")
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = Avatar.AvatarBuilder.class)
@Schema(description = "User avatar.")
public class Avatar implements HasAvatar, Serializable, Cloneable {
    @Lob
    @Column(name = "AVATAR", length = 16777215)
    @Schema(description = "The avatar image itself encoded in BASE64.")
    private byte[] avatar;

    @Column(name = "USE_GRAVATAR")
    @Schema(description = "If the Gravatar service should be used.", example = "true", required = true)
    private boolean gravatar;

    @Override
    public String getAvatarImage() {
        return Base64.encode(avatar);
    }

    /**
     * @param emailAddress The email address to check gravatar for.
     * @return The gravatar link.
     */
    public String getGravatarLink(@NotNull final String emailAddress) {
        if (!gravatar)
            return "";

        try {
            return "https://www.gravatar.com/avatar/" + MessageDigest.getInstance("MD5")
                                                                                .digest(emailAddress.trim().getBytes(StandardCharsets.UTF_8)).toString();
        } catch (NoSuchAlgorithmException e) {
            throw new UnsupportedOperationException(e.getMessage());
        }
    }

    @Override
    public OutputStream getAvatar() {
        return getOutputStream(avatar);
    }


    @Override
    public Avatar clone() throws CloneNotSupportedException {
        Avatar result = (Avatar) super.clone();

        if (avatar != null) {
            result.avatar = Arrays.copyOf(avatar, avatar.length);
        }

        result.gravatar = gravatar;

        return result;
    }
}
