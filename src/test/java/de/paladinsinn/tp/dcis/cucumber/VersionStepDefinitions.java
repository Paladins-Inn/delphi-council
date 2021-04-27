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

package de.paladinsinn.tp.dcis.cucumber;

import io.cucumber.java8.En;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * VersionStepDefinitions -- The steps for accessing the version endpoint of the software.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.3.0  2021-04-24
 */
public class VersionStepDefinitions implements En {
    private final MockMvc mock;

    private ResultActions callUrl;

    @Autowired
    public VersionStepDefinitions(final MockMvc mock) {
        this.mock = mock;

        Given("^the version service is running$", () -> {
            assertNotNull(mock);
        });

        When("^the client calls the url (.+)$", (String url) -> {
            callUrl = mock
                    .perform(get(url))
                    .andDo(print());
        });

        Then("^the version should be (.+)$", (String version) -> {
            callUrl
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString(version)));
        });
    }
}
