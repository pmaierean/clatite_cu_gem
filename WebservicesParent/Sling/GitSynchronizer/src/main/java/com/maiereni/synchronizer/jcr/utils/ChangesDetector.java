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

import java.io.File;
import java.util.List;
import java.util.TreeSet;

import javax.jcr.Node;
import javax.jcr.nodetype.NodeType;

import org.apache.jackrabbit.api.JackrabbitSession;

import com.maiereni.synchronizer.git.service.bo.Change;
import com.maiereni.synchronizer.git.service.bo.ChangeType;
import com.maiereni.synchronizer.git.service.bo.FSStatus;

/**
 * @author Petre Maierean
 *
 */
abstract class ChangesDetector {
	private JackrabbitSession session;

	public ChangesDetector(final JackrabbitSession session) {
		this.session = session;
	}
	
	protected FSStatus buildFSStatus( final File f, 
			final ExclusionPatternFilter filter, 
			final RelativePathProvider rpp,
			final List<Change> changes) {
		FSStatus ret = null;
		if (f != null) {
			ret = new FSStatus();
			ret.setName(f.getName());
			String relativePath = rpp.getRelativePath(f);
			ret.setPath(relativePath);
			Change change = null;
			if (f.isDirectory()) {
				if (!isDirectoryNode(relativePath)) {
					change = new Change();
					change.setChangeType(ChangeType.ADD);
					change.setPathNew(relativePath);
				}

				TreeSet<FSStatus> c = new TreeSet<FSStatus>();
				ret.setNodes(c);
				File[] children = f.listFiles(filter);
				if (children != null) {
					for(File child: children) {
						FSStatus cs = buildFSStatus(child, filter, rpp, changes);
						if (cs != null) {
							c.add(cs);
							if (change == null && cs.getChange() != null) {
								change = new Change();
								change.setChangeType(ChangeType.MODIFY);
								change.setPathNew(relativePath);
							}
						}
					}
				}
			}
			else {
				ret.setFile(true);
				if (!isNotDirectoryNode(relativePath)) {
					change = new Change();
					change.setChangeType(ChangeType.ADD);
					change.setPathNew(relativePath);
				}
				else if (changes != null) {
					change = findChange(changes, relativePath);
				}
			}
			ret.setChange(change);
		}
		return ret;
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
	
	protected boolean isDirectoryNode(final String path) {
		boolean ret = false;
		try {
			Node n = session.getNode(path);
			NodeType nt = n.getPrimaryNodeType();
			ret = nt.getName().equals(NodeType.NT_FOLDER);
		}
		catch(Exception e) {
			
		}
		return ret;
	}
	
	protected boolean isNotDirectoryNode(final String path) {
		boolean ret = false;
		try {
			Node n = session.getNode(path);
			NodeType nt = n.getPrimaryNodeType();
			ret = !nt.getName().equals(NodeType.NT_FOLDER);
		}
		catch(Exception e) {
			
		}
		return ret;
	}
	
}
