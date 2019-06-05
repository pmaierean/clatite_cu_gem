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

import org.apache.sling.commons.scheduler.Job;
import org.apache.sling.commons.scheduler.JobContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maiereni.synchronizer.git.service.GitSynchronizerService;
import com.maiereni.synchronizer.git.service.bo.GitProperties;

/**
 * A synchronized with Git
 * @author Petre Maierean
 *
 */
public class GitSynchronizer implements Job, Runnable {
	public static final String GIT_PROPERTIES = "gitProperties";
	private static final Logger logger = LoggerFactory.getLogger(GitSynchronizer.class);
	private GitSynchronizerService service;
	
	public GitSynchronizer(final GitSynchronizerService service) {
		this.service = service;
	}
	
	@Override
	public void execute(JobContext context) {
		try {
			logger.debug("Start synchronizing");
			GitProperties properties = (GitProperties)context.getConfiguration().get(GIT_PROPERTIES);
			service.importContent(properties);
		}
		catch(Exception e) {
			logger.error("There was a failure downloading from GIT", e);
		}
	}

	@Override
	public void run() {
		logger.debug("Run synchronizer");
	}
	
}
