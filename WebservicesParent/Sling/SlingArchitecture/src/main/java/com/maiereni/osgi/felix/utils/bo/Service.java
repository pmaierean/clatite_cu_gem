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
public class Service implements Serializable {
	private static final long serialVersionUID = -3944724196510514261L;
	private String id, pid, ranking, bundleName, bundleVersion, bundleSymbolicName;
	private String types;
	private List<Property> props;
	private List<BundleRef> usingBundles;
	private int bundleId;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getRanking() {
		return ranking;
	}
	public void setRanking(String ranking) {
		this.ranking = ranking;
	}
	public String getBundleName() {
		return bundleName;
	}
	public void setBundleName(String bundleName) {
		this.bundleName = bundleName;
	}
	public String getBundleVersion() {
		return bundleVersion;
	}
	public void setBundleVersion(String bundleVersion) {
		this.bundleVersion = bundleVersion;
	}
	public String getBundleSymbolicName() {
		return bundleSymbolicName;
	}
	public void setBundleSymbolicName(String bundleSymbolicName) {
		this.bundleSymbolicName = bundleSymbolicName;
	}
	public String getTypes() {
		return types;
	}
	public void setTypes(String types) {
		this.types = types;
	}
	public List<Property> getProps() {
		return props;
	}
	public void setProps(List<Property> props) {
		this.props = props;
	}
	public int getBundleId() {
		return bundleId;
	}
	public void setBundleId(int bundleId) {
		this.bundleId = bundleId;
	}
	public List<BundleRef> getUsingBundles() {
		return usingBundles;
	}
	public void setUsingBundles(List<BundleRef> usingBundles) {
		this.usingBundles = usingBundles;
	}
}
