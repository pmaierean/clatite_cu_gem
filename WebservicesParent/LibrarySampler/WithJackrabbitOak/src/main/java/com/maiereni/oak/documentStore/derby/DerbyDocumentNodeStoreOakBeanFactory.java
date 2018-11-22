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
package com.maiereni.oak.documentStore.derby;

import java.io.File;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.maiereni.oak.bo.RepositoryProperties;
import com.maiereni.oak.documentStore.AbstractJDBCDocumentNodeStoreOakBeanFactory;

/**
 * Bean factory for a Document Node Store based on Embedded Derby. It does use an independent Blob store
 * 
 * @author Petre Maierean
 *
 */
@Configuration
public class DerbyDocumentNodeStoreOakBeanFactory extends AbstractJDBCDocumentNodeStoreOakBeanFactory {
	public static final String DERBY_SYSTEM_HOME = "derby.system.home";
	public static final String DATABASE_PATH = "jdbc.home";
	public static final String DATABASE_NAME = "jdbc.name";
	public static final String DERBY_URL_TEMPLATE = "jdbc:derby:%s";
	public static final String DERBY_SHUTDOWN_TEMPLATE = "jdbc:derby:%s;shutdown=true";
	public static final String DERBY_SHUTDOWN_KEY = "derby.shutdown";
	private static final Logger logger = LoggerFactory.getLogger(DerbyDocumentNodeStoreOakBeanFactory.class);

	@Bean(name="repositoryProperties")
	@Override
	public RepositoryProperties getProperties() throws Exception {
		RepositoryProperties repositoryProperties = new RepositoryProperties();
		String derbyFolder = getProperty(DATABASE_PATH, null);
		if (StringUtils.isBlank(derbyFolder)) {
			throw new Exception("Cannot find a root folder for Derby");
		}
		File repositoryDir = new File(derbyFolder);
		if (!repositoryDir.isDirectory()) {
			throw new Exception("Cannot find a directory at "  + derbyFolder);
		}
		repositoryProperties.put(DERBY_SYSTEM_HOME, repositoryDir);
		System.setProperty(DERBY_SYSTEM_HOME, derbyFolder);
		
		String derbyDBName = getProperty(DATABASE_NAME, "");
		String url = String.format(DERBY_URL_TEMPLATE, derbyDBName);
		
		logger.debug("Use Derby url {}", url);
		int ix = derbyDBName.indexOf(";");
		if (ix > 0) {
			derbyDBName = derbyDBName.substring(0, ix);
		}
		String shutdown = String.format(DERBY_SHUTDOWN_TEMPLATE, "");
		repositoryProperties.put(DERBY_SHUTDOWN_KEY, shutdown);

		BasicDataSource bds = new BasicDataSource();
        bds.setDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");
        bds.setUrl(url);
        String userName = getProperty(USER_KEY, "");
        bds.setUsername(userName);
        String pwd = getProperty(PASSWORD_KEY, "");
        bds.setPassword(pwd);
        bds.setInitialSize(1);
        bds.setMaxIdle(60);
        bds.setMinIdle(60);
        repositoryProperties.put(DATASOURCE_KEY, bds);
		return repositoryProperties;
	}

}
