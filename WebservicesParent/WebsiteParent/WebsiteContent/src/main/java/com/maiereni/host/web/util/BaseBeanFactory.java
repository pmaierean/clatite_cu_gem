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
package com.maiereni.host.web.util;

import javax.annotation.Nonnull;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Base class for bean factories
 * @author Petre Maierean
 *
 */
public abstract class BaseBeanFactory implements ApplicationContextAware {
	protected Context context;
	protected ApplicationContext applicationContext;
	
	public BaseBeanFactory() throws Exception {
		context = new InitialContext();
	}
	/**
	 * Get the value of a property. Try JNDI first but fall back on the System properties though 
	 * @param key the name of the property
	 * @param def default value
	 * @return
	 */
	public String getProperty(@Nonnull final String key, final String def) {
		String ret = null;
		try {
			ret = getContextProperty(key, String.class);
		}
		catch(Throwable e) {
		}
		if (ret == null) {
			ret = getJVMProperty(key, def);
		}
		return ret;
	}
	/**
	 * Get the value of a System property
	 * @param key the name of the property
	 * @param def default value
	 * @return
	 */
	public String getJVMProperty(@Nonnull final String key, final String def) {
		return System.getProperty(key, def);
	}
	
	/**
	 * Resolve a context property for the key and cast it to the expected type
	 * @param key
	 * @param clazz
	 * @return
	 */
	public <T> T getContextProperty(@Nonnull final String key, @Nonnull final Class<T> clazz) {
		T ret = null;
		if (isContextProperty(key, clazz)) {
			try {
				Object o = context.lookup(key);
				ret = clazz.cast(o);
			}
			catch(Exception e) {
				
			}
		}
		return ret;
	}
	
	/**
	 * Check if there is a context property for the key and it is of the given type
	 * @param key
	 * @param clazz
	 * @return
	 */
	public <T> boolean isContextProperty(@Nonnull final String key, @Nonnull final Class<T> clazz) {
		boolean ret = false;
		try {
			Object o = context.lookup(key);
			if (o != null && clazz.isInstance(o)) {
				ret = true;
			}
		}
		catch(Exception e) {
			
		}
		return ret;
	}

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
