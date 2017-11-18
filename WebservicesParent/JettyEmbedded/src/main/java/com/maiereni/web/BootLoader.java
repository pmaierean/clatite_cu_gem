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
package com.maiereni.web;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * The Embedded Jetty boot loader application
 * 
 * @author Petre Maierean
 * 
 */
public class BootLoader {
	private static final Logger logger = LoggerFactory.getLogger(BootLoader.class);
	private BootLoaderConfiguration configuration;
	private Handler handler;
	private Server server;

	public BootLoader(final String[] args) throws Exception {
		logger.debug("Server is initializing ");
		configuration = BootLoaderConfiguration.getConfiguration(args);
		if (configuration != null) {
			initializeHandler();
			startUp();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			new BootLoader(args);
		} catch (Exception e) {
			logger.error("Failed to load web application", e);
		}
	}
	
	/**
	 * Launch the WebContainer
	 * @throws Exception
	 */
	public void startUp() throws Exception {
		logger.debug("The server is starting up");
		server = new Server(configuration.getPort());
		server.setHandler(handler);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				logger.info("The server is shutting down ...");
				server.setStopAtShutdown(true);
			}
		});
		server.start();
		server.join();
	}
	
	private void initializeHandler() throws Exception {
		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setConfigLocation(configuration.getConfiguration());
        context.getEnvironment().setDefaultProfiles(configuration.getProfile());
        ServletContextHandler contextHandler = new ServletContextHandler();
        contextHandler.setErrorHandler(null);
        contextHandler.setContextPath(configuration.getContextPath());
        contextHandler.addServlet(new ServletHolder(new DispatcherServlet(context)), configuration.getMappingURL());
        contextHandler.addEventListener(new ContextLoaderListener(context));
        contextHandler.setResourceBase(new ClassPathResource("webapp").getURI().toString());
        handler = contextHandler;
	}
}
