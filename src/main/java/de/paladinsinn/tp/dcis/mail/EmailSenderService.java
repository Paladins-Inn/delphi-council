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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.validation.constraints.NotNull;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * EmailSenderService -- Sends emails via {@link JavaMailSender} injected into this service.
 *
 * @author Kamer Elciar (<a href="https://kamer-dev.medium.com/spring-boot-user-registration-and-login-43a33ea19745">https://kamer-dev.medium.com/spring-boot-user-registration-and-login-43a33ea19745</a>)
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-04-10
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailSenderService {
    private final JavaMailSender sender;
    private final SpringTemplateEngine templates;
    private final MessageSource messages;

    @Value("${mail.from.user:Quin Sebastian}")
    private String senderName;
    @Value("${mail.from.address:quin.sebastian@delphi-council.org}")
    private String senderAddress;

    /**
     * @param message message to be sent.
     */
    @Async
    public void send(@NotNull SimpleMailMessage message) {
        sender.send(message);

        log.info("Send email. from='{}', to='{}', subject='{}'",
                message.getFrom(), message.getTo(), message.getSubject());
    }

    @Async
    public void send(
            @NotNull String to,
            @NotNull String subjectKey,
            @NotNull List<Object> subjectParams,
            @NotNull String templateName,
            @NotNull Map<String, Object> params,
            @NotNull Locale locale
    ) throws MessagingException {
        final MimeMessage mimeMessage = sender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, MailConfig.TEMPLATE_ENCODING);

        Context context = new Context(locale, params);
        String subject;
        try {
            subject = messages.getMessage(subjectKey, subjectParams.toArray(new Object[0]), locale);
        } catch (NoSuchMessageException e) {
            log.warn("subject key could not be found: key={}, params={}", subjectKey, subjectParams);

            subject = "!" + subjectKey;
        }

        InternetAddress from = new InternetAddress();
        from.setAddress(senderAddress);
        try {
            from.setPersonal(senderName, MailConfig.TEMPLATE_ENCODING);
        } catch (UnsupportedEncodingException e) {
            log.warn("Can't set correct personal name!", e);
        }

        log.debug("Message sender. from={}, to={}, subject={}", from, to, subject);

        mimeMessage.setSender(from);
        message.setTo(to);
        message.setSubject(subject);

        message.setText(
                templates.process(templateName + ".txt", context),
                templates.process(templateName + ".html", context)
        );


        sender.send(mimeMessage);

        log.info("Message sent. from='{}', to='{}', subject='{}'",
                mimeMessage.getSender(), mimeMessage.getAllRecipients(), mimeMessage.getSubject());
    }
}
