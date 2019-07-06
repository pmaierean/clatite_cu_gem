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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.jcr.Session;

import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maiereni.synchronizer.git.service.SlingImporter;
import com.maiereni.synchronizer.git.service.bo.Change;
import com.maiereni.synchronizer.git.service.bo.ImportProperties;
import com.maiereni.synchronizer.git.service.bo.LayoutRules;
import com.maiereni.synchronizer.git.service.bo.SlingProperties;

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
	private BundleInstaller bundleInstaller;

	public SlingImporterImpl(
		final ResourceResolverFactory resourceResolverFactory, 
		final SlingProperties slingProperties) throws Exception {
		this.resourceResolverFactory = resourceResolverFactory;
		bundleInstaller = new BundleInstaller(slingProperties);
	}

	/**
	 * Imports all files and folders from the local file system
	 * @param properties
	 * @throws Exception
	 */
	@Override
	public void importAll(@Nonnull final ImportProperties properties) throws Exception {
		importAll(properties.getRootPath(), properties.getChanges(), properties.getLayoutRules());
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
		importAll(properties.getRootPath(), properties.getChanges(), properties.getLayoutRules());
	}

	private void importAll(final String path, final List<Change> changes, final LayoutRules layoutRules) throws Exception {
		if (StringUtils.isBlank(path)) {
			throw new Exception("Invalid case. The path must be provided");
		}
		ResourceResolver resourceResolver = initSession();
		JackrabbitSession session = null;
		File bundleArchive = null;
		try {
			session = (JackrabbitSession)resourceResolver.adaptTo(Session.class);
			ContentBuilder contentBuilder = new ContentBuilder(session);
			contentBuilder.addListener(new ContentBuilderListener() {
				@Override
				public void addNode(String path) {
					logger.debug("Add node: {}", path);
				}
			});
			if (contentBuilder.hasChanges(path, layoutRules)) {
				logger.debug("There are changes at {} to import", path);
				bundleArchive = contentBuilder.buildZip(path, layoutRules);
				bundleInstaller.installBundle(layoutRules, bundleArchive);
			}
		}
		finally {
			if (bundleArchive != null)
				logger.debug("The Zip file is available at {}", bundleArchive.getPath());
			/*
			if (deltaArchive != null)
				if (!deltaArchive.delete())
					deltaArchive.deleteOnExit();
			*/
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
