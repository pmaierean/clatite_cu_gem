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
import java.io.FileReader;
import java.io.InputStream;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;
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
			CertificateFactory factory = CertificateFactory.getInstance("X.509");
			return (X509Certificate) factory.generateCertificate(in);
		}
	}
	
	@Bean
	public PrivateKey getKey(@Nonnull final SecurityConfiguration securityConfiguration) throws Exception {
		if (StringUtils.isAnyEmpty(securityConfiguration.getKeyStorePath(), securityConfiguration.getKeyStorePassword()))
			throw new Exception("The SSL settings cannot be found");
		Security.addProvider(new BouncyCastleProvider());
		PEMParser pemParser = null;
		try (FileReader fr = new FileReader(securityConfiguration.getKeyStorePath())) {
			pemParser = new PEMParser(fr);
			Object object = pemParser.readObject();
			JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
			KeyPair kp = null;
			if (object instanceof PEMEncryptedKeyPair) {
			    PEMEncryptedKeyPair ckp = (PEMEncryptedKeyPair) object;
			    char[] pwd = securityConfiguration.getKeyStorePassword().toCharArray();
			    PEMDecryptorProvider decProv = new JcePEMDecryptorProviderBuilder().build(pwd);
			    kp = converter.getKeyPair(ckp.decryptKeyPair(decProv));
			}
			else {
			    PEMKeyPair ukp = (PEMKeyPair) object;
			    kp = converter.getKeyPair(ukp);
			}
			return kp.getPrivate();
		}
		finally {
			if (pemParser != null)
				try {
					pemParser.close();
				}
				catch(Exception e) {}	
		}
	}
	
	@Bean
	public DataEncryptor getEncryptor(final X509Certificate certificate, final PrivateKey key) {
		return new BouncyCastleEncryptorImpl(certificate, key);
	}
}
