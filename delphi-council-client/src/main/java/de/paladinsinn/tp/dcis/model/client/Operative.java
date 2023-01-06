/*
 * Copyright (c) 2022-2023. Roland T. Lichti
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.paladinsinn.tp.dcis.model.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.kaiserpfalzedv.commons.core.files.FileData;
import de.kaiserpfalzedv.rpg.torg.model.actors.Clearance;
import de.kaiserpfalzedv.rpg.torg.model.core.Cosm;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;

/**
 * Operative -- The Storm Knight.
 *
 * @author klenkes {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2023-01-06
 */
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class Operative extends PersistedImpl implements de.paladinsinn.tp.dcis.model.Operative {
    @ToString.Include
    private String code;

    private OffsetDateTime deleted;


    @ToString.Include
    private String name;

    private String lastName;

    private String firstName;

    private FileData avatar;

    private FileData token;

    private Cosm cosm;

    @ToString.Include
    private Clearance clearance;

    private int xp;

    private int money;

    @ToString.Include
    private String player;



    @SneakyThrows
    public static Operative copyData(de.paladinsinn.tp.dcis.model.Operative orig)  {
        if (Operative.class.isAssignableFrom(orig.getClass())) {
            return (Operative) orig;
        }

        return Operative.builder()
                .id(orig.getId())
                .version(orig.getVersion())
                .created(orig.getCreated())
                .modified(orig.getModified())
                .deleted(orig.getDeleted())

                .code(orig.getCode())
                .name(orig.getName())
                .lastName(orig.getLastName())
                .firstName(orig.getFirstName())
                .cosm(orig.getCosm())
                .xp(orig.getXp())

                .money(orig.getMoney())
                .avatar(orig.getAvatar().clone())
                .token(orig.getToken().clone())
                .build();
    }


    @SneakyThrows
    @Override
    public Operative clone() {
        return toBuilder().build();
    }
}
