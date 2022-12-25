/*
 * Copyright (c) &today.year Kaiserpfalz EDV-Service, Roland T. Lichti
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

package de.paladinsinn.tp.dcis.files.services;

import de.kaiserpfalzedv.testsupport.oidc.JsonWebTokenFromConfig;
import de.paladinsinn.tp.dcis.services.FileService;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;

/**
 * FileServiceTest --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-01-01
 */
@QuarkusTest
@TestHTTPEndpoint(FileService.class)
@Slf4j
public class FileServiceTest {

    @Test
    @Disabled
    @TestSecurity(user = "player", roles = {"player"})
    public void shouldReturnFullListWhenCalledWithoutParameters() {
        given()
                .auth().basic("player", "player")
        .when()
                .get("/api/v1/files")
                .prettyPeek()
        .then()
                .statusCode(200);
    }


    private void logTest(final String test, final Object... parameters) {
        MDC.put("test", test);

        log.debug("Starting. test='{}', parameters={}", test, parameters);
    }

    @BeforeAll
    static void setUpLogging() {
        MDC.put("test-class", FileServiceTest.class.getSimpleName());

        log.info("Starting test... test-class='{}'", MDC.get("test-class"));
    }

    @AfterAll
    static void tearDownLogging() {
        MDC.remove("test");
        log.info("Ended test. test-class='{}'", MDC.get("test-class"));
        MDC.remove("test-class");
    }
}
