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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maiereni.synchronizer.git.service.bo.ContentPath;
import com.maiereni.synchronizer.git.service.bo.LayoutRule;
import com.maiereni.synchronizer.git.service.bo.LayoutRules;

/**
 * Utility class that builds the list of import tasks by resolving from file system
 * 
 * @author Petre Maierean
 *
 */
class ContentBuilder extends ChangesDetector {
	private static final Logger logger = LoggerFactory.getLogger(ContentBuilder.class);
	private static final long DELTA_RUN = 60 * 60 * 1000L; // 60 minutes
	private static final String MANIFEST_PATH = "META-INF/MANIFEST.MF";
	private static final String ZIP_PATH_TMPL = "initial-content%s";
	private List<ContentBuilderListener> listeners;
	private SlingManifestFileBuilder slingManifestFileBuilder;
	
	private long lastRun;
	
	public ContentBuilder(final JackrabbitSession session) {
		super(session);
		this.lastRun = System.currentTimeMillis() - DELTA_RUN;
		this.slingManifestFileBuilder = new SlingManifestFileBuilder();
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
	 * Detects if the content is to be changed
	 * @param rootPath
	 * @param layoutRules
	 * @return
	 */
	public boolean hasChanges(@Nonnull final String rootPath, @Nonnull final LayoutRules layoutRules) {
		boolean ret = false;
		try {
			File root = getRoot(rootPath);
			for(LayoutRule layoutRule: layoutRules.getLayouts()) {
				if (hasChanges(root, layoutRule)) {
					ret = true;
					break;
				}
			}
		}
		catch(Exception e) { 
			logger.error("Cannot detect changes", e);
		}
		return ret;
	}

	/**
	 * Build archive for the given fsStatus
	 * @param rootPath
	 * @param layoutRules
	 * @return
	 * @throws Exception
	 */
	public File buildZip(@Nonnull final String rootPath, @Nonnull final LayoutRules layoutRules) 
		throws Exception {
		File root = getRoot(rootPath);
		if (!root.isDirectory()) {
			throw new Exception("Unexpected request. The rootPath must point ot a directory");
		}
		List<ContentPath> contents = new ArrayList<ContentPath>();
		File fZip = File.createTempFile("content", ".zip");
		boolean ret = false;
		try (FileOutputStream fos = new FileOutputStream(fZip);
			ZipOutputStream zos = new ZipOutputStream(fos)) {
			for(LayoutRule layoutRule: layoutRules.getLayouts()) {
				String content = addFiles(root, zos, layoutRule);
				if (content != null) {
					ContentPath cp = new ContentPath();
					cp.setName(content);
					cp.setPath(layoutRule.getPath());
					contents.add(cp);
				}
			}
			if (contents.size() > 0) {
				addManifest(zos, layoutRules, contents);
				ret = true;
			}
		}
		finally {
			if (!ret) {
				if (!fZip.delete())
					fZip.deleteOnExit();
			}
			contents.clear();
		}
		return ret ? fZip : null;
	}
	
	protected void addManifest(final ZipOutputStream zos, final LayoutRules layoutRules, final List<ContentPath> contents) throws Exception {
		byte[] buffer = slingManifestFileBuilder.getContent(layoutRules, contents);
		ZipEntry ze = new ZipEntry(MANIFEST_PATH);
        zos.putNextEntry(ze);
        zos.write(buffer);
        zos.closeEntry();
        logger.debug("The manifest file has been written");
	}
	
	private String addFiles(final File root, final ZipOutputStream zos, final LayoutRule rule) throws Exception {
		String ret = null;
		ExclusionPatternFilter filter = new ExclusionPatternFilter(rule);
		File fDir = new File(root, rule.getPath());
		if (filter.accept(fDir)) {
			String jcrPath = rule.getPath();
			String zipPath = String.format(ZIP_PATH_TMPL, jcrPath);
			if (addFiles(fDir, zos, zipPath, filter)) {
				ret = zipPath;
			}
		}		
		return ret;
	}

	private boolean addFiles(final File f, final ZipOutputStream zos, final String zipPath, final ExclusionPatternFilter filter) 
		throws Exception {
		boolean ret = false;
		if (f.isDirectory()) {
			File[] children = f.listFiles(filter);
			if (children != null) {
				for(File child: children) {
					String s = child.getName();
					String fileZipPath = zipPath + "/" + s;
					if (child.isFile()) {
		                byte[] buffer = FileUtils.readFileToByteArray(child);
						ZipEntry ze = new ZipEntry(fileZipPath);
		                zos.putNextEntry(ze);
		                zos.write(buffer);
		                zos.closeEntry();
		                ret = true;
		                if (listeners != null) {
		                	for(ContentBuilderListener listener: listeners) {
		                		listener.addNode(fileZipPath);
		                	}
		                }
					}
					else if (addFiles(child, zos, fileZipPath, filter)){
						ret = true;
					}
				}
			}
		}
		return ret;
	}

	
	private boolean hasChanges(final File root, final LayoutRule rule) throws Exception {
		boolean ret = false;
		ExclusionPatternFilter filter = new ExclusionPatternFilter(rule);
		File fDir = new File(root, rule.getPath());
		if (filter.accept(fDir)) {
			String jcrPath = rule.getPath();
			ret = hasChanges(fDir, jcrPath, filter);
		}
		return ret;
	}
	
	private boolean hasChanges(final File f, final String jcrPath, final ExclusionPatternFilter filter) {
		boolean ret = false;
		if (f.isDirectory()) {
			try {
				if (isDirectoryNode(jcrPath)) {
					File[] children = f.listFiles(filter);
					if (children != null) {
						for(File child: children) {
							String s = child.getName();
							String nodePath = jcrPath + "/" + s;
							if (child.isFile()) {
								if (!matchNodeSize(child, nodePath)) {
									logger.debug("File size doesn't match at " + jcrPath);
									ret = true;
								}
							}
							else if (hasChanges(child, nodePath, filter)){
								ret = true;
							} 
							
							if (ret) {
								break;
							}
						}
					}
				}
				else {
					logger.debug("No directory found at " + jcrPath);
					ret = true;
				}
			}
			catch(Exception e) {
				logger.error("Failed to detect", e);
				ret = true;
			}
		}
		return ret;
	}
	
	protected boolean matchNodeSize(final File f, final String nodePath) {
		boolean ret = false;
		if (isFileNode(nodePath)) {	
			long modif = f.lastModified();
			if (modif > lastRun) {
				long nodeLength = getFileNodeSize(nodePath);
				if (f.length() != nodeLength) {
					ret = true;
				}
			}
		}
		else {
			ret = true;
		}
		return ret;
	}

	private File getRoot(final String rootPath) throws Exception {
		if (StringUtils.isBlank(rootPath)) {
			throw new Exception("The root cannot be blank");
		}
		File root = new File(rootPath);
		if (!root.isDirectory()) {
			throw new Exception("Unexpected request. The rootPath must point ot a directory");
		}
		return root;
	}
}
