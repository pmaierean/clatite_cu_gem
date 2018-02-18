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
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;

import com.maiereni.blpapi.importer.bo.PackageBean;
import com.maiereni.utils.archiving.tar.TarGzArchivingUtil;
import com.maiereni.utils.http.BaseSSLHttpClient;
import com.maiereni.utils.http.bo.ResponseBean;

/**
 * Application that downloads the most recent Zip file from the Bloomberg Website
 * 
 * @author Petre Maierean
 *
 */
public class FileDownloader extends BaseSSLHttpClient {
	private Log logger;
	public static final String BPL_URL = "com.maiereni.blpapi.importer.sourceUrl";
	private static final String FOLDER_URL = "https://software.tech.bloomberg/BLPAPI-Stable-Generic";
	private static final String FILE_NAME_SUFFIX = ".tar.gz";
	private static final String FILE_NAME_PATTERN = "blpapi\\x5Fjava\\x5F[0-9\\x2E]*\\x2Etar\\x2Egz";
	private static final String LINE_PATTERN = ".*(" + FILE_NAME_PATTERN + ").*";
	private Pattern pattern;
	private TarGzArchivingUtil decompressor;
	private String blpFolder;
	private VersionDetector versionDetector;
	/**
	 * Construct the utility class
	 * @throws Exception
	 */
	public FileDownloader() throws Exception {
		this(null);
	}
	
	
	/**
	 * Construct the utility class
	 * @throws Exception
	 */
	public FileDownloader(final Log logger) throws Exception {
		super();
		pattern = Pattern.compile(FILE_NAME_PATTERN);
		blpFolder= System.getProperty(BPL_URL, FileDownloader.FOLDER_URL);
		decompressor = new TarGzArchivingUtil();
		versionDetector = new VersionDetector();
		if (logger == null)
			this.logger = new SystemStreamLog();
		else 
			this.logger = logger;
	}
	
	
	public PackageBean getLibrary() throws Exception {
		return getLibrary(blpFolder);
	}
	/**
	 * Get the library 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public PackageBean getLibrary(@Nonnull final String url) throws Exception {
		PackageBean ret = null;
		File archive = getBLApi(url);
		try {
			ret = new PackageBean();
			File fDir = decompressor.decompress(archive);
			ret.setPath(fDir.getPath());
			String name = fDir.getName();
			String version = getVersion(name);
			ret.setVersion(version);
		}
		finally {
			if (!archive.delete())
				archive.deleteOnExit();
		}
		return ret;
	}
	
	/**
	 * Get the most recent library version
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public String getLibraryVersion(@Nonnull final String url) throws Exception {
		return getMostRecentFile(url);
	}
	
	/**
	 * Download the most recent API available at the FOLDER_URL
	 * @return
	 * @throws Exception
	 */
	File getBLApi() throws Exception {
		return getBLApi(blpFolder);
	}
	
	/**
	 * Download the most recent API available at the URL
	 * @param url
	 * @return
	 * @throws Exception
	 */
	File getBLApi(@Nonnull final String url) throws Exception {
		String tarGzName = url;
		if (!url.endsWith(FILE_NAME_SUFFIX)) {
			tarGzName = getMostRecentFile(url);
			if (!url.endsWith("/"))
				tarGzName = url + "/" + tarGzName;
			else
				tarGzName = url + "/" + tarGzName;
		}
		logger.debug("Get the BLApi Java from " +  tarGzName);
		ResponseBean responseBean = downloadFile(tarGzName);
		File f = new File(responseBean.getBody());
		String newName = f.getName().replaceAll("file", "tar.gz");
		File fNewFile = new File(f.getParentFile(), newName);
		f.renameTo(fNewFile);
		logger.debug("The file is available ar " + fNewFile.getPath());
		return fNewFile;
	}

	private String getMostRecentFile(final String url) throws Exception {
		ResponseBean responseBean = get(url, BaseSSLHttpClient.EMPTY_PARAMS);
		return extractFromText(responseBean.getBody());
	}
	
	String extractFromText(final String text) throws Exception {
		String ret = null, version = null;
		try (StringReader sr = new StringReader(text)) {
			LineNumberReader lnr = new LineNumberReader(sr);
			String s = null;
			for(int i=0; (s = lnr.readLine()) != null; i++) {
				if (s.matches(LINE_PATTERN)) {
					Matcher matcher = pattern.matcher(s);
					while (matcher.find()) {
						String tarName = matcher.group();
						logger.debug("Fund '" + tarName + "' in line " + i);
						String v = getVersion(tarName);
						if (version == null || compareVersions(v, version) > 0) {
							ret = tarName;
							version = v;
						}
						break;
					}
				}
			}
			lnr.close();
		}		
		return ret;
	}
	
	int compareVersions(final String s1, final String s2) {
		return versionDetector.compareVersions(s1, s2);
	}

	String getVersion(final String s) {
		return versionDetector.getVersion(s);
	}
}
