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

import com.maiereni.synchronizer.git.service.bo.ImportProperties;

/**
 * Service to import from file system
 * 
 * @author Petre Maierean
 *
 */
public interface SlingImporter {
	/**
	 * Import all the files and folders from the local file system 
	 * @param properties
	 * @throws Exception
	 */
	void importAll(ImportProperties properties) throws Exception;
	/**
	 * Import delta from the local file system
	 * @param properties
	 * @throws Exception
	 */
	void importDelta(ImportProperties properties) throws Exception;
}
