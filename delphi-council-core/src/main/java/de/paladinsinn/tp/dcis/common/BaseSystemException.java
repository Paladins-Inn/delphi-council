/*
 * Copyright (c) 2021-2023. Roland T. Lichti
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.paladinsinn.tp.dcis.common;

/**
 * @author rlichti
 * @version 1.0.0 2021-09-08
 * @since 1.0.0 2021-09-08
 */
public class BaseSystemException extends de.kaiserpfalzedv.commons.core.api.BaseSystemException {

    public BaseSystemException(String message) {
        super(message);
    }

    public BaseSystemException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseSystemException(Throwable cause) {
        super(cause);
    }

    public BaseSystemException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
