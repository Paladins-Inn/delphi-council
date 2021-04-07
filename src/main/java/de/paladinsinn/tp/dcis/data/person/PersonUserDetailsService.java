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

package de.paladinsinn.tp.dcis.data.person;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * PersonUserDetailsService --
 *
 * @author klenkes74 {literal <rlichti@kaiserpfalz-edv.de>}
 * @since 0.1.0  2021-03-29
 */
@Service
public class PersonUserDetailsService implements UserDetailsService {
    private static final Logger LOG = LoggerFactory.getLogger(PersonUserDetailsService.class);

    @Autowired
    private PersonRepository repository;

    @Override
    public UserDetails loadUserByUsername(final String userName) {
        Person result = repository.findByUsername(userName);
        if (result == null) {
            throw new UsernameNotFoundException(userName);
        }

        return result;
    }

    @Bean
    public UserDetails getUserDetails() {
        try {
            return (UserDetails) SecurityContextHolder.getContext()
                    .getAuthentication().getDetails();
        } catch (NullPointerException e) {
            LOG.warn("Can't retrieve user details: {}", e.getMessage());
            return null;
        }
    }
}
