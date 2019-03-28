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
package com.maiereni.ehcache2.jgroup.jaxrs2.application;

import java.util.logging.Level;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.stereotype.Component;
/**
 * @author Petre Maierean
 *
 */
@ApplicationPath("/api")
@Component
public class SamplerApplication extends ResourceConfig {
	private static final Logger logger = LoggerFactory.getLogger(SamplerApplication.class);
	
    public SamplerApplication() {
    	logger.debug("Get properties");
    	java.util.logging.Logger jerseyLogger = java.util.logging.Logger.getLogger("Jersey");
    	jerseyLogger.addHandler(new SLF4JBridgeHandler());
    	LoggingFeature loggingFeature = new LoggingFeature(jerseyLogger, Level.FINE, LoggingFeature.Verbosity.PAYLOAD_ANY, 10000);
    
    	register(loggingFeature);
        packages("com.fasterxml.jackson.jaxrs.json, com.maiereni.ehcache2.jgroup.jaxrs2.rs");
    }
    
    
    
}
