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

import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ShutdownHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Petre Maierean
 *
 */
public class NpmRegistryStop extends NpmRegistryStart{
	private static final Logger logger = LoggerFactory.getLogger(NpmRegistryStop.class);

	public NpmRegistryStop(String[] args) throws Exception {
		super(args);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			new NpmRegistryStop(args);
		} catch (Exception e) {
			logger.error("Failed to load web application", e);
		}
	}

	@Override
	protected void initialize(final BootLoaderConfiguration configuration) throws Exception {
		String token = FileUtils.readFileToString(new File(SERVER_KEY_FILE));
		logger.debug("The server token is " + token);
		Server server = new Server(configuration.getPort());
		ShutdownHandler handler = new ShutdownHandler(token);
		server.setHandler(handler);
		handler.sendShutdown();
		logger.debug("The request to shutdown has been sent");
	}
}
