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
package com.maiereni.sling.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.felix.framework.util.FelixConstants;
import org.apache.sling.launchpad.base.impl.Sling;
import org.apache.sling.launchpad.base.shared.Notifiable;
import org.apache.sling.launchpad.base.shared.SharedConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Launchpad application that uses Sling from local projects
 * @author Petre Maierean
 *
 */
public class SlingMain implements Notifiable {
	private static final String PROP_PORT = "org.osgi.service.http.port";
	private static final String PROP_HOST = "org.apache.felix.http.host";
	private static final String PROP_START_UP_MODE = "org.apache.sling.launchpad.startupmode";
	private static final String PROP_LOG_LEVEL = "org.apache.sling.commons.log.level";
	private static final Logger LOGGER = LoggerFactory.getLogger(SlingMain.class);
	private Sling sling;
	private Map<String, String> commandLine;
	
	public SlingMain() {
		commandLine = new HashMap<String, String>();
		commandLine.put(PROP_PORT, "8080");
		commandLine.put(PROP_HOST, "127.0.0.1");
		commandLine.put(PROP_START_UP_MODE, "INSTALL");
		commandLine.put(FelixConstants.LOG_LEVEL_PROP, "4");
		commandLine.put(PROP_LOG_LEVEL, "4");
		//commandLine.put(Constants.FRAMEWORK_BEGINNING_STARTLEVEL, "1");
	}
	
	public void startSling(final File jarFile, final File slingHome) throws Exception {
		Map<String, String> props = new HashMap<String, String>();
		props.put(SharedConstants.SLING_HOME, slingHome.getPath());
		commandLine.put(SharedConstants.SLING_LAUNCHPAD, slingHome.getPath());
		ClassLoader classloader = getClass().getClassLoader();
		JarLoaderResourceProvider resProvider = new JarLoaderResourceProvider(classloader, jarFile);
        sling = new Sling(this, new SlingLogger(), resProvider, props) {

            // overwrite the loadPropertiesOverride method to inject the
            // command line arguments unconditionally. These will not be
            // persisted in any properties file, though
            @Override
            protected void loadPropertiesOverride(Map<String, String> properties) {
                if (commandLine != null) {
                    properties.putAll(commandLine);
                }

                // Display port number on console, in case HttpService doesn't. This is logged as late as
                // possible in order to pick up defaults from the Sling property files, although system
                // property substitutions will be missed.
                LOGGER.debug("HTTP server port: " + properties.get(PROP_PORT));
            }
        };

	}
	
	public void stopSling() throws Exception {
        if (sling != null) {
            sling.destroy();
            sling = null;
        }
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			if (args.length < 2 ) {
				throw new Exception("Expected arguments: Jar_file sling_home");
			}
			File jarFile = new File(args[0]);
			if (!jarFile.exists()) {
				throw new Exception("Cannot find a jar file at " + args[0]);
			}
			File slingHome = new File(args[1]);
			if (!slingHome.isDirectory()) {
				if (!slingHome.mkdirs()) {
					throw new Exception("Cannot make directory at " + args[1]);
				}
			}
			final SlingMain slingMain = new SlingMain();
			slingMain.startSling(jarFile, slingHome);
		    Runtime.getRuntime().addShutdownHook(new Thread() { 
		    	public void run() { 
		    		try {
		    			LOGGER.debug("Shutdown Hook is running !"); 
		    			slingMain.stopSling();
		    		}
		    		catch(Exception e) {
		    			LOGGER.error("Failed to stop", e);
		    		}
		    	} 
		    }); 
		}
		catch(Exception e) {
			LOGGER.error("Failed to process", e);
		}
	}

	@Override
	public void stopped() {
		LOGGER.info("STOPPED");
	}

	@Override
	public void updated(File tmpFile) {
		LOGGER.info("UPDATED: " + tmpFile.getPath());
	}

}
