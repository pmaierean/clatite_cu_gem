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

import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.config.RepositoryConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.xml.sax.InputSource;

import com.maiereni.host.web.jaxrs.service.MenuService;
import com.maiereni.host.web.jaxrs.service.RepositoryService;
import com.maiereni.host.web.jaxrs.service.RepositoryUserResolver;
import com.maiereni.host.web.util.BaseBeanFactory;
import com.maiereni.host.web.util.ConfigurationProvider;

/**
 * Builds the services base on configuration
 * @author Petre Maierean
 *
 */
@Configuration
public class ServicesFactory extends BaseBeanFactory {
	private static final Logger logger = LoggerFactory.getLogger(ServicesFactory.class);
	public static final String REPOSITORY_HOME = "rep.home";
	public static final String WORKSPACE_HOME = "wsp.home";
	public static final String WORKSPACE_NAME = "wsp.name";
	
	public ServicesFactory() throws Exception {
		super();
	}
	
	/**
	 * Constructor for the menu service
	 * @return
	 */
	@Bean
	public MenuService getMenuService(final MessageSource messageSource) {
		return new StaticMenuServiceImpl(messageSource);
	}
	
	@Bean(name="repositoryProperties")
	public Properties getRepositoryProperties() throws Exception {
		Properties props = new Properties();
		String repoHome = getProperty(REPOSITORY_HOME, null);
		if (StringUtils.isEmpty(repoHome))
			throw new Exception("The repository home cannot be null. User the key '" + REPOSITORY_HOME + "' to set it up");
		props.setProperty(REPOSITORY_HOME, repoHome);
		props.setProperty(WORKSPACE_HOME, getProperty(WORKSPACE_HOME, repoHome));
		props.setProperty(WORKSPACE_NAME, getProperty(WORKSPACE_NAME, "default"));
		return props;
	}
	
	@Bean(name="repository")
	@Scope("singleton")
	public RepositoryImpl getRepository(final Properties repositoryProperties) throws Exception {
		logger.debug("Create Repository");
		RepositoryImpl ret = null;
		try(InputStream is = getClass().getResourceAsStream("/repository.xml")) {
			InputSource source = new InputSource(is);
			RepositoryConfig cfg = RepositoryConfig.create(source, repositoryProperties);
			ret = RepositoryImpl.create(cfg);
			logger.debug("The repository has been created");
		}
		return ret;
	}
	
	@Bean(name="repositoryUserResolver")
	public RepositoryUserResolver getUserResolver(final RepositoryImpl repository, final ConfigurationProvider configurationProvider)
		throws Exception {
		logger.debug("Create repository user resolver");
		return new RepositoryUserResolverImpl(repository, configurationProvider);
	}
	
	@Bean(name="repositoryService")
	public RepositoryService getRepositoryService(
		final RepositoryImpl repository, 
		final RepositoryUserResolver repoUserResolver) throws Exception {
		logger.debug("Create repository service");
		return new RepositoryServiceImpl(repository, repoUserResolver);
	}
	
}
