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
package com.maiereni.sample.hibernate;

import javax.annotation.Nonnull;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base factory class
 * @author Petre Maierean
 *
 */
public abstract class BaseFactory {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	private Context context;
	
	public BaseFactory() {
		try {
			context = new InitialContext();
		}
		catch(Exception e) {
			logger.error("No naming context");
		}
	}
	
	/**
	 * Gets a configuration setting from system properties or naming context. Throws exception if no value was found for the property
	 * @param key
	 * @return
	 */
	public String getProperty(@Nonnull final String key) throws Exception {
		String ret = getProperty(key, null);
		if (StringUtils.isEmpty(ret))
			throw new Exception("Cannot find a value for property " + key);
		return ret;
	}
	
	/**
	 * Gets a configuration setting from system properties or naming context. Returns the defaultValue if none found
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public String getProperty(@Nonnull final String key, final String defaultValue) {
		String ret = System.getProperty(key, defaultValue);
		if (context != null)
			try {
				ret = (String)context.lookup(key);
			}
			catch(Exception e) {
				logger.error("No value for '" + key + "' in the naming context. Use " + ret);
			}

		return ret;
	}
}
