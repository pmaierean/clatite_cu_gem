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
import java.io.FileFilter;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class that locates classes in JAR files
 * @author Petre Maierean
 *
 */
public class Locate extends JarReader {
	private static final Logger logger = LoggerFactory.getLogger(Locate.class);
	
	/**
	 * Finds a class by the provided className in all JAR or ZIP files in the folder
	 * 
	 * @param className
	 * @param rootFolder
	 * @throws Exception
	 */
	public void locate(final String className, final String searchFolder) throws Exception {
		if (StringUtils.isBlank(className))
			throw new Exception("The className argument cannot be blank");
		if (StringUtils.isBlank(searchFolder))
			throw new Exception("The search folder argument cannot be blank");
		File root = new File(searchFolder);
		if (!root.isDirectory()) {
			throw new Exception("String '" + searchFolder + "' does not point to a folder");
		}
		String actualArtefactName = className.replaceAll("\\x2e", "/");
		String path = locate(actualArtefactName, root);
		if (StringUtils.isBlank(path)) {
			logger.debug("The class {} has not been found", className);
		}

	}
	
	/**
	 * Expected arguments: class_name folder_to_search_in
	 * @param args
	 */
	public static void main(String[] args) {
		Locate locate = null;
		try {
			String className = null;
			if (args.length > 0)
				className = args[0];
			else
				throw new Exception("Class name to look for has not been provided");
			String root = args.length > 1 ? args[1] : "c:/";
			locate = new Locate();
			locate.locate(className, root);
			logger.debug("Done");
		}
		catch(Exception e) {
			logger.error("Failed to locate the requested class", e);
		}
	}
	
	protected void foundClass(final String className, final File jarFile) {
		logger.debug("Found in: " + jarFile.getPath());		
	}
	
	private String locate(final String className, final File dir) throws Exception {
		File[] children = dir.listFiles(new JarZipFilter());
		String path = null;
		if (children != null)
			for(File f : children)  {
				if (f.isFile()) {
					if (isInJar(f, className)) {
						foundClass(className, f);
						path = f.getPath();
					}
				}
				else 
					path = locate(className, f);
			}
		return path;
	} 
	
	public class JarZipFilter implements FileFilter {
		public boolean accept(File f) {
			if (f.isDirectory())
				return true;
			String name = f.getName().toLowerCase();
			return name.endsWith(".jar") || name.endsWith(".zip");
		}
	}
}
