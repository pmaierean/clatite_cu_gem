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
package com.maiereni.oak;


import java.util.Map;

import javax.annotation.Nonnull;
import javax.jcr.SimpleCredentials;
import javax.naming.Context;
import javax.security.auth.login.Configuration;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.oak.InitialContent;
import org.apache.jackrabbit.oak.Oak;
import org.apache.jackrabbit.oak.api.ContentRepository;
import org.apache.jackrabbit.oak.jcr.Jcr;
import org.apache.jackrabbit.oak.namepath.NamePathMapper;
import org.apache.jackrabbit.oak.plugins.commit.ConflictValidatorProvider;
import org.apache.jackrabbit.oak.plugins.commit.JcrConflictHandler;
import org.apache.jackrabbit.oak.plugins.index.WhiteboardIndexEditorProvider;
import org.apache.jackrabbit.oak.plugins.index.property.PropertyIndexEditorProvider;
import org.apache.jackrabbit.oak.plugins.index.property.PropertyIndexProvider;
import org.apache.jackrabbit.oak.plugins.index.reference.ReferenceEditorProvider;
import org.apache.jackrabbit.oak.plugins.index.reference.ReferenceIndexProvider;
import org.apache.jackrabbit.oak.plugins.name.NamespaceEditorProvider;
import org.apache.jackrabbit.oak.plugins.nodetype.TypeEditorProvider;
import org.apache.jackrabbit.oak.plugins.tree.RootProvider;
import org.apache.jackrabbit.oak.plugins.tree.TreeProvider;
import org.apache.jackrabbit.oak.plugins.tree.impl.RootProviderService;
import org.apache.jackrabbit.oak.plugins.tree.impl.TreeProviderService;
import org.apache.jackrabbit.oak.plugins.version.VersionHook;
import org.apache.jackrabbit.oak.query.QueryEngineSettings;
import org.apache.jackrabbit.oak.security.internal.SecurityProviderBuilder;
import org.apache.jackrabbit.oak.spi.security.ConfigurationParameters;
import org.apache.jackrabbit.oak.spi.security.SecurityProvider;
import org.apache.jackrabbit.oak.spi.security.authentication.ConfigurationUtil;
import org.apache.jackrabbit.oak.spi.security.authorization.AuthorizationConfiguration;
import org.apache.jackrabbit.oak.spi.security.principal.PrincipalConfiguration;
import org.apache.jackrabbit.oak.spi.security.user.UserConfiguration;
import org.apache.jackrabbit.oak.spi.security.user.util.UserUtil;
import org.apache.jackrabbit.oak.spi.state.NodeStore;
import org.apache.jackrabbit.oak.spi.whiteboard.DefaultWhiteboard;
import org.apache.jackrabbit.oak.spi.whiteboard.Whiteboard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;

import com.maiereni.oak.bo.RepositoryProperties;

/**
 * A factory class for the repository builder
 * @author Petre Maierean
 *
 */
public abstract class OakBeanFactory {
	private static final Logger logger = LoggerFactory.getLogger(OakBeanFactory.class);

	/**
	 * Get the properties for the Oak repository
	 * @return
	 */
	public abstract @Nonnull RepositoryProperties getProperties();
	/**
	 * Get a node store
	 * @return
	 * @throws Exception
	 */
	public abstract NodeStore getNodeStore() throws Exception;
	
	@Bean(name="securityProvider")
	public SecurityProvider getSecurityProvider() throws Exception {
	    RootProvider rootProvider = new RootProviderService(); 
	    TreeProvider treeProvider = new TreeProviderService();
	    SecurityProviderBuilder securityProviderBuilder = SecurityProviderBuilder.newBuilder();
	    securityProviderBuilder.with(getConfigurationParameters());
	    securityProviderBuilder.withRootProvider(rootProvider);
	    securityProviderBuilder.withTreeProvider(treeProvider);
        return securityProviderBuilder.build();
	}
	
	@Bean(name="userConfiguration")
	public UserConfiguration getUserConfiguration(@Nonnull final SecurityProvider securityProvider) throws Exception {
		return getConfig(securityProvider, UserConfiguration.class);
	}
	
	@Bean(name="principalConfiguration")
	public PrincipalConfiguration getPrincipalManager(@Nonnull final SecurityProvider securityProvider) throws Exception {
		return getConfig(securityProvider, PrincipalConfiguration.class);
	}
	
	@Bean(name="authorizationConfiguration")
	public AuthorizationConfiguration getAuthorizationConfiguration(@Nonnull final SecurityProvider securityProvider) throws Exception {
		return getConfig(securityProvider, AuthorizationConfiguration.class);
	}
	
	@Bean(name="namePathMapper")
	public NamePathMapper getNamePathMapper() {
		return NamePathMapper.DEFAULT;
	}

	
	@Bean(name="oak")
	public Oak getOak(@Nonnull final SecurityProvider securityProvider) throws Exception {
		logger.debug("Create an Oak repository");
        Whiteboard wb = new DefaultWhiteboard();
        WhiteboardIndexEditorProvider wbProvider = new WhiteboardIndexEditorProvider();
        wbProvider.start(wb);
        QueryEngineSettings querySettings = new QueryEngineSettings();
        querySettings.setFailTraversal(true);
        Oak oak = new Oak(getNodeStore());
        initializeOak(oak);
        oak.with(querySettings);
        oak.with(securityProvider);
        oak.with(wb);
        return oak;
	}
	
	@Bean(name="repository")
	public ContentRepository getRepository(@Nonnull final Oak oak) throws Exception {
        return oak.createContentRepository();
	}
	
	@Bean(name="jcr")
	public Jcr getJcr(@Nonnull final Oak oak) throws Exception {
		return new Jcr(oak);
	}

	@Bean(name="superUser")
	public SimpleCredentials getSuperUser() throws Exception {
		RepositoryProperties properties = getProperties();
		if (StringUtils.isAnyBlank(properties.getAdminUser(), properties.getAdminPassword())) {
			throw new Exception("The admin user and password cannot be null");
		}
		return new SimpleCredentials(properties.getAdminUser(), properties.getAdminPassword().toCharArray());
	}

	@Bean(name="adminUser")
    public SimpleCredentials getAdminCredentials(@Nonnull final UserConfiguration userConfiguration) {
        String adminId = UserUtil.getAdminId(userConfiguration.getParameters());
        return new SimpleCredentials(adminId, adminId.toCharArray());
    }

    @Bean("securityConfiguration")
    public Configuration getConfiguration() {
        return ConfigurationUtil.getDefaultConfiguration(getSecurityConfigParameters());
    }
	
	protected void resolveProperty(final Map<String, String> props, final Context ctxt, final String key, final String defaultValue) {
		String value = System.getProperty(key, defaultValue);
		if (ctxt != null)
			try {
				Object o = ctxt.lookup(key);
				if (o != null)
					value = o.toString();
			}
			catch(Exception e) {
				logger.error("Could not resolve " + key + " from context");
			}
		if (StringUtils.isNotEmpty(value))
			props.put(key, value);
	}
	
	protected ConfigurationParameters getConfigurationParameters() {
		return ConfigurationParameters.EMPTY;
	}
	
	protected void initializeOak(final Oak oak) {
        oak.with(new InitialContent());
        oak.with(new VersionHook());
        oak.with(JcrConflictHandler.createJcrConflictHandler());
        oak.with(new NamespaceEditorProvider());
        oak.with(new ReferenceEditorProvider());
        oak.with(new ReferenceIndexProvider());
        oak.with(new PropertyIndexEditorProvider());
        oak.with(new PropertyIndexProvider());
        oak.with(new TypeEditorProvider());
        oak.with(new ConflictValidatorProvider());		
	}
	
    protected ConfigurationParameters getSecurityConfigParameters() {
        return ConfigurationParameters.EMPTY;
    }
	
    protected <T> T getConfig(final SecurityProvider securityProvider, Class<T> configClass) {
        return securityProvider.getConfiguration(configClass);
    }
}
