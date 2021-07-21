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

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;


/**
 * CucumberTests -- Cucumber test runner.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.3.0  2021-04-24
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        monochrome = true,
        stepNotifications = true,
        features = {
                "target/test-classes/features",
                "classpath:de.paladinsinn.tp.dcis.cucumber"
        },
        plugin = {
                "usage",
                "html:target/site/cucumber.html",
                "json:target/cucumber/cucumber.json",
                "junit:target/cucumber/cucumber.xml"
        }
)
public class CucumberTests {
}
