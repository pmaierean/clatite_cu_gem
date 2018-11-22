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
package com.maiereni.host.web.jaxrs.service.bo;

import java.util.HashMap;

import javax.annotation.Nonnull;

/**
 * Properties for the repository
 * @author Petre Maierean
 *
 */
public class RepositoryProperties extends HashMap<String, Object> {
	private static final long serialVersionUID = 6489619944300449976L;

	/**
	 * Get a property of a given type
	 * @param key
	 * @param clazz
	 * @return
	 */
	public <T> T get(@Nonnull final String key, final T defaultValue, @Nonnull final Class<T> clazz) {
		T ret = defaultValue;
		Object o = get(key);
		if (o != null) {
			if (clazz.isInstance(o)) {
				ret = clazz.cast(o);
			}
			else
				throw new ClassCastException("Cannot cast " + o.getClass().getName() + " as " + clazz.getName());
		}
		return ret;
	}
	
	/**
	 * Check if it has a property and it is of the given type
	 * @param key
	 * @param clazz
	 * @return
	 */
	public <T> boolean isProperty(@Nonnull final String key, @Nonnull final Class<T> clazz) {
		boolean ret = false;
		Object o = get(key);
		if (o != null && clazz.isInstance(o)) {
			ret = true;
		}
		
		return ret;
	}
}
