package de.paladinsinn.tp.dcis.model.threadcards;

import lombok.Getter;

/**
 * The vulnerability modifier for a threat.
 *
 * @author rlichti
 * @version 1.0.0 2021-12-25
 * @since 1.0.0 2021-12-25
 */
@Getter
public enum SizeModifier {
    Normal(0),
    VerySmall(-4),
    Small(-2),
    Large(+2),
    VeryLarge(+4);

    int modifier;

    private SizeModifier(int modifier) {
        this.modifier = modifier;
    }
}
