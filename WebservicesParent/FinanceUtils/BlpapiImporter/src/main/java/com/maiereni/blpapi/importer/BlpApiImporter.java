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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maiereni.blpapi.importer.bo.PackageBean;

/**
 * Application class to import the Blp API libraries to the local MAVEN repository
 * @author Petre Maierean
 *
 */
public class BlpApiImporter {
	private static final Logger logger = LoggerFactory.getLogger(BlpApiImporter.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			FileDownloader utils = new FileDownloader();
			PackageBean packageBean = null;
			if (args.length > 0)
				packageBean = utils.getLibrary(args[0]);
			else
				packageBean = utils.getLibrary();				
			logger.debug("Result: " + packageBean);
		}
		catch(Exception e) {
			logger.error("Failed to import the libraries due to an exception", e);
		}
	}

}
