package de.paladinsinn.tp.dcis.files.services;

import de.paladinsinn.tp.dcis.services.FileService;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import static io.restassured.RestAssured.when;

/**
 * FileServiceTest --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-01-01
 */
@QuarkusTest
@Slf4j
public class FileServiceTest {
    @Test
    public void shouldReturnFullListWhenCalledWithoutParameters() {
        when()
                .get("/api/v1/files")
                .prettyPeek()
        .then()
                .statusCode(200);
    }


    private void logTest(@NotNull final String test, final Object... parameters) {
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
