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
package com.maiereni.osgi.felix.utils;

import java.io.File;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.maiereni.sling.info.Bundle;
import com.maiereni.sling.info.Model;
import com.maiereni.sling.info.Source;

/**
 * @author Petre Maierean
 *
 */
public abstract class SourcesResolver {
	private static final String BUNDLE_JAR_TEMPLATE = "felix/bundle%s/version0.0/bundle.jar";
	private static final Logger logger = LoggerFactory.getLogger(SlingDescriber.class);
	private DocumentBuilder documentBuilder;
	private XPathFactory xpathFactory;
	
	/**
	 * Update the source projects for the bundles 
	 * 
	 * @param model
	 * @throws Exception
	 */
	protected void updateSources(final Model model, final String slingHome) throws Exception {
		documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		xpathFactory = XPathFactory.newInstance();
		if (StringUtils.isNotBlank(slingHome)) {
			File fSlingHome = new File(slingHome);
			if (fSlingHome.isDirectory()) {
				logger.debug("Start detecting the source code for the bundles");
				for(Bundle bundle: model.getBundles()) {
					if (bundle.getId().equals("0")) {
						Source source = new Source();
						source.setGroupId("org.apache.felix");
						source.setArtifactId("org.apache.felix.framework");
						source.setVersion(bundle.getVersion());
						source.setUrl("http://svn.apache.org/repos/asf/felix/releases/org.apache.felix.framework-6.0.1");
						source.setRepositoryType("svn");
						bundle.setSource(source);
					}
					else {
						String rp = String.format(BUNDLE_JAR_TEMPLATE, bundle.getId());
						File f = new File(fSlingHome, rp);
						if (f.isFile()) {
							ArtifactSCM artifact = getSCM(bundle.getName(), f);
							bundle.setSource(artifact.source);
						}
					}
				}
			}
		}
	}
	
	private static final String SVN = "scm:svn:";
	private static final String GIT = "scm:git:";
	private ArtifactSCM getSCM(final String bundleName, final File f) throws Exception {
		ArtifactSCM ret = null;
		try (JarFile jf = new JarFile(f)) {
			Enumeration<JarEntry> en = jf.entries();
			while(en.hasMoreElements()) {
				JarEntry entry = en.nextElement();
				String name = entry.getName();
				if (name.endsWith("pom.xml")) {
					if (ret != null) {
						logger.error("duplicate found in " + f.getPath() + " -> " + ret.path + " and " + name);
						if (!name.contains(bundleName)) {
							logger.error("ignore");
							continue;
						}
					}
					ret = new ArtifactSCM();
					ret.path = name;
					try(InputStream is = jf.getInputStream(entry)) {
						Document document = documentBuilder.parse(is);
						XPath xpath = xpathFactory.newXPath();
						ret.source.setGroupId((String)xpath.evaluate("//project/groupId", document, XPathConstants.STRING));
						ret.source.setVersion((String)xpath.evaluate("//project/version", document, XPathConstants.STRING));
						if (StringUtils.isBlank(ret.source.getGroupId())) {
							ret.source.setGroupId((String)xpath.evaluate("//project/parent/groupId", document, XPathConstants.STRING));							
							if (StringUtils.isBlank(ret.source.getVersion())) {
								ret.source.setVersion((String)xpath.evaluate("//project/parent/version", document, XPathConstants.STRING));							
							}							
						}
						ret.source.setArtifactId((String)xpath.evaluate("//project/artifactId", document, XPathConstants.STRING));
						String scm = (String)xpath.evaluate("//scm/connection", document, XPathConstants.STRING);
						scm = scm.replace('\r', ' ');
						scm = scm.replace('\n', ' ');
						scm = scm.trim();
						if (scm.startsWith(SVN)) {
							ret.source.setRepositoryType("svn");
							ret.source.setUrl(scm.substring(SVN.length()));
						}
						else if (scm.startsWith(GIT)) {
							ret.source.setRepositoryType("git");
							ret.source.setUrl(scm.substring(GIT.length()));
						}
						else if (ret.source.getGroupId().equals("org.apache.servicemix.specs")) {
							ret.source.setUrl("https://github.com/apache/servicemix-specs.git");
							ret.source.setRepositoryType("git");
						}
						else if (ret.source.getGroupId().equals("org.apache.jackrabbit") && ret.source.getArtifactId().startsWith("jackrabbit")) {
							ret.source.setUrl("http://svn.apache.org/repos/asf/jackrabbit");
							ret.source.setRepositoryType("svn");
						}						
						else if (ret.source.getGroupId().equals("org.apache.jackrabbit") && ret.source.getArtifactId().startsWith("oak")) {
							ret.source.setUrl("http://svn.apache.org/repos/asf/jackrabbit/oak");
							ret.source.setRepositoryType("svn");
						}
						else if (ret.source.getGroupId().equals("io.dropwizard.metrics")) {
							ret.source.setUrl("https://github.com/dropwizard/dropwizard.git");
							ret.source.setRepositoryType("git");
						}
						else if (ret.source.getGroupId().equals("com.composum.sling.core")) {
							ret.source.setUrl("https://github.com/ist-dresden/composum.git");
							ret.source.setRepositoryType("git");
						}
						else if (ret.source.getGroupId().equals("org.slf4j")) {
							ret.source.setUrl("https://github.com/qos-ch/slf4j.git");
							ret.source.setRepositoryType("git");
						}
						else if (ret.source.getGroupId().startsWith("org.apache.aries")) {
							ret.source.setUrl("https://svn.apache.org/repos/asf/aries");
							ret.source.setRepositoryType("svn");
						}
						else if (ret.source.getGroupId().equals("org.apache.pdfbox")) {
							ret.source.setUrl("http://svn.apache.org/repos/asf/pdfbox");
							ret.source.setRepositoryType("svn");
						}
						else if (ret.source.getGroupId().equals("org.apache.tika")) {
							ret.source.setUrl("https://github.com/apache/tika.git");
							ret.source.setRepositoryType("git");
						}
						else if (ret.source.getGroupId().equals("com.google.guava")) {
							ret.source.setUrl("https://github.com/google/guava.git");
							ret.source.setRepositoryType("git");
						}
						else if (ret.source.getGroupId().equals("org.apache.httpcomponents")) {
							ret.source.setUrl("https://git-wip-us.apache.org/repos/asf/httpcomponents-client.git");
							ret.source.setRepositoryType("git");
						}					
						else if (ret.source.getGroupId().equals("org.antlr")) {
							ret.source.setUrl("https://github.com/antlr/antlr4.git");
							ret.source.setRepositoryType("git");							
						}
						else {
							logger.error("Cannot detect source type " + scm + " for " + ret.toString() + " at " + f.getPath());
							ret.source.setUrl(scm);
						}
					}
				}
			}
		}
		catch(Exception e) {
			logger.error("Failed to process " + f.getPath(), e);
		}
		return ret;
	}
	
	private class ArtifactSCM {
		private String path;
		private Source source = new Source();
		
		public String toString() {
			return source.getGroupId() + ":" + source.getArtifactId() + ":" + source.getVersion();
		}
	}
}
