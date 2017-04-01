/**
 * Copyright 2017 Maiereni Software and Consulting Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */
package org.maiereni.aem.admin.bean;

import java.io.Serializable;

/**
 * @author Petre Maierean
 *
 */
public class IdPathPriviledges implements Serializable {
	private static final long serialVersionUID = 1731412074068849843L;
	private String id, path, primaryTypeFilter;
	private String[] privileges;
	
	public IdPathPriviledges(final String id, final String path, final String[] privileges) {
		this.id = id;
		this.path = path;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getPrimaryTypeFilter() {
		return primaryTypeFilter;
	}
	public void setPrimaryTypeFilter(String primaryTypeFilter) {
		this.primaryTypeFilter = primaryTypeFilter;
	}
	public String[] getPrivileges() {
		return privileges;
	}
	public void setPrivileges(String[] privileges) {
		this.privileges = privileges;
	}

}
