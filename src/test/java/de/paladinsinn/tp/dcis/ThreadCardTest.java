package de.paladinsinn.tp.dcis;

import de.paladinsinn.tp.dcis.model.person.AvatarInformation;
import de.paladinsinn.tp.dcis.model.threadcards.SizeModifier;
import de.paladinsinn.tp.dcis.model.threadcards.ThreadCard;
import lombok.extern.slf4j.Slf4j;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

/**
 * @author rlichti
 * @version 1.0.0 2021-12-25
 * @since 1.0.0 2021-12-25
 */
@Slf4j
public class ThreadCardTest {
    private ThreadCard sut;


    @Test
    public void shouldReturnCorrectNameIfInitialized() {
        Assertions.assertEquals("Testthreat", sut.getName(), "Wrong name");
    }

    @BeforeEach
    void setUpTestClass() {
        sut = ThreadCard.builder()
                .withName("Testthreat")
                .withImage(
                        AvatarInformation.builder()
                                .withGravatar(true)
                                .build()
                )

                .withIntimidation(10)
                .withManeuver(10)
                .withTaunt(10)
                .withTrick(10)

                .withMelee(10)
                .withUnarmedCombat(10)
                .withDodge(10)
                .withToughness(10).withArmor(0)

                .withSize(SizeModifier.Large)
                .build();
    }

    @BeforeAll
    static void setUpLogger() {
        MDC.put("test-class", ThreadCardTest.class.getSimpleName());
        log.info("Start testing ...");
    }

    @AfterAll
    static void removeLogger() {
        log.info("Testing finished.");
        MDC.remove("test");
        MDC.remove("test-class");
    }
}
