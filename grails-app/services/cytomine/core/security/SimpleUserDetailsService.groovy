package cytomine.core.security

import cytomine.core.social.PersistentProjectConnection

/*
* Copyright (c) 2009-2019. Authors: see NOTICE file.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import grails.plugin.springsecurity.userdetails.GormUserDetailsService
import grails.plugin.springsecurity.userdetails.GrailsUser
import grails.transaction.Transactional
import org.grails.orm.hibernate.HibernateDatastore
import org.springframework.dao.DataAccessException
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException

class SimpleUserDetailsService extends GormUserDetailsService {
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username, boolean loadRoles) throws UsernameNotFoundException, DataAccessException {
        SecUser user
        //        TODO: (Migration)
            println getClass().toString() + '001' + username
            user = SecUser.findByUsernameIlike(username)
            println getClass().toString() + '002'
            def authorities = []
            println getClass().toString() + '003'
            def auth = SecUserSecRole.findAllBySecUser(user).collect { new SimpleGrantedAuthority(it.secRole.authority) }
            println getClass().toString() + '004' + auth

        //by default, we remove the role_admin for the current session
            authorities.addAll(auth.findAll { it.authority != "ROLE_ADMIN" })
            println getClass().toString() + '004' + user.username
            println getClass().toString() + '004' + user.password
            println getClass().toString() + '004' + user.enabled
            println getClass().toString() + '004' + user.publicKey
            println getClass().toString() + '004' + authorities


            return new GrailsUser(user.username, user.password, user.enabled, !user.accountExpired,
                    !user.passwordExpired, !user.accountLocked,
                    authorities, user.id)

    }
}