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
package com.maiereni.osgi.felix.utils.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.maiereni.sling.info.Bundle;
import com.maiereni.sling.info.Model;
import com.maiereni.sling.info.Source;

/**
 * @author Petre Maierean
 *
 */
public class SourcesLookup implements PostProcessor {
	private static final Logger logger = LoggerFactory.getLogger(SourcesLookup.class);
	private Map<String, Source> nameSource;
	
	public SourcesLookup(final String sourceFiles) {
		nameSource = init(sourceFiles);
	}
	
	@Override
	public Model updataModel(Model model) throws Exception {
		Model ret = model;
		for(Bundle bundle: ret.getBundles()) {
			String name = bundle.getName();
			if (nameSource.containsKey(name)) {
				bundle.setSource(nameSource.get(name));
				continue;
			}
			name = name.replaceAll("\\x2E", "-");
			if (nameSource.containsKey(name)) {
				bundle.setSource(nameSource.get(name));
				logger.debug("Found source for " + bundle.getName());
				continue;
			}
		}
		return ret;
	}

	private Map<String, Source> init(final String sourceFiles) {
		Map<String, Source> ret = new HashMap<String, Source>();
		if (StringUtils.isNoneBlank(sourceFiles)) {
			String[] sp = sourceFiles.split(",");
			for(String sourceFile: sp) {
				try {
					Map<String, Source> sources = readFromXMLFile(new File(sourceFile));
					ret.putAll(sources);
				}
				catch(Exception e) {
					logger.error("Failed to read from file " + sourceFile, e);
				}
			}
		}
		return ret;
	}
	// http://svn.apache.org/viewvc/felix/releases/org.apache.felix.utils-1.0.0/
	private Map<String, Source> readFromXMLFile(final File f) throws Exception {
		Map<String, Source> ret = new HashMap<String, Source>();
		try(InputStream is = new FileInputStream(f)) {
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = documentBuilder.parse(is);
			XPath xpath = XPathFactory.newInstance().newXPath();
			NodeList nl = (NodeList) xpath.evaluate("//a", document, XPathConstants.NODESET);
			int max = nl.getLength();
			for(int i=0; i<max; i++) {
				Element el = (Element)nl.item(i);
				Source source = new Source();
				String url = el.getAttribute("href");
				source.setUrl(url);
				if (url.contains("git")) {
					source.setRepositoryType("git");
					source.setVersion("master");
				}
				else if (url.contains("svn")) {
					source.setRepositoryType("svn");
					source.setVersion("master");					
				}
				String name = el.getTextContent();
				ret.put(name, source);
			}
		}
		return ret;
	}
}
