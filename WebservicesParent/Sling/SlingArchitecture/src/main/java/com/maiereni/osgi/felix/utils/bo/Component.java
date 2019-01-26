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
public class Component implements Serializable {
	private static final long serialVersionUID = 4424513487212757958L;
	private String id, name, state, pid, configurable;
	private int bundleId, stateRaw;
	private List<Property> props;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public int getBundleId() {
		return bundleId;
	}
	public void setBundleId(int bundleId) {
		this.bundleId = bundleId;
	}
	public int getStateRaw() {
		return stateRaw;
	}
	public void setStateRaw(int stateRaw) {
		this.stateRaw = stateRaw;
	}
	public List<Property> getProps() {
		return props;
	}
	public void setProps(List<Property> props) {
		this.props = props;
	}
	public String getConfigurable() {
		return configurable;
	}
	public void setConfigurable(String configurable) {
		this.configurable = configurable;
	}
}
