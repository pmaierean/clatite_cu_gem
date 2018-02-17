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
package com.maiereni.utils.archiving;

import java.io.File;

/**
 * @author Petre Maierean
 *
 */
public abstract class BaseArchivingUtility implements ArchivingUtility {
	public final String LOCAL_DIR = "com.maiereni.utils.archiving.dir";
	private static final String DEFAULT_LOCAL_DIR = System.getProperty("user.home") + "/Downloads";
	protected File destDir;
	protected String delim;
	
	public BaseArchivingUtility() throws Exception {
		String baseDir = System.getProperty(LOCAL_DIR, DEFAULT_LOCAL_DIR);
		delim = System.getProperty("file.separator");
		destDir = new File(baseDir);
		if (!destDir.isDirectory())
			if (!destDir.mkdirs())
				throw new Exception("Cannot make base directory at " + baseDir);
	}
}
