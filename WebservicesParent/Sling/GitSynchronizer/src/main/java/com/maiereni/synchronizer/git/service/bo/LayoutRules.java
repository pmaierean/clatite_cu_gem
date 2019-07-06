/**
 * ================================================================
 *  Copyright (c) 2017-2019 Maiereni Software and Consulting Inc
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
package com.maiereni.synchronizer.git.service.bo;

import java.io.Serializable;
import java.util.List;

/**
 * @author Petre Maierean
 *
 */
public class LayoutRules implements Serializable {
	private static final long serialVersionUID = -8080311822387084984L;
	private String description, symbolicName, vendor, name, version;
	private int bundleStartLevel = 100;
	private boolean bundleStart = true, bundleRefresh = true;
	
	private List<LayoutRule> layouts;
	
	public List<LayoutRule> getLayouts() {
		return layouts;
	}
	public void setLayouts(List<LayoutRule> layouts) {
		this.layouts = layouts;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSymbolicName() {
		return symbolicName;
	}

	public void setSymbolicName(String symbolicName) {
		this.symbolicName = symbolicName;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
	public int getBundleStartLevel() {
		return bundleStartLevel;
	}
	public void setBundleStartLevel(int bundleStartLevel) {
		this.bundleStartLevel = bundleStartLevel;
	}
	public boolean isBundleStart() {
		return bundleStart;
	}
	public void setBundleStart(boolean bundleStart) {
		this.bundleStart = bundleStart;
	}
	public boolean isBundleRefresh() {
		return bundleRefresh;
	}
	public void setBundleRefresh(boolean bundleRefresh) {
		this.bundleRefresh = bundleRefresh;
	}
	
}
