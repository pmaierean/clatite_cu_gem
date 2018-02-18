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
package com.maiereni.blpapi.importer;

/**
 * @author Petre Maierean
 *
 */
class VersionDetector {
	private static final String FILE_NAME_PREFIX = "blpapi_java_";
	private static final String FILE_NAME_SUFFIX = ".tar.gz";
	
	public String getVersion(final String s) {
		String ret = "";
		if (s.startsWith(FILE_NAME_PREFIX)) {
			int j = s.indexOf(FILE_NAME_SUFFIX);
			if (j > 0)
				ret = s.substring(FILE_NAME_PREFIX.length(), j);
			else {
				ret = s.substring(FILE_NAME_PREFIX.length());
			}
		}
		return ret;
	}
	
	public int compareVersions(final String s1, final String s2) {
		int ret = 0;
		try {
			String[] toks1 = s1.split("\\x2E");
			String[] toks2 = s2.split("\\x2E");
			for (int i =0; i < toks1.length && i < toks2.length; i++) {
				int i1 = Integer.parseInt(toks1[i]);
				int i2 = Integer.parseInt(toks2[i]);
				if (i1 > i2) {
					ret = 1;
					break;
				}
				else if (i1 < i2) {
					ret = -1;
					break;
				}
			}
		}
		catch(Exception e) {
			
		}
		return ret;
	}
}
