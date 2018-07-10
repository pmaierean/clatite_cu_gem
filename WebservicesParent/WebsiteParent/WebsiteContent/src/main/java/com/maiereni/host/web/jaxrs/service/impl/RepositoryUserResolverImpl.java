/**
 * ================================================================
 *  Copyright (c) 2017-2018 Maiereni Software and Consulting Inc
 * ================================================================
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
package com.maiereni.host.web.jaxrs.service.impl;

import javax.annotation.Nonnull;
import javax.jcr.Credentials;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.core.RepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maiereni.host.web.jaxrs.service.RepositoryUserResolver;
import com.maiereni.host.web.util.ConfigurationProvider;

/**
 * An implementation of the Repository User resolver that loads from an encrypted 
 * @author Petre Maierean
 *
 */
public class RepositoryUserResolverImpl implements RepositoryUserResolver {
	private static final Logger logger = LoggerFactory.getLogger(RepositoryUserResolverImpl.class);
	public static final String ADMIN_USER = "admin";
	public static final String ADMIN_USER_PASSWORD = "adminPassword";
	private static final String DEFAULT_ADMIN_USER_PASSWORD = "admin";
	private RepositoryImpl repository;
	private ConfigurationProvider configurationProvider;
	private String adminPassword;
	private String adminPasswordFilePath;
	
	RepositoryUserResolverImpl(
		@Nonnull final RepositoryImpl repository,
		@Nonnull final ConfigurationProvider configurationProvider) throws Exception {
		this.repository = repository;
		this.configurationProvider = configurationProvider;
		this.adminPassword = loadAdminPassword(adminPasswordFilePath);
	}
	
	/**
	 * Creates repository credentials for a repository user
	 * @param repoUser the repository user
	 * @return the credentials
	 * @throws if the repository user cannot be found
	 */
	@Override
	public Credentials getCredentials(@Nonnull final String repoUser) 
		throws Exception {
		Credentials ret = null;
		Session session = null;
		try {
			session = repository.login(new SimpleCredentials(ADMIN_USER, adminPassword.toCharArray()));
	        UserManager userManager = ((JackrabbitSession) session).getUserManager();
            Authorizable authorizable = userManager.getAuthorizable(repoUser);
            if (authorizable != null)
            	ret = ((User) authorizable).getCredentials();
		}
		finally {
			if (session != null) {
				try {
					session.logout();
				}
				catch(Exception e) {
					
				}
			}
		}
		return ret;
	}
	
	/**
	 * Add user
	 * @param repoUser the user name
	 * @param password the password
	 */
	@Override	
	public void addUser(@Nonnull final String repoUser, @Nonnull final String password) 
		throws Exception {
		Session session = null;
		try {
			session = repository.login(new SimpleCredentials(ADMIN_USER, adminPassword.toCharArray()));
	        UserManager userManager = ((JackrabbitSession) session).getUserManager();
            userManager.createUser(repoUser, password);
            session.save();
		}
		finally {
			if (session != null) {
				try {
					session.logout();
				}
				catch(Exception e) {
					
				}
			}
		}		
	}
	
	/**
	 * Change the user password
	 * 
	 * @param repoUser
	 * @param repoPassword
	 * @throws Exception
	 */
	public void setPassword(@Nonnull final String repoUser, @Nonnull final String repoPassword) 
		throws Exception {
		Session session = null;
		try {
            session = repository.login(new SimpleCredentials(ADMIN_USER, adminPassword.toCharArray()));
	        UserManager userManager = ((JackrabbitSession) session).getUserManager();
            Authorizable authorizable = userManager.getAuthorizable(repoUser);

            ((User) authorizable).changePassword(repoPassword);
            if (repoUser.equals(ADMIN_USER)) {
            	this.adminPassword = repoPassword; 
            	this.configurationProvider.setProperty(ADMIN_USER_PASSWORD, repoPassword);
            	logger.info("Admin password has been saved into the configuration file");
            }
            logger.debug("The user password has been changed");
            session.save();
		}
		finally {
			if (session != null) {
				try {
					session.logout();
				}
				catch(Exception e) {
					
				}
			}
		}
	}
	
	public void setAdminPassword(@Nonnull final String adminPassword) throws Exception {
		configurationProvider.setProperty(ADMIN_USER_PASSWORD, adminPassword);
	}
	
	private String loadAdminPassword(final String configFile) throws Exception {
		return configurationProvider.getProperty(ADMIN_USER_PASSWORD,DEFAULT_ADMIN_USER_PASSWORD);
	}
}
