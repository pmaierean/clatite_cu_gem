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
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.Nonnull;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.api.JackrabbitSession;

import com.maiereni.synchronizer.git.service.bo.Change;
import com.maiereni.synchronizer.git.service.bo.FSStatus;

/**
 * Utility class that builds the list of import tasks by resolving from file system
 * 
 * @author Petre Maierean
 *
 */
class ContentBuilder extends ChangesDetector {
	private List<ContentBuilderListener> listeners;
	
	public ContentBuilder(final JackrabbitSession session) {
		super(session);
	}
	
	public void addListener(final ContentBuilderListener listener) {
		if (listener != null) {
			if (listeners == null) {
				listeners = new ArrayList<ContentBuilderListener>();
			}
			listeners.add(listener);
		}
	}
	
	/**
	 * Build a hierarchy of nodes having change status
	 * @param rootPath
	 * @param changes
	 * @param exclusionPath
	 * @return
	 * @throws Exception
	 */
	public FSStatus buildContentUpdater(
		@Nonnull final String rootPath,
		@Nonnull final List<Change> changes,
		@Nonnull final String exclusionPath)
		throws Exception {
		if (StringUtils.isBlank(rootPath)) {
			throw new Exception("The root cannot be blank");
		}
		File root = new File(rootPath);
		if (!root.isDirectory()) {
			throw new Exception("Unexpected request. The rootPath must point ot a directory");
		}
		ExclusionPatternFilter filter = new ExclusionPatternFilter(exclusionPath);
		RelativePathProvider relativePathProvider = new RelativePathProvider(root);
		return buildFSStatus(root, filter, relativePathProvider, changes);
	}

	/**
	 * Build archive for the given fsStatus
	 * @param rootPath
	 * @param fsStatus
	 * @return
	 * @throws Exception
	 */
	public File buildZip(@Nonnull final String rootPath, @Nonnull final FSStatus fsStatus) 
		throws Exception {
		if (StringUtils.isBlank(rootPath)) {
			throw new Exception("The root cannot be blank");
		}
		File root = new File(rootPath);
		if (!root.isDirectory()) {
			throw new Exception("Unexpected request. The rootPath must point ot a directory");
		}
		boolean ret = false;
		File fZip = File.createTempFile("content", ".zip");
		try (FileOutputStream fos = new FileOutputStream(fZip);
			ZipOutputStream zos = new ZipOutputStream(fos)) {
			ret = addNodes(root, zos, fsStatus);
		}
		finally {
			if (!ret) {
				if (!fZip.delete())
					fZip.deleteOnExit();
			}
		}
		return ret ? fZip : null;
	}
	
	private boolean addNodes(final File root, final ZipOutputStream zos, final FSStatus fsStatus) throws Exception {
		boolean ret = false;
		for(FSStatus node : fsStatus.getNodes()) {
			if (node.getChange() != null) {
				if (node.isFile()) {
	                File fNode = new File(root, node.getPath());
	                if (!fNode.isFile()) {
	                	throw new Exception("Cannot find file " + fNode.getPath());
	                }
	                byte[] buffer = FileUtils.readFileToByteArray(fNode);
					ZipEntry ze = new ZipEntry(node.getPath());
	                zos.putNextEntry(ze);
	                zos.write(buffer);
	                zos.closeEntry();
	                ret = true;
	                if (listeners != null) {
	                	for(ContentBuilderListener listener: listeners) {
	                		listener.addNode(node.getPath());
	                	}
	                }
				}
				else {
					if (addNodes(root, zos, node))
						ret = true;
				}
			}
		}		
		return ret;
	}
}
