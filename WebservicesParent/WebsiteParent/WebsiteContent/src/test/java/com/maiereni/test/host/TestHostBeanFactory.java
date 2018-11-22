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
package com.maiereni.test.host;

import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

import com.maiereni.host.web.Cleaner;
import com.maiereni.host.web.jaxrs.service.impl.OakBeanFactory;

/**
 * @author Petre Maierean
 *
 */
@Configuration
public class TestHostBeanFactory {
	private static final Logger logger = LoggerFactory.getLogger(TestHostBeanFactory.class);
	public static final String DERBY_SYSTEM_HOME = "derby.system.home";
	public static final String DERBY_URL = "jdbc:derby:test;create=true";
	public static final String DERBY_SHUTDOWN_URL = "jdbc:derby:;shutdown=true";
	
	private File tmpDir;
	
	/**
	 * Bind an embedded Derby database
	 */
	@Bean(name="datasource")
	public DataSource getDatasource() throws Exception {
		tmpDir = new File(FileUtils.getTempDirectory(), UUID.randomUUID().toString().replaceAll("-", ""));
		if (!tmpDir.mkdirs()) {
			throw new Exception("Cannot make a temporary directory at " + tmpDir.toString());
		}
		System.setProperty(DERBY_SYSTEM_HOME, tmpDir.getPath());
		SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
		BasicDataSource bds = new BasicDataSource();
        bds.setDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");
        bds.setUsername("");
        bds.setPassword("");
        bds.setUrl(DERBY_URL);
        bds.setInitialSize(1);
        bds.setMaxIdle(60);
        bds.setMinIdle(60);
        logger.debug("Creates a Derby database at {}", tmpDir.getPath());
        builder.bind(OakBeanFactory.OAK_DATASOURCE, bds);
        builder.activate();
        return bds;
	}

	@Bean(name="cleaner")
	public Cleaner getCleaner() {
		return new Cleaner() {
			@Override
			public void cleanup() throws Exception {
				try {
					logger.debug("Cleanup Derby with {}", DERBY_SHUTDOWN_URL);
					DriverManager.getConnection(DERBY_SHUTDOWN_URL, "", "");
				}
				catch(SQLException ex) {
					if (ex.getMessage().equals("Derby system shutdown.")) {
						logger.debug("Derby system shutdown");
					}
					else {
						logger.error("Failed shutdown", ex);
					}
				}
				catch(Exception e) {
					logger.error("Cannot cleanup the datatabse", e);
				}
				try {
					FileUtils.deleteDirectory(tmpDir);
					logger.debug("Directory has been deleted: " + tmpDir.getPath());
				}
				catch(Exception e) {
					logger.error("Failed to remove repository home: " + tmpDir.getPath(), e);
				}				
			}
		};
	}
}
