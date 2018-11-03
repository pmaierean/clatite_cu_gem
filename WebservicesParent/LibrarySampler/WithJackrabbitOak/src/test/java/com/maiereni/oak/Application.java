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

import java.io.File;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.maiereni.oak.bo.RepositoryProperties;

/**
 * The tester application
 * @author Petre Maierean
 *
 */
@Component
public class Application extends OakBeanFactory {
	protected static final Logger logger = LoggerFactory.getLogger(Application.class);

	@Override
	public RepositoryProperties getProperties() {
		RepositoryProperties props = new RepositoryProperties();
		File fDir = new File(FileUtils.getTempDirectoryPath(), UUID.randomUUID().toString().replaceAll("-", ""));
		props.setRepositoryPath(fDir.getPath());
		props.setAdminPassword("admin");
		props.setAdminUser("admin");
		return props;
	}
}
