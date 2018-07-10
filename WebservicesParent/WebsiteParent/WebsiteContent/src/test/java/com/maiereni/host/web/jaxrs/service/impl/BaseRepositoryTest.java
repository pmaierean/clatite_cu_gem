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

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

import org.apache.cxf.helpers.FileUtils;
import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.config.RepositoryConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import com.maiereni.host.web.util.ConfigurationProvider;

/**
 * Base class for the unit tests that user JCR
 * @author Petre Maierean
 *
 */
class BaseRepositoryTest {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	private File repoDir;
	
	/**
	 * Creates a test repository
	 * @return
	 * @throws Exception
	 */
	public RepositoryImpl getRepository() throws Exception {
		repoDir = FileUtils.createTmpDir();
		logger.debug("Temporary dir has been created at " + repoDir.getPath());
		Properties props = new Properties();
		props.setProperty(ServicesFactory.REPOSITORY_HOME, repoDir.getPath());
		props.setProperty(ServicesFactory.WORKSPACE_HOME, repoDir.getPath());
		props.setProperty(ServicesFactory.WORKSPACE_NAME, "default");
		try(InputStream is = getClass().getResourceAsStream("/repository.xml")) {
			InputSource source = new InputSource(is);
			RepositoryConfig cfg = RepositoryConfig.create(source, props);
			return RepositoryImpl.create(cfg);
		}
	}
	
	/**
	 * Clean up after running the test
	 * @throws Exception
	 */
	public void cleanUp() throws Exception {
		if (repoDir != null) {
			FileUtils.removeDir(repoDir);
			logger.debug("The temporary directory has been removed");
		}
	}
	/**
	 * Creates a Properties object wrapper
	 * @return
	 * @throws Exception
	 */
	public ConfigurationProvider getConfigurationProvider() throws Exception {
		return new BogusConfigurationProvider();
	}
	
	public class BogusConfigurationProvider implements ConfigurationProvider {
		private Properties properties = new Properties();
		
		@Override
		public String getProperty(String key, String defaultValue) {
			return properties.getProperty(key, defaultValue);
		}

		@Override
		public void setProperty(String key, String value) throws Exception {
			properties.setProperty(key, value);
		}
	}
}
