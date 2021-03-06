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

package de.paladinsinn.tp.dcis.mail;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import de.paladinsinn.tp.dcis.data.person.AccountSecurityStatus;
import de.paladinsinn.tp.dcis.data.person.Person;
import de.paladinsinn.tp.dcis.ui.i18n.Translator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * MailConfigTest --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.1.0  2021-05-07
 */
@SpringBootTest
@ActiveProfiles({"test"})
@Slf4j
public class MailSenderTest {

    private static final String EMAIL_ADDRESS = "test@delpi-council.org";
    private static final String EMAIL_SUBJECT = "mail.password-reset.subject";

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(
                    GreenMailConfiguration
                            .aConfig()
                            .withUser("duke", "springboot")
            )
            .withPerMethodLifecycle(false);

    /**
     * Service under test.
     */
    private final EmailSenderService sut;

    private final Translator i18n;


    @Autowired
    public MailSenderTest(
            @NotNull final EmailSenderService sut,
            @NotNull final Translator i18n
    ) {
        this.sut = sut;
        this.i18n = i18n;
    }


    @Test
    public void shouldSendPasswortCheckEmail() throws MessagingException {
        // given
        UUID token = UUID.randomUUID();

        HashMap<String, Object> params = new HashMap<>(2);
        params.put("token", token);
        params.put("person", Person.builder()
                .username("qs")
                .firstname("Quin")
                .lastname("Sebastian")
                .email(EMAIL_ADDRESS)
                .status(AccountSecurityStatus.builder().build())
                .build());

        ArrayList<Object> subjectParams = new ArrayList<>(1);
        subjectParams.add("qs");

        // when
        sut.send(EMAIL_ADDRESS, EMAIL_SUBJECT, subjectParams, "password-reset", params, Locale.GERMANY);

        // then
        MimeMessage receivedMessage = greenMail.getReceivedMessages()[0];

        log.trace("from={}, to={}, subjet='{}', body:\n-----8<-----8<-----8<-----\n{}\n-----8<-----8<-----8<-----",
                receivedMessage.getSender(),
                receivedMessage.getAllRecipients(),
                receivedMessage.getSubject(),
                GreenMailUtil.getBody(receivedMessage));

        assertEquals("Quin Sebastian <quin.sebastian@delphi-council.org>", receivedMessage.getSender().toString());
        assertEquals(1, receivedMessage.getAllRecipients().length);
        assertEquals(EMAIL_ADDRESS, receivedMessage.getAllRecipients()[0].toString());
        assertEquals(i18n.getTranslation(EMAIL_SUBJECT, Locale.GERMAN, "qs"), receivedMessage.getSubject());

        // needs to remove all mime linebreaks because the token is often divided on two lines.
        assertThat(GreenMailUtil.getBody(receivedMessage).replaceAll("=[\r\n]+", ""), containsString(token.toString()));
    }
}
