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
package com.maiereni.sling.sources.bo;

import java.io.Serializable;

/**
 * @author Petre Maierean
 *
 */
public class Arguments implements Serializable {
	private static final long serialVersionUID = 2192265245427007888L;
	private String modelFile, projectsRoot, updateLogFile, projectName;
	public String getModelFile() {
		return modelFile;
	}
	public void setModelFile(String modelFile) {
		this.modelFile = modelFile;
	}
	public String getProjectsRoot() {
		return projectsRoot;
	}
	public void setProjectsRoot(String projectsRoot) {
		this.projectsRoot = projectsRoot;
	}
	public String getUpdateLogFile() {
		return updateLogFile;
	}
	public void setUpdateLogFile(String updateLogFile) {
		this.updateLogFile = updateLogFile;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

}
