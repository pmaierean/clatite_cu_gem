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
package com.maiereni.synchronizer.git.service.impl;

import java.io.FileInputStream;
import java.util.Properties;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maiereni.synchronizer.git.service.bo.Configuration;
import com.maiereni.synchronizer.git.service.bo.GitProperties;
import com.maiereni.synchronizer.git.service.bo.SlingProperties;
import com.maiereni.util.EncryptedFileLoader;

/**
 * @author Petre Maierean
 *
 */
class CredentialsLoader extends EncryptedFileLoader {
	private static final Logger logger = LoggerFactory.getLogger(CredentialsLoader.class);
	public static final String PASSWORD_KEY = "git.password";
	public static final String USER_KEY = "git.user";
	public static final String URL_KEY = "git.url";
	public static final String BRANCH_KEY = "git.branch";
	public static final String LOCAL_REPO = "git.localPath";
	public static final String CONTENT_PATH = "git.contentPath";
	private static final String URL_PRE = URL_KEY + "=";
	private static final String USER_PRE = USER_KEY + "=";
	private static final String PASSWORD_PRE = PASSWORD_KEY + "=";
	private static final String BRANCH_PRE = BRANCH_KEY + "=";
	private static final String LOCAL_REPO_PRE = LOCAL_REPO + "=";
	private static final String CONTENT_PATH_PRE = CONTENT_PATH + "=";
	private static final String SLING_URL = "sling.url";
	private static final String SLING_USER = "sling.user";
	private static final String SLING_PASSWORD = "sling.password";
	/**
	 * Get properties from a comma sepparated string
	 * @param s
	 * @return
	 * @throws Exception
	 */
	public GitProperties getPropertiesFromString(@Nonnull final String s) {
		GitProperties ret = new GitProperties();
		if (StringUtils.isNotBlank(s)) {
			String[] toks = s.split(",");
			for(String tok : toks) {
				if (tok.startsWith(URL_PRE)) {
					ret.setRemote(tok.substring(URL_PRE.length()));
				}
				else if (tok.startsWith(USER_PRE)) {
					ret.setUserName(tok.substring(USER_PRE.length()));
					logger.debug("User: " + ret.getUserName());
				}
				else if (tok.startsWith(PASSWORD_PRE)) {
					ret.setPassword(tok.substring(PASSWORD_PRE.length()));
					logger.debug("Password: " + ret.getPassword());
				}
				else if (tok.startsWith(BRANCH_PRE)) {
					ret.setBranchName(tok.substring(BRANCH_PRE.length()));
				}
				else if (tok.startsWith(LOCAL_REPO_PRE)) {
					ret.setLocalRepo(tok.substring(LOCAL_REPO_PRE.length()));
				}
				else if (tok.startsWith(CONTENT_PATH_PRE)) {
					ret.setContentPath(tok.substring(CONTENT_PATH_PRE.length()));
				}
			}
		}
		return ret;
	}
	/**
	 * Read properties from a file
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public Configuration getProperties(@Nonnull final String path) throws Exception {
		Configuration ret = new Configuration();
		Properties props = null;
		if (path.endsWith(".properties")) {
			try(FileInputStream is = new FileInputStream(path)) {
				props = new Properties();
				props.load(is);
				logger.debug("The properties have been loaded from a file");
			}
		}
		else {
			props = loadProperties(path);
			logger.debug("The properties have been loaded from an encrypted file");
		}
		ret.setGitProperties(getGitProperties(props));
		ret.setSlingProperties(getSlingProperties(props));
		return ret;
	}

	protected GitProperties getGitProperties(final Properties props) {
		GitProperties ret = new GitProperties();
		ret.setPassword(props.getProperty(PASSWORD_KEY));
		ret.setUserName(props.getProperty(USER_KEY));
		ret.setRemote(props.getProperty(URL_KEY));
		ret.setBranchName(props.getProperty(BRANCH_KEY));
		ret.setLocalRepo(props.getProperty(LOCAL_REPO));
		ret.setContentPath(props.getProperty(CONTENT_PATH));
		return ret;
	}
	
	protected SlingProperties getSlingProperties(final Properties props) {
		SlingProperties ret = new SlingProperties();
		ret.setPassword(props.getProperty(SLING_PASSWORD, "admin"));
		ret.setUserName(props.getProperty(SLING_USER, "admin"));
		ret.setUrl(props.getProperty(SLING_URL, "http://localhost:8080/system/content/bundle"));
		return ret;
	}
}
