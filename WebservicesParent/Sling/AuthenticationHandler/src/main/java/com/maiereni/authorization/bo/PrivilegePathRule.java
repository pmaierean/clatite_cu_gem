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
package com.maiereni.authorization.bo;

import java.io.Serializable;
import java.util.List;

/**
 * @author Petre Maierean
 *
 */
public class PrivilegePathRule implements Serializable {
	private static final long serialVersionUID = -5283830761919134313L;
	private String queryStmt, lang;
	private List<String> priviledges;

	public List<String> getPriviledges() {
		return priviledges;
	}
	public void setPriviledges(List<String> priviledges) {
		this.priviledges = priviledges;
	}
	public String getQueryStmt() {
		return queryStmt;
	}
	public void setQueryStmt(String queryStmt) {
		this.queryStmt = queryStmt;
	}
	public String getLang() {
		return lang;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}

}
