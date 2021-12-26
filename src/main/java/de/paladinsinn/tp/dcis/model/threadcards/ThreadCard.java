package de.paladinsinn.tp.dcis.model.threadcards;

import de.paladinsinn.tp.dcis.model.HasAvatar;
import de.paladinsinn.tp.dcis.model.HasId;
import de.paladinsinn.tp.dcis.model.HasName;
import de.paladinsinn.tp.dcis.model.person.AvatarInformation;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.OutputStream;
import java.util.UUID;

/**
 * @author rlichti
 * @version 1.0.0 2021-12-25
 * @since 1.0.0 2021-12-25
 */
@RegisterForReflection
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true, setterPrefix = "with")
@Getter
@ToString(onlyExplicitlyIncluded = true)
public class ThreadCard implements HasId, HasName, HasAvatar, Cloneable {
    @ToString.Include
    @Builder.Default
    @NotNull
    private UUID id = UUID.randomUUID();

    @ToString.Include
    @NotEmpty
    @NotNull
    @NotBlank
    @Size(min = 2, max = 20)
    private String name;

    @NotNull
    private AvatarInformation image;

    @Min(0)
    private int intimidation, maneuver, taunt, trick,
                melee, dodge, unarmedCombat,
                toughness, armor;

    @Builder.Default
    @NotNull
    private SizeModifier size = SizeModifier.Normal;

    @Override
    public String getAvatarImage() {
        if (image == null) return "";
        return image.getAvatarImage();
    }

    @Override
    public OutputStream getAvatar() {
        if (image == null) return getOutputStream(null);

        return image.getAvatar();
    }

    @Override
    public ThreadCard clone() throws CloneNotSupportedException {
        ThreadCard result = (ThreadCard) super.clone();

        result.id = id;
        result.name = name;
        result.image = image;
        result.size = size;

        result.intimidation = intimidation;
        result.maneuver = maneuver;
        result.taunt = taunt;
        result.trick = trick;

        result.melee = melee;
        result.dodge = dodge;
        result.unarmedCombat = unarmedCombat;

        result.toughness = toughness;
        result.armor = armor;

        return result;
    }
}
