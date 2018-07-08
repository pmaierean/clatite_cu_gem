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

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.maiereni.host.web.util.BaseBeanFactory;
import com.maiereni.host.web.util.DataEncryptor;

/**
 * A factory class for the encryptor
 * @author Petre Maierean
 *
 */
@Configuration
public class EncryptorFactory extends BaseBeanFactory{
	public static final String TRUST_STORE_PASSWORD = "javax.net.ssl.trustStorePassword";
	public static final String TRUST_STORE_PATH = "javax.net.ssl.truesStore";
	public static final String KEY_STORE_PASSWORD = "javax.net.ssl.keyStorePassword";
	public static final String KEY_STORE_PATH = "javax.net.ssl.keyStore";
	public static final String KEY_ALIAS = "javax.net.ssl.keyAlias";
	
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
		try (InputStream in = new FileInputStream("CERT.RSA")) {
			return getCertificate(in, securityConfiguration.getKeyAlias(), securityConfiguration.getKeyStorePassword());
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
	
	protected X509Certificate getCertificate(@Nonnull final InputStream is, @Nonnull final String alias, @Nonnull final String keyStorePassword) 
		throws Exception {
	    KeyStore keystore = KeyStore.getInstance("JKS");
	    char[] pwd = keyStorePassword.toCharArray();
	    keystore.load(is, pwd);	
	    Certificate[] chain = keystore.getCertificateChain(alias);
	    return (X509Certificate) chain[0];
	}

	protected PrivateKey getKey(@Nonnull final InputStream is, @Nonnull final String keyAlias, @Nonnull final String keyStorePassword) throws Exception {
	    KeyStore keystore = KeyStore.getInstance("JKS");
	    char[] pwd = keyStorePassword.toCharArray();
	    keystore.load(is, pwd);
	    return (PrivateKey) keystore.getKey(keyAlias, pwd);
	}

}
