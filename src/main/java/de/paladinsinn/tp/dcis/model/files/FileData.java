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

package de.paladinsinn.tp.dcis.model.files;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.paladinsinn.tp.dcis.model.HasImage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Lob;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Arrays;

/**
 * FileData -- An embedded file (byte coded).
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 2.0.0  2021-12-31
 * @since 2.0.0  2021-12-31
 */
@SuperBuilder(setterPrefix = "with", toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = FileData.FileDataBuilder.class)
@Embeddable
@Schema(description = "Files saved on the server.")
public class FileData implements HasImage, Serializable, Cloneable {
    @Column(name = "FILE_NAME", length = 100)
    @Schema(description = "The name of the file.")
    private String name;

    @Lob
    @Column(name = "FILE_DATA", length = 16777215)
    @Schema(description = "The image itself encoded in BASE64.")
    private byte[] data;

    @Column(name = "FILE_MEDIA_TYPE", length = 100)
    @Schema(description = "The mediatype of the encoded file.")
    private String mediaType;

    @JsonIgnore
    public OutputStream getDataStream() {
        return getOutputStream(data);
    }


    @Override
    public FileData clone() throws CloneNotSupportedException {
        FileData result = (FileData) super.clone();

        if (data != null) {
            result.data = Arrays.copyOf(data, data.length);
        }

        return result;
    }
}
