package de.paladinsinn.tp.dcis;

import de.kaiserpfalzedv.commons.core.api.BaseSystemException;

/**
 * A wrapper around a checked exception.
 *
 * @author rlichti
 * @version 2.0.0 2021-09-08
 * @since 2.0.0 2021-09-08
 */
public class WrappedException extends BaseSystemException {
    public WrappedException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrappedException(Throwable cause) {
        super(cause);
    }

    public WrappedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
