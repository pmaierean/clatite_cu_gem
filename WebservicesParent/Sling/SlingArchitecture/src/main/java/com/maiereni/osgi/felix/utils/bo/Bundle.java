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
public class Bundle implements Serializable {
	private static final long serialVersionUID = -3700231704419042205L;
	private String id, name, state, symbolicName, category, version;
	private List<Property> props;
	private boolean fragment;
	private int stateRaw;

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
	public String getSymbolicName() {
		return symbolicName;
	}
	public void setSymbolicName(String symbolicName) {
		this.symbolicName = symbolicName;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public boolean isFragment() {
		return fragment;
	}
	public void setFragment(boolean fragment) {
		this.fragment = fragment;
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
}
