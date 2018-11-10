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
package com.maiereni.oak.filestore;

import java.io.File;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.oak.segment.SegmentNodeStoreBuilders;
import org.apache.jackrabbit.oak.segment.file.FileStore;
import org.apache.jackrabbit.oak.segment.file.FileStoreBuilder;
import org.apache.jackrabbit.oak.spi.state.NodeStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.maiereni.oak.OakBeanFactory;
import com.maiereni.oak.bo.RepositoryProperties;

/**
 * The tester application
 * @author Petre Maierean
 *
 */
@Component
public class FileSystemOakBeanFactory extends OakBeanFactory {
	protected static final Logger logger = LoggerFactory.getLogger(FileSystemOakBeanFactory.class);

	@Override
	public RepositoryProperties getProperties() {
		RepositoryProperties props = new RepositoryProperties();
		String repositoryPath = System.getProperty("repository.path");
		if (StringUtils.isBlank(repositoryPath)) {
			File fDir = new File(FileUtils.getTempDirectoryPath(), UUID.randomUUID().toString().replaceAll("-", ""));
			repositoryPath = fDir.getPath();
		}
		
		props.setRepositoryPath(repositoryPath);
		props.setAdminPassword("admin");
		props.setAdminUser("admin");
		return props;
	}
	
	@Bean(name="repositoryDir")
	public File getRepositoryDir() throws Exception  {
		RepositoryProperties properties = getProperties();
		if (StringUtils.isBlank(properties.getRepositoryPath())) {
			throw new Exception("The configuration for path cannot be empty");
		}
		File repositoryDir = new File(properties.getRepositoryPath());
		if (repositoryDir.isFile()) {
			throw new Exception("The repository name points to a file");
		}
		else if (!(repositoryDir.exists() || repositoryDir.mkdirs())) {
			throw new Exception("Cannot make directory at " + repositoryDir.getPath());
		}
		return repositoryDir;
	}
	
	/**
	 * Create a file system node store
	 * @return
	 * @throws Exception
	 */
	@Override
	public NodeStore getNodeStore() throws Exception {
		File repositoryDir = getRepositoryDir();
		FileStoreBuilder fileStoreBuilder = FileStoreBuilder.fileStoreBuilder(repositoryDir);
		fileStoreBuilder.withMaxFileSize(1);
		FileStore segmentStore = fileStoreBuilder.build();			
		NodeStore ret = SegmentNodeStoreBuilders.builder(segmentStore).build();			
		logger.debug("Node store has been created at " + repositoryDir);
		return ret;
	}
}
