/*
 * Copyright (c) 2022 Kaiserpfalz EDV-Service, Roland T. Lichti
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

package de.paladinsinn.tp.dcis;

import de.paladinsinn.torganized.core.threadcards.SizeModifier;
import de.paladinsinn.torganized.core.threadcards.ThreadCard;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
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
                .name("Testthreat")

                .intimidation(10)
                .maneuver(10)
                .taunt(10)
                .trick(10)

                .melee(10)
                .unarmedCombat(10)
                .dodge(10)
                .toughness(10).armor(0)

                .size(SizeModifier.Large)
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
