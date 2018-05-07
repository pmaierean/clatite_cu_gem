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

import java.io.File;
import java.net.URL;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ShutdownHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Embedded Jetty boot loader application
 * 
 * @author Petre Maierean
 * 
 */
public class NpmRegistryStart {
	private static final Logger logger = LoggerFactory.getLogger(NpmRegistryStart.class);
	public static final String SERVER_KEY_FILE = "npmRegistry";

	public NpmRegistryStart(final String[] args) throws Exception {
		BootLoaderConfiguration configuration = BootLoaderConfiguration.getConfiguration(args);
		if (configuration != null)
			initialize(configuration);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			new NpmRegistryStart(args);
		} catch (Exception e) {
			logger.error("Failed to load web application", e);
		}
	}
	
	/**
	 * Launch the WebContainer
	 * @throws Exception
	 */
	protected void initialize(final BootLoaderConfiguration configuration) throws Exception {
		final String token = UUID.randomUUID().toString().replaceAll("-", "");
		FileUtils.writeStringToFile(new File(SERVER_KEY_FILE), token);
		logger.debug("The server is starting up. Assigned token is " + token);
		logger.debug("Context path is: " + configuration.getContextPath());
		Server server = new Server(configuration.getPort());
		ShutdownHandler handler = new ShutdownHandler(token);
		handler.setHandler(initializeHandler(configuration));
		server.setHandler(handler);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				logger.info("The server is shutting down ...");
				server.setStopAtShutdown(true);
			}
		});
		if (configuration.isLogRequests()) {
			NCSARequestLog requestLog = new NCSARequestLog("npmRegistry.log");
			requestLog.setAppend(true);
			requestLog.setExtended(false);
			requestLog.setLogTimeZone("GMT");
			requestLog.setLogLatency(true);
			server.setRequestLog(requestLog);
			logger.debug("Log requests to: npmRegistry.log"); 
		}
		server.start();
		logger.info("The server has been started. Listening to port " + configuration.getPort());
		server.join();
		logger.info("Shutting down");
	}
	
	private Handler initializeHandler(final BootLoaderConfiguration configuration) throws Exception {
		WebAppContext contextHandler = new WebAppContext();
		contextHandler.setContextPath(configuration.getContextPath());
		URL url = NpmRegistryStart.class.getResource("/webapp");
		Resource r = Resource.newResource(url);
		contextHandler.setBaseResource(r);
        return contextHandler;
	}
}
