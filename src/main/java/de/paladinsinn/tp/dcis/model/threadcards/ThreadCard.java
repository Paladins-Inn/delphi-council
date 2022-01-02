package de.paladinsinn.tp.dcis.model.threadcards;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 2.0.0 2021-12-25
 * @since 2.0.0 2021-12-25
 */
@RegisterForReflection
@SuperBuilder(toBuilder = true, setterPrefix = "with")
@AllArgsConstructor
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@Getter
public class ThreadCard {
    @ToString.Include
    @NotEmpty
    @NotNull
    @NotBlank
    @Size(min = 2, max = 20)
    private String name;

    @Min(0)
    private int intimidation, maneuver, taunt, trick,
                melee, dodge, unarmedCombat,
                toughness, armor;

    @Builder.Default
    @NotNull
    private SizeModifier size = SizeModifier.Normal;

    @Override
    public ThreadCard clone() throws CloneNotSupportedException {
        ThreadCard result = (ThreadCard) super.clone();

        result.name = name;

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
