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
import liquibase.util.MD5Util;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Lob;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Arrays;

/**
 * AvatarInformation --
 *
 * @author klenkes74 {literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-04-05
 */
@Embeddable
@Getter
@Setter
public class AvatarInformation implements Serializable, Cloneable {
    @Lob
    @Column(name = "AVATAR", length = 16777215)
    private byte[] avatar;

    @Column(name = "USE_GRAVATAR")
    private boolean gravatar;

    public Image getAvatarImage(@NotNull final String fileName, @NotNull final String emailAddress) {
        Image result = new Image();

        StreamResource data = getAvatar(fileName);
        if (data != null) {
            result.setSrc(data);
        } else if (gravatar) {
            result.setSrc(getGravatarLink(emailAddress));
        }

        return result;
    }

    public String getGravatarLink(@NotNull final String emailAddress) {
        if (!gravatar)
            return "";

        return "https://www.gravatar.com/avatar/" + MD5Util.computeMD5(emailAddress.trim());
    }

    public StreamResource getAvatar(@NotNull final String fileName) {
        if (avatar != null) {
            return new StreamResource(fileName, () -> new ByteArrayInputStream(avatar));
        } else {
            return null;
        }
    }

    /**
     * @param data data to read.
     * @throws IOException If the data can't be read.
     */
    public void setAvatar(InputStream data) throws IOException {
        avatar = data.readAllBytes();
    }


    public void disableGravatar() {
        gravatar = false;
    }

    public void enableGravatar() {
        gravatar = true;
    }

    @Override
    public AvatarInformation clone() throws CloneNotSupportedException {
        AvatarInformation result = (AvatarInformation) super.clone();

        if (avatar != null) {
            result.avatar = Arrays.copyOf(avatar, avatar.length);
        }

        result.gravatar = gravatar;

        return result;
    }
}
