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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import javax.validation.constraints.NotNull;
import java.util.*;

import static org.thymeleaf.templatemode.TemplateMode.HTML;
import static org.thymeleaf.templatemode.TemplateMode.TEXT;

/**
 * MailConfig --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.1.0  2021-05-07
 */
@Configuration
@Slf4j
@Getter
@ToString
@EqualsAndHashCode
public class MailConfig {
    public static final String TEMPLATE_ENCODING = "UTF-8";

    private final JavaMailSender sender;

    public MailConfig(
            @NotNull final JavaMailSender sender
    ) {
        this.sender = sender;
    }

    /**
     * @param templateResolver List of available template resolvers.
     * @return the template engine.
     */
    @Bean
    public SpringTemplateEngine thymeleafTemplateEngine(Collection<ITemplateResolver> templateResolver, MessageSource messageSource) {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();

        String message = messageSource.getMessage("mail.password-reset.subject", null, Locale.GERMANY);
        log.debug("Found message for subject: {}", message);

        templateEngine.setMessageSource(messageSource);
        templateResolver.forEach(templateEngine::setTemplateResolver);

        return templateEngine;
    }


    @Bean
    public Set<ITemplateResolver> templateResolver() {
        HashSet<ITemplateResolver> result = new HashSet<>(2);

        result.add(createTemplateResolver(1, HTML));
        result.add(createTemplateResolver(2, TEXT));

        return result;
    }

    private ITemplateResolver createTemplateResolver(int order, TemplateMode mode) {
        final ClassLoaderTemplateResolver result = new ClassLoaderTemplateResolver();

        result.setOrder(order);
        result.setResolvablePatterns(Collections.singleton(mode == HTML ? "*.html" : "*.txt"));
        result.setTemplateMode(mode);
        result.setCharacterEncoding(TEMPLATE_ENCODING);
        result.setCacheable(false);

        return result;
    }

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding(TEMPLATE_ENCODING);

        log.debug("Resource bundle: {}", messageSource.getBasenameSet());

        return messageSource;
    }
}
