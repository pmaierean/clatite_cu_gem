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

/**
 * Base class for bean factories
 * @author Petre Maierean
 *
 */
public abstract class BaseBeanFactory {
	private Context context;
	
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
			Object o = context.lookup(key);
			if (o != null)
				ret = o.toString();
		}
		catch(Exception e) {
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
}
