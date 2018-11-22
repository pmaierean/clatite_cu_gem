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
package com.maiereni.oak;

import javax.annotation.Nonnull;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Petre Maierean
 *
 */
public class PropertiesReader {
	private static final Logger logger = LoggerFactory.getLogger(PropertiesReader.class);
	private Context context;
	
	public PropertiesReader() {
		try {
			context = new InitialContext();
		}
		catch(Exception e) {
			logger.error("Cannot load context", e);
		}
	}
	/**
	 * Lookup for a String property
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public String getProperty(@Nonnull final String key, @Nonnull final String defaultValue) {
		String ret = System.getProperty(key, defaultValue);
		try {
			Object o = context.lookup(key);
			if (o != null) {
				ret = o.toString();
			}
		}
		catch(Exception e) {
			
		}
		return ret;
	}

	/**
	 * Lookup for a property of a given type
	 * @param key
	 * @param defaultValue
	 * @param clazz
	 * @return
	 */
	public <T> T getProperty(@Nonnull final String key, @Nonnull final T defaultValue, @Nonnull final Class<T> clazz) {
		T ret = defaultValue;
		try {
			Object o = context.lookup(key);
			if (o != null) {
				ret = clazz.cast(o);
			}
		}
		catch(Exception e) {
			logger.error("Failed to cast", e);
		}
		return ret;
	}

}
