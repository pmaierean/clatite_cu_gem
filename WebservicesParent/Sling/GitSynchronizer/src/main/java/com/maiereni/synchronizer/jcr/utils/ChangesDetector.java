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
package com.maiereni.synchronizer.jcr.utils;

import java.io.InputStream;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.nodetype.NodeType;

import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.oak.commons.IOUtils;

import com.maiereni.synchronizer.git.service.bo.Change;
import com.maiereni.synchronizer.git.service.bo.ChangeType;

/**
 * @author Petre Maierean
 *
 */
abstract class ChangesDetector {
	private JackrabbitSession session;

	public ChangesDetector(final JackrabbitSession session) {
		this.session = session;
	}
	
	protected Change findChange(final List<Change> changes, final String path) {
		Change ret = null;
		if (changes != null) {
			for(Change change: changes) {
				if (change.getPathNew().equals(path)) {
					ret = change;
					break;
				}
			}
		}
		else {
			ret = new Change();
			ret.setChangeType(ChangeType.ADD);
			ret.setPathNew(path);
		}
		return ret;
	}
	
	protected boolean isDirectoryNode(final String path) throws Exception {
		boolean ret = false;
		Node n = session.getNode(path);
		NodeType nt = n.getPrimaryNodeType();
		ret = nt.getName().equals(NodeType.NT_FOLDER);
		return ret;
	}
	
	protected boolean isNotDirectoryNode(final String path) {
		boolean ret = false;
		try {
			Node n = session.getNode(path);
			if (n != null) {
				NodeType nt = n.getPrimaryNodeType();
				ret = !nt.getName().equals(NodeType.NT_FOLDER);
				n.getProperties("");
			}
		}
		catch(Exception e) {
			
		}
		return ret;
	}

	protected boolean isFileNode(final String path) {
		boolean ret = false;
		try {
			Node n = session.getNode(path);
			if (n != null) {
				NodeType nt = n.getPrimaryNodeType();
				ret = nt.getName().equals(NodeType.NT_FILE);
			}
		}
		catch(Exception e) {
			
		}
		return ret;
	}
	
	protected int getFileNodeSize(final String path) {
		int ret = -1;
		try {
			Node n = session.getNode(path);
			if (n != null) {
				NodeType nt = n.getPrimaryNodeType();
				if (nt.getName().equals(NodeType.NT_FILE)) {
					Node jcrContent = n.getNode("jcr:content");
					try (InputStream content = jcrContent.getProperty("jcr:data").getBinary().getStream()) {
						byte[] buffer = IOUtils.readBytes(content);
						ret = buffer.length;
					}
				}
			}
		}
		catch(Exception e) {
			
		}
		return ret;
	}
}
