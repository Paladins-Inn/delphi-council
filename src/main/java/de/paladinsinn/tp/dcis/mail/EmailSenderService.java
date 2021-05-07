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

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
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
@AllArgsConstructor
public class EmailSenderService {
    private static final Logger LOG = LoggerFactory.getLogger(EmailSenderService.class);

    private final JavaMailSender sender;
    private final SpringTemplateEngine templates;

    /**
     * @param message message to be sent.
     */
    @Async
    public void send(@NotNull SimpleMailMessage message) {
        sender.send(message);

        LOG.info("Send email. from='{}', to='{}', subject='{}'",
                message.getFrom(), message.getTo(), message.getSubject());
    }

    private void sendHtml(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        sender.send(message);
    }

    @Async
    public void send(
            @NotNull String to,
            @NotNull String subject,
            @NotNull String templateName,
            @NotNull Map<String, Object> params,
            @NotNull Locale locale
    ) throws MessagingException {
        final MimeMessage mimeMessage = sender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, MailConfig.TEMPLATE_ENCODING);

        Context context = new Context(locale, params);

        message.setTo(to);
        message.setSubject(subject);

        message.setText(
                templates.process(templateName + ".txt", context),
                templates.process(templateName + ".html", context)
        );

        sender.send(mimeMessage);
    }
}
