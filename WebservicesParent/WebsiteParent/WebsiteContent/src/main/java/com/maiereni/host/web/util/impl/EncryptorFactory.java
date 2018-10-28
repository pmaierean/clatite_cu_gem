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

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.maiereni.host.web.util.BaseBeanFactory;
import com.maiereni.host.web.util.ConfigurationProvider;
import com.maiereni.host.web.util.DataEncryptor;

/**
 * A factory class for the encryptor
 * @author Petre Maierean
 *
 */
@Configuration
public class EncryptorFactory extends BaseBeanFactory{
	public static final String TRUST_STORE_PASSWORD = "javax.net.ssl.trustStorePassword";
	public static final String TRUST_STORE_PATH = "javax.net.ssl.trustStore";
	public static final String KEY_STORE_PASSWORD = "javax.net.ssl.keyStorePassword";
	public static final String KEY_STORE_PATH = "javax.net.ssl.keyStore";
	public static final String KEY_ALIAS = "javax.net.ssl.keyAlias";
	public static final String CONFIGURATION_FILE = "com.maiereni.host.web.util.configurationFilePath";
	public static final String DEFAULT_CONFIGURATION_FILE = "/opt/local/maiereni/config.dta";
	public EncryptorFactory() throws Exception {
		super();
	}

	@Bean
	public SecurityConfiguration getSecurityConfiguration() {
		SecurityConfiguration ret = new SecurityConfiguration();
        ret.setTrustStorePassword(getProperty(TRUST_STORE_PASSWORD, null));
        ret.setTrustStorePath(getProperty(TRUST_STORE_PATH, null));
        ret.setKeyAlias(getProperty(KEY_ALIAS, null));
        ret.setKeyStorePassword(getProperty(KEY_STORE_PASSWORD, null));
        ret.setKeyStorePath(getProperty(KEY_STORE_PATH, null));
        return ret;
	}
	
	@Bean
	public X509Certificate getCertificate(@Nonnull final SecurityConfiguration securityConfiguration) throws Exception {
		if (StringUtils.isAnyEmpty(securityConfiguration.getKeyStorePath(), securityConfiguration.getKeyStorePassword()))
			throw new Exception("The SSL settings cannot be found");
		try (InputStream is = new FileInputStream(securityConfiguration.getKeyStorePath())) {
			return getCertificate(is, securityConfiguration.getKeyAlias(), securityConfiguration.getKeyStorePassword());
		}
	}
	
	
	@Bean
	public PrivateKey getKey(@Nonnull final SecurityConfiguration securityConfiguration) throws Exception {
		if (StringUtils.isAnyEmpty(securityConfiguration.getKeyStorePath(), securityConfiguration.getKeyStorePassword()))
			throw new Exception("The SSL settings cannot be found");
		try (InputStream is = new FileInputStream(securityConfiguration.getKeyStorePath())) {
			return getKey(is, securityConfiguration.getKeyAlias(), securityConfiguration.getKeyStorePassword());
		}
	}
		
	@Bean
	public DataEncryptor getEncryptor(final X509Certificate certificate, final PrivateKey key) {
		return new BouncyCastleEncryptorImpl(certificate, key);
	}
	
	@Bean
	public ConfigurationProvider getConfiguration(final DataEncryptor dataEncryptor) throws Exception {
		String configFile = getProperty(CONFIGURATION_FILE, DEFAULT_CONFIGURATION_FILE);
		return new ConfigurationProviderImpl(dataEncryptor, configFile);
	}
	
	protected X509Certificate getCertificate(@Nonnull final InputStream is, @Nonnull final String alias, @Nonnull final String keyStorePassword) 
		throws Exception {
		X509Certificate ret = null;
	    KeyStore keystore = KeyStore.getInstance("JKS");
	    char[] pwd = keyStorePassword.toCharArray();
	    keystore.load(is, pwd);	
	    Enumeration<String> en = keystore.aliases();
	    while(en.hasMoreElements()) {
	    	String s = en.nextElement();
	    	Certificate cert = keystore.getCertificate(s);
	    	if (StringUtils.isEmpty(alias) || alias.equals(s)) {
	    		ret = (X509Certificate)cert;
	    		break;
	    	}
	    }
	    return ret;
	}

	protected PrivateKey getKey(@Nonnull final InputStream is, @Nonnull final String keyAlias, @Nonnull final String keyStorePassword) throws Exception {
		PrivateKey ret = null;
		KeyStore keystore = KeyStore.getInstance("JKS");
	    char[] pwd = keyStorePassword.toCharArray();
	    keystore.load(is, pwd);
	    Enumeration<String> en = keystore.aliases();
	    while(en.hasMoreElements()) {
	    	String s = en.nextElement();
	    	if ((StringUtils.isEmpty(keyAlias) || keyAlias.equals(s)) && keystore.isKeyEntry(s)) {
	    		ret = (PrivateKey) keystore.getKey(s, pwd);
	    		break;
	    	}
	    }
	    return ret;
	}

}
