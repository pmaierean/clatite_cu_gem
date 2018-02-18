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
package com.maiereni.blpapi.importer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.installation.InstallRequest;
import org.eclipse.aether.installation.InstallResult;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResult;

import com.maiereni.blpapi.importer.bo.PackageBean;

/**
 * Application class to import the Blp API libraries to the local MAVEN repository if needed
 * @author Petre Maierean
 *
 */
@Mojo( name="blpApiImporter", defaultPhase = LifecyclePhase.INITIALIZE)
public class BlpApiImporter extends AbstractMojo {	
	public static final String DEFAULT_GROUP_ID =  "com.bloomberg.blpapi";
	public static final String DEFAULT_API_ID =  "blpapi";
	
	@Parameter( property = "blpApiImporter.blpApiUrl", defaultValue = FileDownloader.BPL_URL)
	private String blpApiUrl;
	
	@Parameter( property = "blpApiImporter.versionConstantName", defaultValue = "blpApi.version")
	private String versionConstantName;

	@Parameter( property = "blpApiImporter.packageName", defaultValue = DEFAULT_GROUP_ID)
	private String packageName;

	@Parameter( property = "blpApiImporter.jarName", defaultValue = DEFAULT_API_ID)
	private String jarName;
	
	@Component
	RepositorySystem repositorySystem;
	
	@Parameter( defaultValue = "${repositorySystemSession}", readonly = true, required = true  )
	RepositorySystemSession repoSession;
	
	/**
	 * Import the blpApi library
	 */
	public void execute() throws MojoExecutionException {
		try {
			FileDownloader utils = new FileDownloader();
			String version = utils.getLibraryVersion(blpApiUrl);
			if (!isVersionUpToDate(packageName, jarName, version)) {
				getLog().debug("The new version of the package needs to be imported");
				PackageBean packageBean = downloadPackage(utils, blpApiUrl);
				version = updateVersion(packageBean);
			}
			getLog().debug("The blpApi version is " + version);
			System.setProperty(versionConstantName, version);
		}
		catch(Exception e) {
			getLog().error("Failed to download", e);
		}
	}
	
	boolean isVersionUpToDate(final String groupdId, final String artifactId, final String version) {
		boolean ret = false;
		try {
			ArtifactRequest artifactRequest = new ArtifactRequest();
			artifactRequest.setArtifact(new DefaultArtifact(groupdId, artifactId, "jar", version));
			ArtifactResult artifactResult = repositorySystem.resolveArtifact(repoSession, artifactRequest);
			ret = artifactResult.isResolved();
		}
		catch(Exception e) {
		}
		return ret;
	}

	PackageBean downloadPackage(final FileDownloader utils, final String blpApiUrl) throws Exception {
		PackageBean packageBean = utils.getLibrary(blpApiUrl);
		packageBean.setArtifactId(jarName);
		packageBean.setGroupId(packageName);
		getLog().debug("The package has been downloaded");
		return packageBean;
	}
	
	String updateVersion(final PackageBean packageBean) throws Exception {
		File fJar = findJar(packageBean);
		if (fJar != null) {
			String version = getActualVersion(packageBean);
			if (!isVersionUpToDate(packageBean.getGroupId(), packageBean.getArtifactId(), version)) {
				List<Artifact> artifacts = new ArrayList<Artifact>();
				artifacts.add(new DefaultArtifact(packageBean.getGroupId(), packageBean.getArtifactId(), null, "jar", version, null, fJar));
				InstallRequest installRequest = new InstallRequest();
				installRequest.setArtifacts(artifacts);
				InstallResult result = repositorySystem.install(repoSession, installRequest);
				getLog().debug("The new artifact has been installed:\r\n" + result.toString());
			}
			return version;
		}
		throw new Exception("Could not install");
	}
	
	File findJar(final PackageBean packageBean) {
		File ret = null;
		File root = new File(packageBean.getPath());
		if (root.exists()) {
			File fRoot = new File(root, "bin");
			File[] children = fRoot.listFiles();
			if (children != null) {
				for(File child: children) {
					String name = child.getName();
					if (name.startsWith("blpapi") && name.endsWith(".jar")) {
						ret = child;
						break;
					}
				}
			}
		}
		return ret;
	}
	
	String getActualVersion(final PackageBean packageBean) throws Exception {
		String ret = null;
		File fJar = findJar(packageBean);
		if (fJar != null) {
			try (JarFile jarFile = new JarFile(fJar)) {	
				Manifest mf = jarFile.getManifest();
				Attributes attr = mf.getMainAttributes();
				if (!attr.isEmpty())
					ret = attr.getValue("Implementation-Version");
			}
		}
		return ret;
	}
}
