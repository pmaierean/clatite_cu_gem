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
package com.maiereni.host.web.util.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.annotation.Nonnull;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maiereni.host.web.util.ConfigurationProvider;
import com.maiereni.host.web.util.DataEncryptor;

/**
 * An implementation of the Configuration API that uses an external file having encrypted data as a storage
 * for the application configuration
 * @author Petre Maierean
 *
 */
public class ConfigurationProviderImpl implements ConfigurationProvider {
	private static final SimpleDateFormat SDF = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	private static final Logger logger = LoggerFactory.getLogger(ConfigurationProviderImpl.class);
	private DataEncryptor dataEncryptor;
	private String configFile;
	private Properties properties;
	
	public ConfigurationProviderImpl(@Nonnull final DataEncryptor dataEncryptor, @Nonnull final String configFile) 
		throws Exception {
		this.dataEncryptor = dataEncryptor;
		this.configFile = configFile;
		this.properties = loadConfiguration();
	}

	@Override
	public String getProperty(String key, String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}

	@Override
	public synchronized void setProperty(String key, String value) throws Exception {
		properties = loadConfiguration();
		properties.setProperty(key, value);
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			properties.store(out, "Configuration properties from " + SDF.format(new Date()));
			byte[] buffer = out.toByteArray();
			byte[] encoded = dataEncryptor.encryptData(buffer);
			File f = new File(configFile);
			FileUtils.writeByteArrayToFile(f, encoded);
			logger.debug("The new property has been saved into the configuration file");
		}		
	}
	
	public String getConfigFile() {
		return configFile;
	}
	
	private Properties loadConfiguration() throws Exception {
		Properties props = new Properties();
		File f = new File(configFile);
		if (f.exists()) {
			byte[] buffer = FileUtils.readFileToByteArray(f);
			if (buffer.length > 0) {
				byte[] decrypted = dataEncryptor.decryptData(buffer);
				try (ByteArrayInputStream is = new ByteArrayInputStream(decrypted)) {
					props.load(is);
				}
			}
		}
		return props;			
	}
}
