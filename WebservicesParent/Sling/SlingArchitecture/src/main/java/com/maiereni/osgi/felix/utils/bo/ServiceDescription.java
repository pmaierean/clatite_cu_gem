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
import java.util.Arrays;
import java.util.List;

/**
 * @author Petre Maierean
 *
 */
public class ServiceDescription implements Serializable {
	public static final long serialVersionUID = -4564375513413462569L;
	public static final String PROPERTY_ROOT = "provider.root";
	public static final String PROPERTY_NAME = "provider.name";
	public static final String PROPERTY_USE_RESOURCE_ACCESS_SECURITY = "provider.useResourceAccessSecurity";
	public static final String PROPERTY_MODIFIABLE = "provider.modifiable";
	public static final String PROPERTY_ADAPTABLE = "provider.adaptable";
	public static final String PROPERTY_REFRESHABLE = "provider.refreshable";
	public static final String PROPERTY_ATTRIBUTABLE = "provider.attributable";
	public static final String PROPERTY_AUTHENTICATE = "provider.authenticate";
	public static final String AUTH_TYPE_NO = "no";
	public static final String AUTH_TYPE_LAZY = "lazy";
	public static final String AUTH_TYPE_REQUIRED = "required";
	public static final List<String> PROPS = Arrays.asList(new String[] {
		PROPERTY_ROOT, PROPERTY_NAME, PROPERTY_USE_RESOURCE_ACCESS_SECURITY, PROPERTY_MODIFIABLE, PROPERTY_ADAPTABLE, PROPERTY_REFRESHABLE, PROPERTY_ATTRIBUTABLE, PROPERTY_AUTHENTICATE  
	});
	private String root, name, authType=AUTH_TYPE_NO, bundleSymbolicName;
	private int bundleId, serviceId;
	private boolean useResourceAccessSecurity, attributable, adaptable, modifiable, refreshable;
	private ValidationResponse validationResponse;
	
	public String getRoot() {
		return root;
	}
	public void setRoot(String root) {
		this.root = root;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAuthType() {
		return authType;
	}
	public void setAuthType(String authType) {
		this.authType = authType;
	}
	public boolean isUseResourceAccessSecurity() {
		return useResourceAccessSecurity;
	}
	public void setUseResourceAccessSecurity(boolean useResourceAccessSecurity) {
		this.useResourceAccessSecurity = useResourceAccessSecurity;
	}
	public boolean isAttributable() {
		return attributable;
	}
	public void setAttributable(boolean attributable) {
		this.attributable = attributable;
	}
	public boolean isAdaptable() {
		return adaptable;
	}
	public void setAdaptable(boolean adaptable) {
		this.adaptable = adaptable;
	}
	public boolean isModifiable() {
		return modifiable;
	}
	public void setModifiable(boolean modifiable) {
		this.modifiable = modifiable;
	}
	public boolean isRefreshable() {
		return refreshable;
	}
	public void setRefreshable(boolean refreshable) {
		this.refreshable = refreshable;
	}
	public int getBundleId() {
		return bundleId;
	}
	public void setBundleId(int bundleId) {
		this.bundleId = bundleId;
	}
	public String getBundleSymbolicName() {
		return bundleSymbolicName;
	}
	public void setBundleSymbolicName(String bundleSymbolicName) {
		this.bundleSymbolicName = bundleSymbolicName;
	}
	public int getServiceId() {
		return serviceId;
	}
	public void setServiceId(int serviceId) {
		this.serviceId = serviceId;
	}
	public ValidationResponse getValidationResponse() {
		return validationResponse;
	}
	public void setValidationResponse(ValidationResponse validationResponse) {
		this.validationResponse = validationResponse;
	}
}
