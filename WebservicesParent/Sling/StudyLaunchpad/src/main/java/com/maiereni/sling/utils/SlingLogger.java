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

import org.apache.felix.framework.Logger;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.slf4j.LoggerFactory;

/**
 * @author Petre Maierean
 *
 */
public class SlingLogger extends Logger {
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SlingLogger.class);

    @Override
    protected void doLog(Bundle bundle, @SuppressWarnings("rawtypes") ServiceReference sr, int level, String msg, Throwable throwable) {

        // unwind throwable if it is a BundleException
        if ((throwable instanceof BundleException) && (((BundleException) throwable).getNestedException() != null)) {
            throwable = ((BundleException) throwable).getNestedException();
        }

        final StringBuilder sb = new StringBuilder();
        if (sr != null) {
            sb.append("SvcRef ");
            sb.append(sr);
            sb.append(" ");
        } else if (bundle != null) {
            sb.append("Bundle '");
            sb.append(String.valueOf(bundle.getBundleId()));
            sb.append("' ");
        }
        sb.append(msg);
        if ( throwable != null ) {
            sb.append(" (");
            sb.append(throwable);
            sb.append(")");
        }
        final String s = sb.toString();

        switch (level) {
            case LOG_DEBUG:
            	logger.debug("DEBUG: " + s);
                break;
            case LOG_INFO:
            	logger.info("INFO: " + s, throwable);
                break;
            case LOG_WARNING:
            	logger.warn("WARNING: " + s, throwable);
                break;
            case LOG_ERROR:
            	logger.error("ERROR: " + s, throwable);
                break;
            default:
            	logger.warn("UNKNOWN[" + level + "]: " + s);
        }
    }
}
