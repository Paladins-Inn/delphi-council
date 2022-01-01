package de.paladinsinn.tp.dcis.model;

import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;

/**
 * About -- About the torganized play.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2021-12-31
 */
@Slf4j
public class About {
    public static final String NAMESPACE = "torganized-play";
    public static final OffsetDateTime DEFAULT_CREATION = OffsetDateTime.parse("2021-12-31T22:10:00+01:00");
}
