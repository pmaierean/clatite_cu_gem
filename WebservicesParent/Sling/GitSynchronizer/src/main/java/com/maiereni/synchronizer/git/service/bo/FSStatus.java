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
import java.util.TreeSet;

/**
 * The status of the file system
 * @author Petre Maierean
 */
public class FSStatus implements Comparable<FSStatus>, Serializable {
	private static final long serialVersionUID = -4820859286161229700L;
	private String name, path;
	private TreeSet<FSStatus> nodes;
	private boolean isFile;
	private Change change;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isFile() {
		return isFile;
	}
	public void setFile(boolean isFile) {
		this.isFile = isFile;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public TreeSet<FSStatus> getNodes() {
		return nodes;
	}
	public void setNodes(TreeSet<FSStatus> nodes) {
		this.nodes = nodes;
	}
	public Change getChange() {
		return change;
	}
	public void setChange(Change change) {
		this.change = change;
	}
	@Override
	public int compareTo(FSStatus o) {
		int ret = 0;
		if (o != null) {
			FSStatus stat = (FSStatus)o;
			ret = stat.name.compareTo(name);
		}
		return ret;
	}
	
}
