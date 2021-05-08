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

import com.vaadin.flow.component.ComponentEventListener;
import de.paladinsinn.tp.dcis.data.person.PasswordResetToken;
import de.paladinsinn.tp.dcis.data.person.PasswordResetTokenRepository;
import de.paladinsinn.tp.dcis.data.person.Person;
import de.paladinsinn.tp.dcis.ui.components.TorgNotification;
import de.paladinsinn.tp.dcis.ui.views.login.LoginView;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * ResetPasswordListener -- Resets the password for a given user with the provided new password.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.1.0  2021-05-07
 */
@Service
@AllArgsConstructor
@Slf4j
public class ResetPasswordListener implements ComponentEventListener<ResetPasswordEvent> {
    private final PasswordResetTokenRepository tokenRepository;

    @Transactional
    @Override
    public void onComponentEvent(@NotNull final ResetPasswordEvent event) {
        log.info("User want's to reset his password. event={}", event);

        Optional<Person> person = loadByToken(event.getToken());

        person.ifPresentOrElse(
                p -> {
                    p.setPassword(event.getPassword());
                    removeAllPersonalToken(p);

                    new TorgNotification(
                            "password-reset.changed-password.success",
                            null,
                            null,
                            Collections.singletonList(p.getUsername())
                    ).open();
                },
                () -> new TorgNotification(
                        "password-reset.invalid-token",
                        null,
                        null,
                        Collections.emptyList()
                ).open()
        );

        event.getSource().getUI().ifPresent(ui -> ui.navigate(LoginView.ROUTE));
    }


    private Optional<Person> loadByToken(@NotNull final UUID tokenId) {
        Optional<PasswordResetToken> token = tokenRepository.findById(tokenId);

        Optional<Person> result = Optional.empty();
        return token.map(PasswordResetToken::getPerson).or(() -> result);
    }

    private void removeAllPersonalToken(@NotNull final Person person) {
        List<PasswordResetToken> tokens = tokenRepository.findAllByPerson(person);

        tokens.forEach(tokenRepository::delete);
    }
}
