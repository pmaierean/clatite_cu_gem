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
public class ImportProperties implements Serializable {
	private static final long serialVersionUID = 422245225730350636L;
	private String rootPath;
	private List<Change> changes;
	private LayoutRules layoutRules;
	
	public String getRootPath() {
		return rootPath;
	}
	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}
	public List<Change> getChanges() {
		return changes;
	}
	public void setChanges(List<Change> changes) {
		this.changes = changes;
	}
	public LayoutRules getLayoutRules() {
		return layoutRules;
	}
	public void setLayoutRules(LayoutRules layoutRules) {
		this.layoutRules = layoutRules;
	}
}
