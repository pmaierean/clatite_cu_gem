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
package com.maiereni.sling.sources;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.maiereni.sling.sources.bo.Arguments;
import com.maiereni.sling.sources.bo.Project;
import com.maiereni.sling.sources.bo.Repository;
import com.maiereni.sling.sources.git.GitProjectDownloader;
import com.maiereni.sling.sources.reports.XmlDownloadingReportMaker;
import com.maiereni.sling.sources.svn.SvnProjectDownloader;

/**
 * Updates the source code of the bundles in the Sling Application
 * @author Petre Maierean
 *
 */
public class ProjectsUpdaterMain {
	private static final Logger logger = LoggerFactory.getLogger(ProjectsUpdaterMain.class);
	private static final String MODEL_FILE= "modelFile";
	private static final String PROJECT_ROOT = "projectsRoot";
	private static final String UPDATE_LOG_FILE = "updateLogFile";
	private static final String PROJECT_NAME = "projectName";
	private static final List<String> CONFIG_KEYS = Arrays.asList(new String[] {
		MODEL_FILE, PROJECT_ROOT, UPDATE_LOG_FILE, PROJECT_NAME
	});
	
	/**
	 * Update projects from the modelFile. Get the source code to the projectRoot
	 * @param arguments
	 * @throws Exception 
	 */
	public void updateProjects(final Arguments arguments) throws Exception {
		List<Repository> repositories = loadProjects(arguments.getModelFile(), arguments.getProjectName());
		try (XmlDownloadingReportMaker reportMaker = new XmlDownloadingReportMaker(arguments.getUpdateLogFile())) {
			Map<String, ProjectDownloader> downloaders = new HashMap<String, ProjectDownloader>();
			for(Repository repository: repositories) {
				ProjectDownloader downloader = getDownloader(downloaders, reportMaker, arguments.getProjectsRoot(), repository.getRepositoryType());
				if (downloader == null) {
					reportMaker.notify(repository, "error", "Cannot find a downloader for the repository type " + repository.getRepositoryType());
				}
				else {
					downloader.downloadProject(repository);
				}
			}
		}
	}
	
	private ProjectDownloader getDownloader(final Map<String, ProjectDownloader> downloaders, final XmlDownloadingReportMaker reportMaker, final String root, final String repositoryType) {
		ProjectDownloader ret = downloaders.get(repositoryType);
		if (ret == null) {
			if (repositoryType.equals("git")) {
				ret = new GitProjectDownloader(reportMaker, new File(root));
			}
			else if (repositoryType.equals("svn")) {
				ret = new SvnProjectDownloader(reportMaker, new File(root));
			}
			if (ret != null) {
				downloaders.put(repositoryType, ret);
			}
		}
		return ret;
	}
	
	private List<Repository> loadProjects(final String modelFile, final String projectName) throws Exception {
		List<Repository> ret = null;
		File f = new File(modelFile);
		if (!f.isFile())
			throw new Exception("No such file " + modelFile);
		try (FileInputStream is = new FileInputStream(f)) {
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = documentBuilder.parse(is);
			XPath xpath = XPathFactory.newInstance().newXPath();
			String stmt = "//repository";
			if (StringUtils.isNotBlank(projectName)) {
				if (projectName.endsWith(".*")) {
					stmt = "//repository[project[starts-with(@name,'" + projectName.substring(0, projectName.length() - 2) + "')]]"; 
				}
				else {
					stmt = "//repository[project[@name='" + projectName + "']]";
				}
			}
			logger.debug("XPath statement is " + stmt);
			NodeList nl = (NodeList)xpath.evaluate(stmt, document, XPathConstants.NODESET);
			int max = nl.getLength();
			ret = new ArrayList<Repository>();
			for(int i=0; i< max; i++) {
				Element element = (Element) nl.item(i);
				ret.add(convert(element));
			}
			logger.debug("Found a number of " + ret.size());
		}
		return ret;
	}
	
	private Repository convert(final Element element) throws Exception {
		Repository ret = new Repository();
		ret.setRepositoryType(element.getAttribute("repositoryType"));
		ret.setUrl(element.getAttribute("url"));
		ret.setVersion(element.getAttribute("version"));
		ret.setProjects(new ArrayList<Project>());
		String name = null;
		NodeList nl = element.getElementsByTagName("project");
		for(int i= 0; i< nl.getLength(); i++) {
			Element pel = (Element)nl.item(i);
			Project p = new Project();
			p.setName(pel.getAttribute("name"));
			p.setDescription(pel.getTextContent());
			ret.getProjects().add(p);
			if (name == null)
				name=p.getName();
		}
		ret.setName(name);
		return ret;
	}
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		ProjectsUpdaterMain projectUpdater = new ProjectsUpdaterMain();
		try {
			Arguments arguments = convert(args);
			projectUpdater.updateProjects(arguments);
		}
		catch(Exception e) {
			logger.error("Failed to download", e);
		}
	}

	private static Arguments convert(final String[] args) throws Exception {
		Arguments arguments = new Arguments();
		for(String arg: args) {
			String[] toks = arg.split("=");
			int ix = CONFIG_KEYS.indexOf(toks[0]);
			if (ix >= 0 && toks.length == 2) {
				switch(ix) {
				case 0:
					arguments.setModelFile(toks[1]);
					break;
				case 1:
					arguments.setProjectsRoot(toks[1]);
					break;
				case 2:
					arguments.setUpdateLogFile(toks[1]);
					break;
				case 3:
					arguments.setProjectName(toks[1]);
					break;
				}
			}
		}
		if (StringUtils.isAnyBlank(arguments.getModelFile(), arguments.getProjectsRoot())) {
			throw new Exception("Expected arguments: " + MODEL_FILE + "=modelFile " + PROJECT_ROOT + "=projectRoot " + UPDATE_LOG_FILE + "=log_file");
		}
		return arguments;
	}

}
