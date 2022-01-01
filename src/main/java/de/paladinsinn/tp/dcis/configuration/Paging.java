package de.paladinsinn.tp.dcis.configuration;

import com.google.common.base.Strings;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import javax.enterprise.context.ApplicationScoped;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Paging -- The automated paging
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-01-01
 */
@Slf4j
@ApplicationScoped
public class Paging {
    public PageRequest calculatePage(
            final int limit, final int offset,
            final String direction,
            final String[] columns
    ) {
        if (Strings.isNullOrEmpty(direction)) {
            return PageRequest.of(limit, offset);
        }

        Sort.Direction dir = Sort.Direction.fromString(direction);

        return PageRequest.of(limit, offset, Sort.by(dir, columns));
    }
}
