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
package com.maiereni.util.locator;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Petre Maierean
 *
 */
public class ClasspathLocator extends JarReader {
	private static final Logger logger = LoggerFactory.getLogger(ClasspathLocator.class);

	public boolean locate(final String className, final String classpathFile) throws Exception {
		File f = new File(classpathFile);
		String s = FileUtils.readFileToString(f, "UTF-8");
		String[] cps = s.split(";|:");
		boolean ret = false;
		String actualClassName = className.replace('.', '/');
		for(String cp : cps) {
			if (cp.endsWith(".jar")) {
				File fc = new File(cp);
				if (isInJar(fc, actualClassName)) {
					logger.debug("Found it in " + fc.getAbsolutePath());
					ret = true;
				}
			}
		}
		return ret;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			if (args.length != 2) {
				throw new Exception("Expected arguments: className classpath.txt");
			}
			ClasspathLocator locator = new ClasspathLocator();
			if (!locator.locate(args[0], args[1])) {
				logger.debug("class not found");
			}
		}
		catch(Exception e) {
			logger.error("Failed to locate", e);
		}
	}

}
