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
import javax.jcr.SimpleCredentials;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.oak.InitialContent;
import org.apache.jackrabbit.oak.Oak;
import org.apache.jackrabbit.oak.api.ContentRepository;
import org.apache.jackrabbit.oak.jcr.Jcr;
import org.apache.jackrabbit.oak.namepath.NamePathMapper;
import org.apache.jackrabbit.oak.plugins.commit.ConflictValidatorProvider;
import org.apache.jackrabbit.oak.plugins.commit.JcrConflictHandler;
import org.apache.jackrabbit.oak.plugins.document.rdb.RDBDocumentNodeStoreBuilder;
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
import org.apache.jackrabbit.oak.spi.blob.BlobStore;
import org.apache.jackrabbit.oak.spi.blob.FileBlobStore;
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
import org.springframework.context.annotation.Configuration;

import com.maiereni.host.web.jaxrs.service.bo.RepositoryProperties;
import com.maiereni.host.web.util.BaseBeanFactory;

/**
 * The factory class for the Services
 * @author Petre Maierean
 *
 */
@Configuration
public class OakBeanFactory extends BaseBeanFactory {
	public static final String OAK_DATASOURCE = "java:comp/env/jdbc/oak_datasource";
	public static final String OAK_CACHE_SIZE = "java:comp/env/jdbc/oak_cacheSize";
	public static final String OAK_CLUSTER_ID = "java:comp/env/jdbc/oak_clusterId";
	public static final String OAK_BLOB_STORE = "java:comp/env/jdbc/oak_blobStore";
	public static final String OAK_BLOB_FS = "java:comp/env/jdbc/oak_blobPath";
	private static final Logger logger = LoggerFactory.getLogger(OakBeanFactory.class);

	public OakBeanFactory() throws Exception {
		super();
	}

	@Bean(name="repositoryProperties")
	public RepositoryProperties getRepositoryProperties() throws Exception {
		RepositoryProperties ret = new RepositoryProperties();
		String blobPath = getProperty(OAK_BLOB_FS, null);
		if (StringUtils.isNotBlank(blobPath)) {
			FileBlobStore blobStore = new FileBlobStore(blobPath);
			ret.put(OAK_BLOB_STORE, blobStore);
		}
		try {
			String cacheSize = getProperty(OAK_CACHE_SIZE, "1");
			ret.put(OAK_CACHE_SIZE, Long.parseLong(cacheSize));
		}
		catch(Exception e) {
			ret.put(OAK_CACHE_SIZE, new Long(1));			
		}
		return ret;
	}

	/**
	 * Get a node store
	 * @return
	 * @throws Exception
	 */
	@Bean(name="nodeStore")
	public NodeStore getNodeStore(final RepositoryProperties properties) throws Exception {
		logger.debug("Create the Document node store");
		DataSource datasource = getContextProperty(OAK_DATASOURCE, DataSource.class);
		if (datasource == null) {
			datasource = this.applicationContext.getBean(DataSource.class);
		}
		if (datasource == null) {
			throw new Exception("No datasource found");
		}
		RDBDocumentNodeStoreBuilder builder = new RDBDocumentNodeStoreBuilder();
		builder.setRDBConnection(datasource);
		long cacheSize = properties.get(OAK_CACHE_SIZE, 1L, Long.class);
		builder.memoryCacheSize(cacheSize);
		int clusterId = properties.get(OAK_CLUSTER_ID, 1, Integer.class);
        builder.setClusterId(clusterId);
        BlobStore blobStore = properties.get(OAK_BLOB_STORE, null, BlobStore.class);
        if (blobStore != null) {
        	builder.setBlobStore(blobStore);
        }
        builder.setLogging(false);
        
		return builder.build();
	}
	
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
	public Oak getOak(@Nonnull final SecurityProvider securityProvider, @Nonnull final NodeStore nodeStore) throws Exception {
		logger.debug("Create an Oak repository");
        Whiteboard wb = new DefaultWhiteboard();
        WhiteboardIndexEditorProvider wbProvider = new WhiteboardIndexEditorProvider();
        wbProvider.start(wb);
        QueryEngineSettings querySettings = new QueryEngineSettings();
        querySettings.setFailTraversal(true);
        Oak oak = new Oak(nodeStore);
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

	@Bean(name="adminUser")
    public SimpleCredentials getAdminCredentials(@Nonnull final UserConfiguration userConfiguration) {
        String adminId = UserUtil.getAdminId(userConfiguration.getParameters());
        return new SimpleCredentials(adminId, adminId.toCharArray());
    }

    @Bean("securityConfiguration")
    public javax.security.auth.login.Configuration getConfiguration() {
        return ConfigurationUtil.getDefaultConfiguration(getSecurityConfigParameters());
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
