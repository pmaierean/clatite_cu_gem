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

import org.apache.commons.io.FileUtils;
import org.apache.jackrabbit.oak.Oak;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.maiereni.oak.Cleaner;

/**
 * @author Petre Maierean
 *
 */
@Component
public class FolderCleaner implements Cleaner {
	public static final Logger logger = LoggerFactory.getLogger(FolderCleaner.class);
	
	@Autowired
	private File repositoryDir;
	@Autowired
	private Oak oak;
	
	@Override
	public void cleanup() {
		logger.debug("Start cleanup");
		try {
			FileUtils.deleteDirectory(repositoryDir);
			logger.debug("Directory has been deleted: " + repositoryDir.getPath());
		}
		catch(Exception e) {
			logger.error("Failed to delete " + repositoryDir.getPath(), e);
		}
	}

}
