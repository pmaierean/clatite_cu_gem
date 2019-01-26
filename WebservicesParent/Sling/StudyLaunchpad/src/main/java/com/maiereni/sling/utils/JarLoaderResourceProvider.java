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
package com.maiereni.sling.utils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.launchpad.api.LaunchpadContentProvider;
import org.apache.sling.launchpad.base.impl.ClassLoaderResourceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of the launchpad that looks into the specific JAR for resources
 * 
 * @author Petre Maierean
 *
 */
public class JarLoaderResourceProvider extends ClassLoaderResourceProvider implements LaunchpadContentProvider, Closeable {
	private static final Logger logger = LoggerFactory.getLogger(JarLoaderResourceProvider.class);
	private File jarFile;
	private ZipFile file;
	
	public JarLoaderResourceProvider(final ClassLoader classLoader, final File jarFile) throws Exception {
		super(classLoader);
		this.jarFile = jarFile;
		this.file = new ZipFile(jarFile);
		logger.debug("Use the archive file {}", jarFile.getPath());
	}
	
	@Override
	public Iterator<String> getChildren(String path) {
        if(path.endsWith("/") && path.length() > 1) {
            path = path.substring(0, path.length()-1);
        }
        logger.debug("Find children for path " + path);
        Pattern pathPattern = Pattern.compile("^" + path + "/[^/]+/?$");
        
		List<String> ret = new ArrayList<String>();
		Enumeration<? extends ZipEntry> children = file.entries();
		while(children.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) children.nextElement();
			String name = entry.getName();
			if (name.startsWith(path)) {
				if (pathPattern.matcher(name).matches()) {
					ret.add(name);
				}
			}
		}
		if (ret.size() == 0) {
			return super.getChildren(path);
		}
		logger.debug("Found {} resources", ret.size());
		
		return ret.iterator();
	}
	
	@Override
	public URL getResource(String path) {
		URL ret = null;
		logger.debug("Get resource for path " + path);
		if (hasResource(path)) {
			try {
				String s = "!" + path;
				if (StringUtils.isBlank(path) || path.equals("/"))
					s = "";
				else if (!path.startsWith("/"))
					s = "!/" + path;
				ret = new URL("jar:" + jarFile.toURI().toString() + s); 
			}
			catch(Exception e) {
				logger.error("Failed to make URL for the path", e);
			}
		}
		else {
			ret = super.getResource(path);
		}
		return ret;
	}

	@Override
	public InputStream getResourceAsStream(String path) {
		logger.debug("Get stream for resource for path " + path);
		InputStream ret = null;
		try {
			Enumeration<? extends ZipEntry> children = file.entries();
			while(children.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) children.nextElement();
				String name = entry.getName();
				if (!entry.isDirectory() && name.equals(path)) {
					ret = file.getInputStream(entry);
				}
			}
		}
		catch(Exception e) {
			logger.error("Failed to load resource", e);
		}
		if (ret == null) {
			ret = super.getResourceAsStream(path);
		}
		return ret;
	}

	@Override
	public void close() throws IOException {
		if (file != null) {
			file.close();
		}
	}

	private boolean hasResource(final String path) {
		boolean ret = false;
		Enumeration<? extends ZipEntry> children = file.entries();
		while(children.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) children.nextElement();
			String name = entry.getName();
			if (!entry.isDirectory() && name.equals(path)) {
				ret = true;
				break;
			}
		}
		return ret;
	}
}
