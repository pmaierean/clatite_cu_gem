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

/**
 * @author Petre Maierean
 *
 */
public class Configuration implements Serializable {
	private static final long serialVersionUID = 2702533953324073978L;
	private GitProperties gitProperties;
	private SlingProperties slingProperties;
	public GitProperties getGitProperties() {
		return gitProperties;
	}
	public void setGitProperties(GitProperties gitProperties) {
		this.gitProperties = gitProperties;
	}
	public SlingProperties getSlingProperties() {
		return slingProperties;
	}
	public void setSlingProperties(SlingProperties slingProperties) {
		this.slingProperties = slingProperties;
	}
}
