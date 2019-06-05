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
package com.maiereni.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.util.Enumeration;
import java.util.Properties;

import javax.annotation.Nonnull;
import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Utility class to encrypt the content of a Properties object using RSA algorithm.  
 * The Private and Public keys are obtained from a JKS file, presumably the cacerts of the JVM. The utility class requires the BouncyCastle provider
 * to be available with the JVM
 * 
 * @author Petre Maierean
 *
 */
public class EncryptedFileLoader {
	private static final String CLASSPATH = "classpath:";
	public static final String RSA = "RSA/None/NoPadding";
	public static final String BouncyCastle = "BC";
    public static final String KEY_STORE_PASSWORD = "javax.net.ssl.keyStorePassword";
    public static final String KEY_STORE_PATH = "javax.net.ssl.keyStore";
    public static final String KEY_STORE_TYPE = "javax.net.ssl.keyStoreType";
    public static final String KEY_PASSWORD = "javax.net.ssl.keyPassword";
    public static final String KEY_ALIAS = "javax.net.ssl.keyAlias";
    
    private String keyStorePassword;
    private String keyPassword;
    private String keyStorePath;
    private String keyStoreType;
    private String keyAlias;
    
    private Base64 base64 = new Base64();

    public EncryptedFileLoader() {
    	this(System.getProperty(KEY_STORE_PATH), 
    		System.getProperty(KEY_STORE_PASSWORD), 
    		System.getProperty(KEY_PASSWORD, ""), 
    		System.getProperty(KEY_STORE_TYPE, "JKS"), 
    		System.getProperty(KEY_ALIAS, null));
    }

    public EncryptedFileLoader(
    	final String keyStorePath, 
    	final String keyStorePassword, 
    	final String keyPassword,
    	final String keyStoreType, 
    	final String keyAlias) {
    	this.keyStorePath = keyStorePath;
    	this.keyStorePassword = keyStorePassword;
    	this.keyPassword = keyPassword;
    	this.keyAlias = keyAlias;
    	this.keyStoreType = keyStoreType;
    }
    
    public void checkProvider() throws Exception {
		if (Security.getProvider(BouncyCastle) == null) {
			throw new Exception("Please install BounceCastle provider");
		}    	
    }
    
    /**
     * Load properties from an encrypted file
     * @param path points to a file containing an encrypted content
     * @return
     * @throws Exception
     */
	public Properties loadProperties(@Nonnull final String path) throws Exception {
		checkProvider();
		File f = new File(path);
		if (!f.isFile()) {
			throw new Exception("The path does not point to a file");
		}
		try (FileInputStream fis = new FileInputStream(f)) {
			Cipher cipher = Cipher.getInstance(RSA, BouncyCastle);
			PrivateKey pk = getPrivateKey();
			cipher.init(Cipher.DECRYPT_MODE, pk);
			String s = FileUtils.readFileToString(f, "UTF-8");
			byte[] buffer = base64.decode(s.getBytes());//IOUtils.readBytes(fis);
			byte[] decodedBuffer = cipher.doFinal(buffer);
			return loadProperties(decodedBuffer);
		}
	}

	/**
	 * Saves the properties to a file after encrypting its content
	 * @param properties the properties to be encrypted
	 * @param path the file to contain the encrypted content
	 * @throws Exception
	 */
	public void saveProperties(@Nonnull Properties properties, @Nonnull final String path) throws Exception {
		checkProvider();
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			Cipher cipher = Cipher.getInstance(RSA, BouncyCastle);
			PublicKey pk = getPublicKey();
			cipher.init(Cipher.ENCRYPT_MODE, pk);
			properties.store(out, null);
			byte[] buffer = out.toByteArray();
			byte[] encryptedBuff = cipher.doFinal(buffer);
			// FileUtils.writeByteArrayToFile(new File(path), encryptedBuff);
			String encodedString = new String(base64.encode(encryptedBuff));
			FileUtils.write(new File(path), encodedString, "UTF-8");
		}
	}
		
	private PrivateKey getPrivateKey() throws Exception {
		KeyStore ks = loadKeyStore(keyStorePath, keyStorePassword, keyStoreType);
		return getPrivateKey(ks, keyPassword, keyAlias);
	}

	private PublicKey getPublicKey() throws Exception {
		KeyStore ks = loadKeyStore(keyStorePath, keyStorePassword, keyStoreType);
		return getPublicKey(ks, keyPassword, keyAlias);		
	}
	
	private Properties loadProperties(final byte[] buffer) throws Exception {
		try (ByteArrayInputStream bis = new ByteArrayInputStream(buffer)) {
			Properties props = new Properties();
			props.load(bis);
			return props;
		}
	}
	
	private PublicKey getPublicKey(final KeyStore keyStore, final String keyPassword, final String alias) throws Exception {
	    PublicKey ret = null;
		char[] password = keyPassword.toCharArray();
	    KeyStore.ProtectionParameter protParam = new KeyStore.PasswordProtection(password);
		if (StringUtils.isNotBlank(alias)) {
			ret = ((KeyStore.PrivateKeyEntry) keyStore.getEntry(alias, protParam)).getCertificate().getPublicKey();
		}
		else {
			Enumeration<String> e = keyStore.aliases();
			while(e.hasMoreElements()) {
				String s = e.nextElement();
				if (keyStore.isCertificateEntry(s)) {
					ret = ((KeyStore.PrivateKeyEntry) keyStore.getEntry(s, protParam)).getCertificate().getPublicKey();
					break;
				}
			}
		}
		return ret;
	}
	
	private KeyStore loadKeyStore(@Nonnull final String path, @Nonnull final String password, final String type) throws Exception {
		char[] pwd = password.toCharArray();
		KeyStore ks = KeyStore.getInstance(type);
	    try (InputStream is = getInputStream(path)) {
	        ks.load(is, pwd);
	    }
		return ks;
	}
	
	private InputStream getInputStream(@Nonnull final String path) throws Exception  {
		InputStream ret = null;
		if (path.startsWith(CLASSPATH)) {
			ret = getClass().getResourceAsStream(path.substring(CLASSPATH.length()));
			if (ret == null) {
				throw new Exception("The resource cannot be found at " + path);
			}
		}
		else {
			File f = new File(path);
			if (!f.isFile()) {
				throw new Exception("The key store path does not point to a file");
			}
			ret = new FileInputStream(path);
		}
		
		return ret;
	}

	private PrivateKey getPrivateKey(final KeyStore keyStore, final String keyPassword, final String alias) throws Exception {
		char[] password = keyPassword.toCharArray();
		
	    KeyStore.ProtectionParameter protParam = new KeyStore.PasswordProtection(password);
	    PrivateKey ret = null;
		if (StringUtils.isNotBlank(alias)) {
			ret = ((KeyStore.PrivateKeyEntry) keyStore.getEntry(alias, protParam)).getPrivateKey();
		}
		else {
			Enumeration<String> e = keyStore.aliases();
			while(e.hasMoreElements()) {
				String s = e.nextElement();
				if (keyStore.isKeyEntry(s)) {
					ret = ((KeyStore.PrivateKeyEntry) keyStore.getEntry(s, protParam)).getPrivateKey();
					break;
				}
			}
		}
		return ret;
	}
	
}
