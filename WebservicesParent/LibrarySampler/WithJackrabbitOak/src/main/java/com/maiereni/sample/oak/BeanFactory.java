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
package com.maiereni.sample.oak;


import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.jcr.SimpleCredentials;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.oak.InitialContent;
import org.apache.jackrabbit.oak.Oak;
import org.apache.jackrabbit.oak.api.ContentRepository;
import org.apache.jackrabbit.oak.jcr.Jcr;
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
import org.apache.jackrabbit.oak.segment.SegmentNodeStore;
import org.apache.jackrabbit.oak.segment.SegmentNodeStoreBuilders;
import org.apache.jackrabbit.oak.segment.file.FileStore;
import org.apache.jackrabbit.oak.segment.file.FileStoreBuilder;
import org.apache.jackrabbit.oak.spi.security.ConfigurationParameters;
import org.apache.jackrabbit.oak.spi.security.SecurityProvider;
import org.apache.jackrabbit.oak.spi.security.user.UserConfiguration;
import org.apache.jackrabbit.oak.spi.security.user.util.UserUtil;
import org.apache.jackrabbit.oak.spi.whiteboard.DefaultWhiteboard;
import org.apache.jackrabbit.oak.spi.whiteboard.Whiteboard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * A factory class for the repository builder
 * @author Petre Maierean
 *
 */
@Configuration
public class BeanFactory {
	private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);
	/**
	 * The root to the repository
	 */
	public static final String ROOT_REPOSITORY_PATH = "com.maiereni.sample.oak.repositoryRoot";
	public static final String REPORITORY_SUPER_USER = "com.maiereni.sample.oak.repository.su";
	public static final String REPORITORY_SUPER_USER_PASSWORD = "com.maiereni.sample.oak.repository.supwd";
	
	@Bean(name="properties")
	public Map<String, String> getProperties() throws Exception {
		Map<String, String> ret = new HashMap<String, String>();
		Context ctxt = null;
		try {
			ctxt = new InitialContext();
		}
		catch(Exception e) {
		}
		resolveProperty(ret, ctxt, ROOT_REPOSITORY_PATH, "/opt/local/sample/repo");
		resolveProperty(ret, ctxt, REPORITORY_SUPER_USER, "admin");
		resolveProperty(ret, ctxt, REPORITORY_SUPER_USER_PASSWORD, "");
		
		return ret;
	}
	
	@Bean (name="repositoryDir")
	public File getRepositoryDirectory(@Nonnull final Map<String, String> properties) {
		String repositoryRootDirectory = properties.get(ROOT_REPOSITORY_PATH);
		try {
			Context ctxt = new InitialContext();
			Object o = ctxt.lookup(ROOT_REPOSITORY_PATH);
			if (o != null)
				repositoryRootDirectory = o.toString();
		}
		catch(Exception e) {
		}
		logger.debug("The repository will be set at " + repositoryRootDirectory);
		return new File(repositoryRootDirectory);
	}
	
	@Bean(name="segmentStore")
	public FileStore getSegmentNodeStore(@Nonnull final File repositoryDir) throws Exception {
        return FileStoreBuilder.fileStoreBuilder(repositoryDir).withMaxFileSize(1).build();	
	}
	
	@Bean(name="securityProvider")
	public SecurityProvider getSecurityProvider() throws Exception {
	    RootProvider rootProvider = new RootProviderService(); 
	    TreeProvider treeProvider = new TreeProviderService();		
        SecurityProvider securityProvider = SecurityProviderBuilder.newBuilder().with(ConfigurationParameters.EMPTY)
        		.withRootProvider(rootProvider)
        		.withTreeProvider(treeProvider)
        		.build();
        return securityProvider;
	}
	
	@Bean(name="oak")
	public Oak getOak(@Nonnull final FileStore segmentStore, @Nonnull final SecurityProvider securityProvider) throws Exception {
		logger.debug("Create an Oak repository");
        Whiteboard wb = new DefaultWhiteboard();
        WhiteboardIndexEditorProvider wbProvider = new WhiteboardIndexEditorProvider();
        wbProvider.start(wb);
        SegmentNodeStore segmentNodeStore = SegmentNodeStoreBuilders.builder(segmentStore).build();	
        QueryEngineSettings querySettings = new QueryEngineSettings();
        querySettings.setFailTraversal(true);
        
        Oak oak = new Oak(segmentNodeStore)
                .with(new InitialContent())
                .with(new VersionHook())
                .with(JcrConflictHandler.createJcrConflictHandler())
                .with(new NamespaceEditorProvider())
                .with(new ReferenceEditorProvider())
                .with(new ReferenceIndexProvider())
                .with(new PropertyIndexEditorProvider())
                .with(new PropertyIndexProvider())
                .with(new TypeEditorProvider())
                .with(new ConflictValidatorProvider())
                .with(querySettings)
                .with(securityProvider);
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
	public SimpleCredentials getSuperUser(@Nonnull final SecurityProvider securityProvider) throws Exception {
		UserConfiguration userConfiguration = securityProvider.getConfiguration(UserConfiguration.class);
		String adminId = UserUtil.getAdminId(userConfiguration.getParameters());
		return new SimpleCredentials(adminId, adminId.toCharArray());
	}

	private void resolveProperty(final Map<String, String> props, final Context ctxt, final String key, final String defaultValue) {
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
	

}
