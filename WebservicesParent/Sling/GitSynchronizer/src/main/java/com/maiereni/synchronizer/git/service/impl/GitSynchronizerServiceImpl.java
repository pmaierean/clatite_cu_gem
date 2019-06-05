/**
 * ================================================================
 *  Copyright (c) 2017-2019 Maiereni Software and Consulting Inc
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
package com.maiereni.synchronizer.git.service.impl;

import static org.apache.sling.engine.EngineConstants.FILTER_SCOPE_REQUEST;
import static org.apache.sling.engine.EngineConstants.SLING_FILTER_SCOPE;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.apache.sling.jcr.contentloader.ContentImporter;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maiereni.synchronizer.git.service.GitDownloader;
import com.maiereni.synchronizer.git.service.GitSynchronizerService;
import com.maiereni.synchronizer.git.service.SlingImporter;
import com.maiereni.synchronizer.git.service.bo.GitProperties;
import com.maiereni.synchronizer.git.service.bo.GitResults;
import com.maiereni.synchronizer.git.service.bo.ImportProperties;
import com.maiereni.synchronizer.git.utils.GitDownloaderImpl;
import com.maiereni.synchronizer.jcr.utils.SlingImporterImpl;

/**
 * An implementation of the GitSyncronizerService
 * 
 * @author Petre Maierean
 *
 */
@Component(
	service = GitSynchronizerService.class,
    name = "com.maiereni.synchronizer.git.service.GitSynchronizerService",
    immediate = true,
    property = { SLING_FILTER_SCOPE + "=" + FILTER_SCOPE_REQUEST, Constants.SERVICE_RANKING + ":Integer=1" }
)
@Designate(ocd=GitSynchronizerServiceImpl.Config.class)
public class GitSynchronizerServiceImpl implements GitSynchronizerService {
	private static final Logger logger = LoggerFactory.getLogger(GitSynchronizerServiceImpl.class);
	private static final String DEFAULT_EXPRESSION = "0 0/15 * * * ?";
	private static final String DEFAULT_GIT_BRANCH = "master";
	private static final String DEFAULT_JOB_NAME = "Git Synchronizer";
	private static final String DEFAULT_LOCAL_GIT = "/opt/local/git";
	
	@ObjectClassDefinition(name ="Git Synchronizer Service",
            description="The synchronization service for Sling. It uses Git as a repository for content")
    public @interface Config {
    	@AttributeDefinition(name = "The pase of synchronization",
                description = "Defines the scheduler expression to use for checking with the Git repository for any updates ")
        String update_expression() default GitSynchronizerServiceImpl.DEFAULT_EXPRESSION;
    
        @AttributeDefinition(name = "Git Server URL",
                description = "The information needed to connect to the git server. If this field is left empty, no synchronization is performed")
        String git_remote();
        
        @AttributeDefinition(name = "Git Branch for the source",
                description = "The Git branch for the source. The default is master")
        String git_branch() default GitSynchronizerServiceImpl.DEFAULT_GIT_BRANCH;
        
        @AttributeDefinition(name = "Git Credentials",
                description = "This information can be passed as [user]:[password]. If both this field and git_credentials_file are empty, no synchronization is performed")        
        String git_credentials();
        
        @AttributeDefinition(name = "Git Credential File",
                description = "This points to a properties file containing the user and password as an alternative to the git_credentials. "
                		+ "If the extension of the file name is .enc then this attempts to decrypt it using the private key provided in the "
                		+ "-Djavax.net.ssl.keyStore ")
        String git_crendential_file();
        
        @AttributeDefinition(name = "Git Job Name", description = "The name of the Git Job")
        String job_name() default GitSynchronizerServiceImpl.DEFAULT_JOB_NAME;
        
        @AttributeDefinition(name = "Git Local Repository", description = "The local repository of the Git Job")
        String git_local_repo() default GitSynchronizerServiceImpl.DEFAULT_LOCAL_GIT;
        
        @AttributeDefinition(name = "Git Content Path", description = "The path to the content relative to the root of the project in GIT")        
        String git_content_path() default ".";
    }

    @Reference
	private ResourceResolverFactory resolverFactory;
    
    @Reference
    private Scheduler scheduler;
    
    @Reference
    private ContentImporter contentImporter;
    
    private CredentialsLoader credentialsLoader = new CredentialsLoader();
    
    private String jobName = DEFAULT_JOB_NAME;
    
    private SlingImporter slingImporter;
    private GitDownloader gitDownloader;
    private GitProperties properties;
    
    @Activate
    protected void activate(final BundleContext bundleContext,
    	final Map<String, Object> componentConfig,
    	final Config config) throws Exception {
    	slingImporter = new SlingImporterImpl(resolverFactory, contentImporter);
    	gitDownloader = new GitDownloaderImpl();
    	final GitSynchronizerService service = this;
    	final ScheduleOptions options = getScheduleOptions(config);
    	Thread th = new Thread(new Runnable() {
    		public void run() {
    			try {
    				GitSynchronizer gitSynchronizer = new GitSynchronizer(service);
			    	logger.debug("Activate the Git synchronizer with " + config.update_expression());
			        Map<String, Serializable> c = new HashMap<>();
			        c.put(GitSynchronizer.GIT_PROPERTIES, properties);
			    	if (scheduler.schedule(gitSynchronizer, options)) {
			    		logger.debug("The Git synchronizer has been activated");
			    	}
			    	else {
			    		logger.info("The Git synchronizer could not be added");
			    	}
    			}
    			catch(Exception e) {
    				logger.error("Failed to schedule", e);
    			}
    		}
    	});
    	th.start();
    }
    
    @Deactivate
    protected void deactivate(ComponentContext componentContext) {
    	logger.debug("Deactivate the Git synchronizer");
    	scheduler.unschedule(jobName);
    }
    
    public GitResults download() throws Exception {
    	return download(null);
    }
    /**
     * Download content from Git
     * @param gitProperties properties for the operations
     * @throws Exception
     */
    public GitResults download(final GitProperties gitProperties) throws Exception {
    	GitResults res = null;
    	if (gitProperties == null) {
    		res = gitDownloader.download(properties);
    	}
    	else {
    		res = gitDownloader.download(gitProperties);
    	}
    	return res;
    }
    
    public void importContent() throws Exception {
    	importContent(null);
    }
    /**
     * Import content from Git
     * @param gitProperties properties for the operation
     * @throws Exception 
     */
    public void importContent(final GitProperties gitProperties) throws Exception {
    	GitResults results = download(gitProperties);
    	logger.debug("Downloaded files from Git");
    	ImportProperties props = new ImportProperties();
		props.setRootPath(results.getContentPath());
		props.setChanges(results.getChanges());
    	if (results.isCreated()) {
    		slingImporter.importAll(props);
    		logger.debug("Imported all from file system");
    	}
    	else if (!results.getChanges().isEmpty()) {
    		slingImporter.importDelta(props);
    		logger.debug("Imported delta from file system");
    	}
    }

    private ScheduleOptions getScheduleOptions(final Config config) throws Exception {
    	properties = getProperties(config);
    	jobName = config.job_name();
    	
        Map<String, Serializable> c = new HashMap<>();
        c.put(GitSynchronizer.GIT_PROPERTIES, properties);
        logger.debug("Scheduling expression is {}", config.update_expression());
    	return scheduler.EXPR(config.update_expression()).
    			canRunConcurrently(false).
    			config(c).
    			name(jobName);
    }
    
    private GitProperties getProperties(final Config config) throws Exception {
    	GitProperties properties = credentialsLoader.getPropertiesFromString(config.git_credentials());
    	if (StringUtils.isNotBlank(config.git_crendential_file())) {
    		properties = credentialsLoader.getProperties(config.git_crendential_file());
    	}
    	if (StringUtils.isNotBlank(config.git_remote())) {
    		properties.setRemote(config.git_remote());
    	}
    	if (StringUtils.isNotBlank(config.git_branch())) {
    		properties.setBranchName(config.git_branch());
    	}
    	if (StringUtils.isNotBlank(config.git_local_repo())) {
    		properties.setLocalRepo(config.git_local_repo());
    	}
    	if (StringUtils.isNotBlank(config.git_content_path())) {
    		properties.setContentPath(config.git_content_path());
    	}
    	return properties;
    }
}
