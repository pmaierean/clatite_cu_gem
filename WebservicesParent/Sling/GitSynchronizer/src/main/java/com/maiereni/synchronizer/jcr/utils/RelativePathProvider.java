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
package com.maiereni.synchronizer.jcr.utils;

import java.io.File;

/**
 * @author Petre Maierean
 *
 */
class RelativePathProvider {
	private String rootPath;
	public RelativePathProvider(final File dir) {
		if (dir != null) {
			this.rootPath = dir.getPath();
		}
	}

	public String getRelativePath(final File f) {
		String ret = null;
		if (f != null) {
			String path = f.getPath();
			if (path.startsWith(rootPath)) {
				if (path.equals(rootPath)) {
					ret = "";
				}
				else {
					ret = path.substring(rootPath.length() + 1);
					ret = ret.replace('\\', '/');
				}
			}
		}
		return ret;
	}
}
