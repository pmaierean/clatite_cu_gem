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
package com.maiereni.synchronizer.git.service;

import com.maiereni.synchronizer.git.service.bo.GitProperties;
import com.maiereni.synchronizer.git.service.bo.GitResults;

/**
 * The API of the OSGI service that synchronizes the Sling JCR with a GIT repository
 * 
 * @author Petre Maierean
 *
 */
public interface GitSynchronizerService {
	/**
	 * Download from git to the local file system
	 * @param gitProperties
	 * @return
	 * @throws Exception
	 */
	GitResults download(GitProperties gitProperties) throws Exception;
	
	/**
	 * Import content from git
	 * @param gitProperties
	 * @throws Exception
	 */
	void importContent(GitProperties gitProperties) throws Exception;
}
