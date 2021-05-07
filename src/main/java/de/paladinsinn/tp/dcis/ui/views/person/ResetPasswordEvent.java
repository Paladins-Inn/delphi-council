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

package de.paladinsinn.tp.dcis.ui.views.person;

import com.sun.istack.NotNull;
import com.vaadin.flow.component.ComponentEvent;
import de.paladinsinn.tp.dcis.ui.views.login.PasswordResetView;
import lombok.Getter;

import java.util.UUID;

/**
 * ResetPasswordEvent --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.1.0  2021-05-07
 */
@Getter
public class ResetPasswordEvent extends ComponentEvent<PasswordResetView> {
    /**
     * The token to confirm.
     */
    private final UUID token;

    private final String password;

    /**
     * Creates a new event using the given source and indicator whether the
     * event originated from the client side or the server side.
     *
     * @param source   the source component
     * @param token    The password reset token
     * @param password the new password to save
     */
    public ResetPasswordEvent(
            @NotNull final PasswordResetView source,
            @NotNull final UUID token,
            @NotNull final String password
    ) {
        super(source, false);

        this.token = token;
        this.password = password;
    }
}
