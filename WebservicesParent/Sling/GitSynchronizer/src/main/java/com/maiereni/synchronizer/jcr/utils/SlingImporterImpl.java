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
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.jcr.Node;
import javax.jcr.Session;

import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.contentloader.ContentImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maiereni.synchronizer.git.service.SlingImporter;
import com.maiereni.synchronizer.git.service.bo.Change;
import com.maiereni.synchronizer.git.service.bo.FSStatus;
import com.maiereni.synchronizer.git.service.bo.ImportProperties;

/**
 * A service class that imports from file system
 * 
 * @author Petre Maierean
 *
 */
public class SlingImporterImpl implements SlingImporter {
	public static final String SERVICE_NAME = "GitSynchronizerService";
	public static final String USER_NAME = "synchronization-handler";
	private static final Logger logger = LoggerFactory.getLogger(SlingImporterImpl.class);
	private ResourceResolverFactory resourceResolverFactory;
	private ContentImporter contentImporter;
	
	public SlingImporterImpl(
		final ResourceResolverFactory resourceResolverFactory, 
		final ContentImporter contentImporter) {
		this.resourceResolverFactory = resourceResolverFactory;
		this.contentImporter = contentImporter;
	}

	/**
	 * Imports all files and folders from the local file system
	 * @param properties
	 * @throws Exception
	 */
	@Override
	public void importAll(@Nonnull final ImportProperties properties) throws Exception {
		importAll(properties.getRootPath(), properties.getChanges(), properties.getExclusionPattern());
	}

	/**
	 * Imports a delta set of files and folders from the local file system
	 * @param properties
	 * @throws Exception
	 */
	@Override
	public void importDelta(@Nonnull final ImportProperties properties) throws Exception {
		if (properties.getChanges() == null) {
			throw new Exception("The list of changes cannot be null");
		}
		importAll(properties.getRootPath(), properties.getChanges(), properties.getExclusionPattern());
	}
	private static final String DEFAULT_CONTENT_NAME = "content.zip";
	private void importAll(final String path, final List<Change> changes, final String exclusionPath) throws Exception {
		if (StringUtils.isBlank(path)) {
			throw new Exception("Invalid case. The path must be provided");
		}
		ResourceResolver resourceResolver = initSession();
		JackrabbitSession session = null;
		File deltaArchive = null;
		try {
			session = (JackrabbitSession)resourceResolver.adaptTo(Session.class);
			ContentBuilder contentBuilder = new ContentBuilder(session);
			contentBuilder.addListener(new ContentBuilderListener() {
				@Override
				public void addNode(String path) {
					logger.debug("Add node: {}", path);
				}
			});
			FSStatus fsStatus = contentBuilder.buildContentUpdater(path, changes, exclusionPath);
			deltaArchive = contentBuilder.buildZip(path, fsStatus);
			if (deltaArchive != null) {
				try(InputStream is = new FileInputStream(deltaArchive);
					GitContentImportListener listener = new GitContentImportListener()) {
					logger.debug("The ZIP file with changes is ready to import");
					Node root = session.getNode("/");
					contentImporter.importContent(root, DEFAULT_CONTENT_NAME, is, new GitImporterOptions(true), listener);
					logger.debug("Done importing\r\n{}", listener.toString());
					session.save();
					session.refresh(false);
				}
			}
			else {
				logger.debug("Nothing to import");
			}
		}
		finally {
			if (deltaArchive != null)
				if (!deltaArchive.delete())
					deltaArchive.deleteOnExit();
			if (session != null) {
				session.logout();
			}
			resourceResolver.close();
		}		
	}
	
	protected ResourceResolver initSession() throws Exception {
		ResourceResolver resourceResolver = null;
		logger.debug("Open an administrative session");
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(ResourceResolverFactory.USER, USER_NAME);
		properties.put(ResourceResolverFactory.SUBSERVICE, SERVICE_NAME);
		resourceResolver = resourceResolverFactory.getServiceResourceResolver(properties);
		return resourceResolver;
	}
}
