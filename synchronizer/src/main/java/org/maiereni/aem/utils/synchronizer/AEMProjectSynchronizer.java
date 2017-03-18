/**
 * Copyright 2017 Maiereni Software and Consulting Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */
package org.maiereni.aem.utils.synchronizer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Mojo that downloads artifacts from AEM using vault. The 
 * @author Petre Maierean
 *
 */
@Mojo(name="synchronizer")
public class AEMProjectSynchronizer extends AbstractMojo {

	private static final String TEXT = ",txt";
	/**
	 * The path to vault 
	 */
	@Parameter(property = "synchronizer.vaultPath", required=false) 
	private String vaultPath;
	/**
	 * The URL to AEM instance to download from
	 */
	@Parameter(property = "synchronizer.uri", required=false, defaultValue="http://localhost:4502/crx") 

	private String uri;
	/**
	 * The JCR path to download from
	 */
	@Parameter(property = "synchronizer.jcrPath", required=true) 
	private String jcrPath;
	/**
	 * The local path to download to
	 */
	@Parameter(property = "synchronizer.localPath", required=true) 
	private String localPath;
	
	/**
	 * The local path to download to
	 */
	@Parameter(property = "synchronizer.userName", required=false, defaultValue="admin") 
	private String userName;
	/**
	 * The local path to download to
	 */
	@Parameter(property = "synchronizer.ignoreXMLTags", required=false, defaultValue="") 
	private String ignoreXMLTags;
	
	/**
	 * The local path to download to
	 */
	@Parameter(property = "synchronizer.ignoreXMLAttribute", required=false, defaultValue="cq:lastModified,jcr:lastModified") 
	private String ignoreXMLAttribute;
	
	/**
	 * The local path to download to
	 */
	@Parameter(property = "synchronizer.password", required=false, defaultValue="admin") 
	private String password;
	
	private static final String COMMAND_TEMPLATE = "%s export -v -p %s %s %s --credentials %s:%s";
	public void execute() throws MojoExecutionException, MojoFailureException {	
		String userName = System.getProperty("synchronizer.user.name", this.userName);
		String password = System.getProperty("synchronizer.user.password", this.password);
		String vaultPath = System.getProperty("synchronizer.vault.path", this.vaultPath);
		if (StringUtils.isEmpty(vaultPath))
			throw new MojoExecutionException("The vaultPath is missing. You need to specify it with -Ddownloader.vault.path");
		String[] paths = jcrPath.split(",");
		File localDir = new File(localPath);
		File tmpDir = getTempDir();
		try {
			XmlComparator xmlFileComparator = getXmlComparator();
			for(String path: paths) {
				File localSourcesDir = new File(localDir, path);
				File localTempSourcesDir = createSourcesCopy(localSourcesDir, tmpDir, path);
				String cmd = String.format(COMMAND_TEMPLATE, vaultPath, uri, path, localTempSourcesDir.getPath(), userName, password);
				runCommand(cmd);
				getLog().debug("Code has been downloaded for " + path);
				File fToCopy = new File(localTempSourcesDir, "jcr_root" + path);
				copyDownloads(fToCopy, localSourcesDir, xmlFileComparator);
				getLog().debug("Downloaded code has been copied to "  + fToCopy.getPath() + " for " + path);
			}
		}
		finally {
			try {
				FileUtils.deleteDirectory(tmpDir);
			}
			catch(Exception e) {
				getLog().error("Failed to remove " + tmpDir.toString(), e);
			}
		}
	}
	
	private XmlComparator getXmlComparator() throws MojoExecutionException {
		return new XmlComparator() {
			@Override
			protected Log getLog() {
				return getLog();
			}
			public boolean ignoreTag(final String tagName) {
				return false;
			}
			public boolean ignoreAttribute(final String attributeName) {
				return false;
			}
			public XmlComparator init() {
				getAsList(ignoreXMLTags); 
				getAsList(ignoreXMLAttribute);
				return this;
			}
		}.init();
	}
	
	private List<String> getAsList(final String s) {
		if (s != null)
			return Arrays.asList(s.split(","));
		return null;
	}
	
	private void copyDownloads(final File tempDir, final File localSourcesDir, final XmlComparator xmlFileComparator) throws MojoExecutionException {
		try {
			if (!localSourcesDir.exists())
				if (!localSourcesDir.mkdirs())
					throw new Exception("Cannot create directory at " + localSourcesDir.getPath());
			List<File> toRemove = new ArrayList<File>();
			File[] fChildren = localSourcesDir.listFiles();
			if (fChildren != null)
				toRemove.addAll(Arrays.asList(fChildren));
			File[] fTChildren = tempDir.listFiles();
			if (fTChildren != null) {
				for(File fTChild: fTChildren) {
					String name = fTChild.getName();
					File fChild = getFileByName(toRemove, name);
					if (fChild == null) { // A brand new file
						fChild = new File(localSourcesDir, name);
						if (fTChild.isDirectory()){
							if (!fChild.mkdirs())
								throw new Exception("Could not make directory at " + fChild.getPath());
							copyDownloads(fTChild, fChild, xmlFileComparator);
						}
						else { 
							if (fTChild.getName().endsWith(".jar"))
								getLog().debug("Skip to copy JAR file: " + fTChild.getPath());
							else
								FileUtils.copyFile(fTChild, fChild);
						}
					}
					else { // An existing file or folder
						toRemove.remove(fChild);
						if (fTChild.isDirectory())
							copyDownloads(fTChild, fChild, xmlFileComparator);
						else {
							if (fTChild.getName().endsWith(".xml")) {
								if (!xmlFileComparator.isSame(fTChild, fChild)) 
									FileUtils.copyFile(fTChild, fChild);
							}
							else
								FileUtils.copyFile(fTChild, fChild);
						}
					}
				}
			}
			
		}
		catch(Exception e) {
			getLog().error("copyDownloads", e);
			throw new MojoExecutionException("Failed to copyDownloads ", e);
		}
	}
	
	private File getFileByName(final List<File> fChildren, final String name) {
		File ret = null;
		for(File fChild: fChildren) {
			if (fChild.getName().equals(name)) {
				ret = fChild;
				break;
			}
		}
		return ret;
	}
	
	private File createSourcesCopy(final File localSourcesDir, final File tempDir, final String jcrPath) throws MojoExecutionException {
		try{
			File destinationDirectory = new File(tempDir, UUID.randomUUID().toString().replace("-", ""));
			if (!destinationDirectory.mkdirs())
				throw new Exception("Cannot create directory at " + destinationDirectory.getPath());
			getLog().debug("Folder has been created at " + destinationDirectory.getPath() + " for " + jcrPath);
			if (localSourcesDir.isDirectory()) { // Create a local copy of the existing sources
				FileUtils.copyDirectory(localSourcesDir, destinationDirectory);
				getLog().debug("Existing code has been copied for " + jcrPath);
			}
			return destinationDirectory;
		}
		catch(Exception e) {
			getLog().error("Failed to create temp", e);
			throw new MojoExecutionException("Failed to create temp", e);
		}
	}
	
	private File getTempDir() throws MojoExecutionException {
		File fTmp = null;
		try {
			fTmp = File.createTempFile("Simple", TEXT);
			int ix = fTmp.getPath().lastIndexOf(TEXT);
			File fDir = new File(fTmp.getPath().substring(0, ix));
			if (!fDir.mkdirs())
				throw new Exception("Cannot create directory at " + fDir.getPath());
			return fDir;
		}
		catch(Exception e) {
			getLog().error("Failed to create temp", e);
			throw new MojoExecutionException("Failed to create temp", e);
		}
		finally {
			if (fTmp != null)
				if (!fTmp.delete())
					fTmp.deleteOnExit();
		}
	}
	
	
	private void runCommand(final String command) throws MojoFailureException {
		getLog().debug("Run: " + command);
		int retCode = -1;
		try {
			Process p = Runtime.getRuntime().exec(command, null);
			Thread th1 = new Thread(new SyncPipe(p.getErrorStream(), true));
			th1.setDaemon(true);
			th1.start();
			SyncPipe syp = new SyncPipe(p.getInputStream(), false);
			Thread th2 = new Thread(syp);
			th2.setDaemon(true);
			th2.start();
			retCode = p.waitFor();
		}
		catch(Exception e) {
			getLog().error("Failed to run command: " + command, e);
		}
		if (retCode != 0)
			throw new MojoFailureException("Failed to run" + retCode);		
	}
	
	
	public class SyncPipe implements Runnable {
		protected byte[] out;
		private final InputStream istrm;
		private boolean isError;

		public SyncPipe(InputStream istrm, boolean isError) {
			this.istrm = istrm;
			this.isError = isError;
		}
		
		public void run() {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			try {
				final byte[] buffer = new byte[1024];
				for (int length = 0; (length = istrm.read(buffer)) != -1; ) {
					os.write(buffer, 0, length);
				}
				out = os.toByteArray();
				String res = os.toString();
				if (!res.equals(""))
					if (isError)
						getLog().error(res, null);
					else
						getLog().debug(res);
			}
			catch (Exception e) {
				getLog().error("Failed to write to the pipe", e);
			}
			finally {
				try {
					os.close();
				}
				catch(Exception e) {}
			}
		}
		
		public byte[] getOut() {
			return out;
		}
	}


}
