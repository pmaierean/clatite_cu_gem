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
package com.maiereni.osgi.felix.utils.bo;

import java.io.Serializable;
import java.util.List;

/**
 * @author Petre Maierean
 *
 */
public class BundleMapping implements Serializable {
	private static final long serialVersionUID = -2373588900181746867L;
	private String bundleName, guid;
	private List<ComponentMapping> components;
	private List<ServiceMapping> services;
	public String getBundleName() {
		return bundleName;
	}
	public void setBundleName(String bundleName) {
		this.bundleName = bundleName;
	}
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	public List<ComponentMapping> getComponents() {
		return components;
	}
	public void setComponents(List<ComponentMapping> components) {
		this.components = components;
	}
	public List<ServiceMapping> getServices() {
		return services;
	}
	public void setServices(List<ServiceMapping> services) {
		this.services = services;
	}
}
