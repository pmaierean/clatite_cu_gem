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
package com.maiereni.npm.web;

import java.io.InputStream;
import java.text.MessageFormat;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A configuration loader class. Initializes the configuration parameters from System properties and attempts
 * to load values for them from command line arguments 
 * 
 * @author Petre Maierean
 *
 */
public class BootLoaderConfiguration {
	private static final Logger logger = LoggerFactory.getLogger(BootLoaderConfiguration.class);
	public static final String CMD_LINE_ARG_PORT = "-port=";
	public static final String SYSTEM_PROP_KEY_PORT = "app.port";
	public static final String CMD_LINE_ARG_CONFIG = "-cfgFile=";
	public static final String SYSTEM_PROP_KEY_CONFIG = "app.cfgFile";
	public static final String CMD_LINE_ARG_PROFILE = "-profile=";
	public static final String SYSTEM_PROP_KEY_PROFILE = "app.profile";
	public static final String CMD_LINE_ARG_CONTEXT_PATH = "-contextPath=";
	public static final String SYSTEM_PROP_KEY_CONTEXT_PATH = "app.contextPath";
	public static final String SYSTEM_PROP_KEY_LOG_REQUEST = "app.logRequests";
	public static final String CMD_LINE_ARG_MAPPING_URL = "-mappingURL=";
	public static final String SYSTEM_PROP_KEY_MAPPING_URL = "app.mappingURL";
	public static final String DISPLAY_HELP = "--help";
	public static final String DISPLAY_HELP_SHORT = "-h";
	private int port;
	private String contextPath;
	private boolean logRequests;
	
	/**
	 * Lists the configuration description text
	 * @return
	 */
	public static final String listConfigurationText() throws Exception {
		try (InputStream is = BootLoaderConfiguration.class.getResourceAsStream("/config.txt");) {
			String tplt = IOUtils.toString(is);
			return MessageFormat.format(tplt, 
					SYSTEM_PROP_KEY_PORT, 
					SYSTEM_PROP_KEY_CONTEXT_PATH, 
					CMD_LINE_ARG_PORT, 
					CMD_LINE_ARG_CONTEXT_PATH);
		}
	}
	/**
	 * Loads configuration from command line arguments
	 * @param args
	 * @return
	 */
	public static BootLoaderConfiguration getConfiguration(final String[] args) throws Exception {
		BootLoaderConfiguration config = new BootLoaderConfiguration();
		
		for(String arg: args) {
			if (arg.startsWith(CMD_LINE_ARG_PORT))
				config.setPort(arg.substring(CMD_LINE_ARG_PORT.length()));
			else if (arg.startsWith(CMD_LINE_ARG_CONTEXT_PATH))
				config.setContextPath(arg.substring(CMD_LINE_ARG_CONTEXT_PATH.length()));
			else if (arg.equals(DISPLAY_HELP_SHORT) || arg.equals(DISPLAY_HELP)) {
				try {
					System.out.println(listConfigurationText());
				}
				catch(Exception e) {
					logger.error("Failed to prepare a helper message", e);
				}
				return null;
			}
		}
		return config;
	}

	private BootLoaderConfiguration() {
		setPort(System.getProperty(SYSTEM_PROP_KEY_PORT,"8187"));
		setContextPath(System.getProperty(SYSTEM_PROP_KEY_CONTEXT_PATH, "/npmRegistry"));
		setLogRequests("true".equals(System.getProperty(SYSTEM_PROP_KEY_LOG_REQUEST, "false")));
	}
	
	public int getPort() {
		return port;
	}
	
	private void setPort(final String sPort) {
		if (StringUtils.isNoneEmpty(sPort))
			try {
				port = Integer.parseInt(sPort);
			}
			catch(Exception e){
				logger.error("Cannot set port from string '{}'. An integer is expected", sPort);				
			}
	}

	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}
	public boolean isLogRequests() {
		return logRequests;
	}
	public void setLogRequests(boolean logRequests) {
		this.logRequests = logRequests;
	}

}

