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
package com.maiereni.oak.bo;

import java.io.Serializable;
import java.util.HashMap;

/**
 * The properties for the repository
 * 
 * @author Petre Maierean
 *
 */
public class RepositoryProperties extends HashMap<String, Object> implements Serializable {
	private static final long serialVersionUID = -2131787866133722575L;
	private String repositoryPath, adminUser, adminPassword;
	public String getRepositoryPath() {
		return repositoryPath;
	}
	public void setRepositoryPath(String repositoryPath) {
		this.repositoryPath = repositoryPath;
	}
	public String getAdminUser() {
		return adminUser;
	}
	public void setAdminUser(String adminUser) {
		this.adminUser = adminUser;
	}
	public String getAdminPassword() {
		return adminPassword;
	}
	public void setAdminPassword(String adminPassword) {
		this.adminPassword = adminPassword;
	}
	
	/**
	 * Get the property as a string
	 * @param key
	 * @param defValue
	 * @return
	 */
	public String getAsString(final String key, final String defValue) {
		String ret = defValue;
		if (isProperty(key, String.class)) {
			ret = get(key, String.class);
		}
		return ret;
	}
	/**
	 * Get a property of a given type
	 * @param key
	 * @param clazz
	 * @return
	 */
	public <T> T get(final String key, final Class<T> clazz) {
		T ret = null;
		Object o = get(key);
		if (o != null) {
			if (clazz.isInstance(o)) {
				ret = clazz.cast(o);
			}
			else
				throw new RepositoryClassCastException("Cannot cast " + o.getClass().getName() + " as " + clazz.getName());
		}
		return ret;
	}
	
	/**
	 * Check if it has a property and it is of the given type
	 * @param key
	 * @param clazz
	 * @return
	 */
	public <T> boolean isProperty(final String key, final Class<T> clazz) {
		boolean ret = false;
		Object o = get(key);
		if (o != null && clazz.isInstance(o)) {
			ret = true;
		}
		
		return ret;
	}
}
