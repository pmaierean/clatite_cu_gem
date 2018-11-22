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
import java.sql.DriverManager;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.maiereni.oak.Cleaner;
import com.maiereni.oak.bo.RepositoryProperties;
/**
 * @author Petre Maierean
 *
 */
@Component
public class OakCleaner implements Cleaner {
	public static final Logger logger = LoggerFactory.getLogger(OakCleaner.class);

	@Autowired
	private RepositoryProperties repositoryProperties;
	
	public void cleanup() {
		logger.debug("Start cleaning up");
		Object o = repositoryProperties.get(DerbyDocumentNodeStoreOakBeanFactory.DERBY_SYSTEM_HOME);
		if (o != null) {
			try {
				String cleanup = (String)repositoryProperties.get(DerbyDocumentNodeStoreOakBeanFactory.DERBY_SHUTDOWN_KEY);
				logger.debug("Cleanup " + cleanup);
				DriverManager.getConnection(cleanup, "", "");
			}
			catch(Exception e) {
				//logger.error("Cannot cleanup the datatabse", e);
			}
			File repositoryDir = (File)o;
			try {
				FileUtils.deleteDirectory(repositoryDir);
				logger.debug("Directory has been deleted: " + repositoryDir.getPath());
			}
			catch(Exception e) {
				logger.error("Failed to remove repository home: " + repositoryDir.getPath(), e);
			}
		}
	}
}
