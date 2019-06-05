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
package com.maiereni.util;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Petre Maierean
 *
 */
public class DeletableDirectory implements Closeable {
	private static final Logger logger = LoggerFactory.getLogger(DeletableDirectory.class);
	public static final String TMP = System.getProperty("java.io.tmpdir");
	private File dir;
	
	public DeletableDirectory() throws Exception {
		String name = UUID.randomUUID().toString().replaceAll("-", "");
		dir = new File(TMP, name);
		if (!dir.mkdirs())
			throw new Exception("Cannot make directory at " + dir.getPath());
	}


	public File getDir() {
		return dir;
	}
	
	public File makeSubdirectory(final String relPath) throws Exception {
		File ret = null;
		if (StringUtils.isNotBlank(relPath)) {
			ret = new File(dir, relPath);
			if (!ret.exists()) {
				if (!ret.mkdirs()) {
					throw new Exception("Could not make file at " + ret.getPath());
				}
			}
		}
		return ret;
	}
	
	public String getPath() {
		return dir.getPath();
	}
	
	@Override
	public void close() throws IOException {
		if (dir != null) {
			try {
				FileUtils.forceDelete(dir);
			}
			catch(Exception e) {
				logger.error("Failed to delete directory " + dir.getPath(), e);
			}
		}
	}
}
