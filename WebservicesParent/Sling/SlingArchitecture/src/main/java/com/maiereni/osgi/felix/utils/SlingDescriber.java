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
package com.maiereni.osgi.felix.utils;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maiereni.osgi.felix.utils.bo.SlingInfo;
import com.maiereni.osgi.felix.utils.model.ModelHandler;
import com.maiereni.osgi.felix.utils.sling.SlingInfoDownloader;
import com.maiereni.sling.info.Model;

/**
 * Describes the architecture of an OSGI application using a Restful client for the Admin Console
 * It uses the API from the Sparx Enterprise Architect
 * To run this application, pass the path to the native library with -Djava.library.path=
 * 
 * @author Petre Maierean
 *
 */
public class SlingDescriber {
	private static final Logger logger = LoggerFactory.getLogger(SlingDescriber.class);
	private String host, user, pwd;
	private String outputModel, inputModel, eaModel;
	private ObjectMapper mapper = new ObjectMapper();
	
	public SlingDescriber() {
		mapper = new ObjectMapper();
		mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		host = "localhost:8080";
		user = "admin";
		pwd = "admin";
		
	}
	
	/**
	 * Describe the instance that is reachable by its Restful API
	 * 
	 * @throws Exception
	 */
	public void describe() throws Exception {
		ModelHandler modelHandler = new ModelHandler();
		Model slingInfo = null;
		if (StringUtils.isNotBlank(inputModel)) {
			slingInfo = modelHandler.loadFromXml(inputModel);
			slingInfo = modelHandler.updataModel(slingInfo);
		}
		else {
			SlingInfoDownloader downloader = new SlingInfoDownloader();
			SlingInfo si = downloader.getSlingInfo(host, user, pwd);
			slingInfo = modelHandler.convert(si);
		}
		logger.debug("The Sling Information bean has been loaded");
		if (StringUtils.isNotBlank(outputModel)) {
			modelHandler.writetoXML(slingInfo, outputModel);
		}
		if (StringUtils.isNotBlank(eaModel)) {
			modelHandler.writetoEA(slingInfo, eaModel);
		}
	}
	
	/**
	 * Describe a Sling instane 
	 * 
	 * @param args
	 */
	public static void main(final String[] args) {
		SlingDescriber describer = new SlingDescriber();
		describer.setParameters(args);
		if (describer.validateLib()) {
			try {
				describer.describe();
			}
			catch(Exception e) {
				logger.error("Failed to describe", e);
			}
		}
		else {
			logger.error("The application cannot be run without the EA native library");
		}
	}
	
	
	void setParameters(final String[] args) {
		for(String arg: args) {
			String[] sp = arg.split("=");
			if (sp.length==2) {
				if (sp[0].equals("host")) {
					host = sp[1];
				}
				else if (sp[0].equals("user")) {
					user = sp[1];
				}
				else if (sp[0].equals("password")) {
					pwd = sp[1];
				}
				else if (sp[0].equals("outXml")) {
					outputModel = sp[1];
				}
				else if (sp[0].equals("inXml")) {
					inputModel = sp[1];
				}
				else if (sp[0].equals("eaModel")) {
					eaModel = sp[1];
				}
			}
		}
	}
	
	
	boolean validateLib() {
		boolean ret = true;
		if (StringUtils.isNotBlank(eaModel)) {
			String sparxApi = System.getProperty("java.library.path");
			if (StringUtils.isBlank(sparxApi)) {
				logger.error("Please specify the folder containing the sparx dll (SSJavaCOM.dll) with -Djava.library.path");
				ret = false;
			}
			else {
				File fDir = new File(sparxApi);
				if (!fDir.isDirectory()) {
					logger.error("Please specify an existing folder containing the sparx dll (SSJavaCOM.dll) with java.library.path");				
					ret = false;
				}			
			}
		}
		return ret;
	}
}
