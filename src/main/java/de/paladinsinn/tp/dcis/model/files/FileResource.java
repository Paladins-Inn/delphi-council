package de.paladinsinn.tp.dcis.model.files;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.kaiserpfalzedv.commons.core.resources.Resource;

import de.paladinsinn.tp.dcis.model.About;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * FileResource -- An image or any other file saved for the system..
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2021-12-31
 */
@SuperBuilder(setterPrefix = "with", toBuilder = true)
@AllArgsConstructor
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@JsonDeserialize(builder = FileResource.FileResourceBuilder.class)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@Schema(description = "A file saved in the system.")
public class FileResource extends Resource<FileData> {
    public static final String KIND = "File";
    public static final String VERSION = About.NAMESPACE + "/v1";
    public static final String NAMESPACE = About.NAMESPACE;
}
