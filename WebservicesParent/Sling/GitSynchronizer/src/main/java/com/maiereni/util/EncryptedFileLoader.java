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
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.Security;
import java.util.Enumeration;
import java.util.Properties;

import javax.annotation.Nonnull;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.crypto.util.PublicKeyFactory;


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
    
    static final String CMD_KEY_STORE_PASSWORD = "-D" + KEY_STORE_PASSWORD + "=";
    static final String CMD_KEY_STORE_PATH = "-D" + KEY_STORE_PATH + "=";
    static final String CMD_KEY_STORE_TYPE = "-D" + KEY_STORE_TYPE + "=";
    static final String CMD_KEY_PASSWORD = "-D" + KEY_PASSWORD + "=";
    static final String CMD_KEY_ALIAS = "-D" + KEY_ALIAS + "=";

    
    private static final String SUN_JAVA_COMMAND = "sun.java.command";
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
    	initFromSlingCmdLine();
    }

    private void initFromSlingCmdLine() {
    	String cmdLine = System.getProperty(SUN_JAVA_COMMAND);
    	if (StringUtils.isNotBlank(cmdLine)) {
    		String[] args = cmdLine.split(" ");
    		for(String arg: args) {
    			if (arg.startsWith(CMD_KEY_ALIAS)) {
    				this.keyAlias = arg.substring(CMD_KEY_ALIAS.length());
    			}
    			else if (arg.startsWith(CMD_KEY_PASSWORD)) {
    				this.keyPassword = arg.substring(CMD_KEY_PASSWORD.length());
    			}
    			else if (arg.startsWith(CMD_KEY_STORE_PASSWORD)) {
    				this.keyStorePassword = arg.substring(CMD_KEY_STORE_PASSWORD.length());
    			}
    			else if (arg.startsWith(CMD_KEY_STORE_PATH)) {
    				this.keyStorePath = arg.substring(CMD_KEY_STORE_PATH.length());
    			}
    			else if (arg.startsWith(CMD_KEY_STORE_TYPE)) {
    				this.keyStoreType = arg.substring(CMD_KEY_STORE_TYPE.length());
    			}
    		}
    	}    	
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
			throw new Exception("The path does not point to a file: " + path);
		}
		try (FileInputStream fis = new FileInputStream(f)) {
			String s = FileUtils.readFileToString(f, "UTF-8");
			byte[] buffer = base64.decode(s);
			byte[] decodedBuffer = decryptRSA(buffer);
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
			properties.store(out, "");
			byte[] buffer = out.toByteArray();
			byte[] hexEncodedCipher = encryptRSA(buffer);
            String encodedString = base64.encodeToString(hexEncodedCipher);
			FileUtils.write(new File(path), encodedString, "UTF-8");
		}
	}

	/**
	 * Encrypt and encode 
	 * @param buffer
	 * @return
	 * @throws Exception
	 */
	protected byte[] encryptRSA(final byte[] buffer) 
		throws Exception {
		try(ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            AsymmetricBlockCipher cipher = getAsymmetricBlockCipher(true);
            int len = cipher.getInputBlockSize();
            for (int i=0; i < buffer.length;  i += len) {
                if (i + len > buffer.length)
                    len = buffer.length - i;
 
                byte[] encrypted = cipher.processBlock(buffer, i, len);
                out.write(encrypted);
            }
            return out.toByteArray();
		}
    }

	protected byte[] decryptRSA(final byte[] buffer) throws Exception {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()){
            AsymmetricBlockCipher cipher = getAsymmetricBlockCipher(false);
            int len = cipher.getInputBlockSize();
            for (int i=0; i < buffer.length;  i += len) {
                if (i + len > buffer.length) {
                    len = buffer.length - i;
                } 
 
                byte[] decrypted = cipher.processBlock(buffer, i, len);
                out.write(decrypted);
            }
            return out.toByteArray();
        }
    }
	

	protected KeyPair getKeyPair() throws Exception {
		KeyPair ret = null;
		KeyStore keyStore = loadKeyStore(keyStorePath, keyStorePassword, keyStoreType);
		char[] password = keyPassword.toCharArray();
	    KeyStore.ProtectionParameter protParam = new KeyStore.PasswordProtection(password);
		if (StringUtils.isNotBlank(keyAlias)) {
			ret = convert((KeyStore.PrivateKeyEntry) keyStore.getEntry(keyAlias, protParam));
		}
		else {
			Enumeration<String> e = keyStore.aliases();
			while(e.hasMoreElements()) {
				String s = e.nextElement();
				if (keyStore.isCertificateEntry(s)) {
					ret = convert((KeyStore.PrivateKeyEntry) keyStore.getEntry(s, protParam));
					break;
				}
			}
		}

		return ret;
	}

	private AsymmetricBlockCipher getAsymmetricBlockCipher(boolean encoding) throws Exception {
		KeyPair keyPair = getKeyPair();
        AsymmetricKeyParameter key = null;
        if (encoding) { 
        	key = PrivateKeyFactory.createKey(keyPair.getPrivate().getEncoded());
        }
        else {
        	key = PublicKeyFactory.createKey(keyPair.getPublic().getEncoded());
        }
        AsymmetricBlockCipher ret = new PKCS1Encoding(new RSAEngine());
        
        ret.init(encoding, key);
        return ret;
	}
	private KeyPair convert(final KeyStore.PrivateKeyEntry entry) {
		return new KeyPair(entry.getCertificate().getPublicKey(), entry.getPrivateKey());
	}
	
	private Properties loadProperties(final byte[] buffer) throws Exception {
		try (ByteArrayInputStream bis = new ByteArrayInputStream(buffer)) {
			Properties props = new Properties();
			props.load(bis);
			return props;
		}
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

	/**
	 * Encode the input properties file and save the result to the outputFile
	 * @param inputFile
	 * @param outputFile
	 * @throws Exception
	 */
	public void encode(final String inputFile, final String outputFile) throws Exception {
		try (InputStream is = getInputStream(inputFile)) {
			Properties props = new Properties();
			props.load(is);
			saveProperties(props, outputFile);
		}
	}

	/**
	 * Decode the input properties file and save the result to the outputFile
	 * @param inputFile
	 * @param outputFile
	 * @throws Exception
	 */
	public void decode(final String inputFile, final String outputFile) throws Exception {
		try(FileOutputStream out = new FileOutputStream(outputFile)) {
			Properties props = loadProperties(inputFile);
			props.store(out, "Decoded");
		}
	}

	/**
	 * <p>Convert a properties file to an encrypted one and vice versa</p>
	 * <p>Arguments: -i=input_file -o=output_file</p>
	 * <p>If the input_file ends with '.properties' then the output_file will contain its encryption. Otherwise the output_file will contain the attempt of decryption</p>
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			String inputFile = null, outputFile = null;
			for(String arg: args) {
				if (arg.startsWith("-i=")) {
					inputFile = arg.substring(3);
				}
				else if (arg.startsWith("-o=")) {
					outputFile  = arg.substring(3);
				}
			}
			if (StringUtils.isAnyBlank(inputFile, outputFile)) {
				throw new Exception("Expected arguments: -i=input_file -o=output_file");
			}
			EncryptedFileLoader loader = new EncryptedFileLoader();
			if (inputFile.endsWith(".properties")) {
				loader.encode(inputFile, outputFile);
				System.out.println("Sccessfully encoded the file to " + outputFile);
			}
			else {
				loader.decode(inputFile, outputFile);
				System.out.println("Sccessfully decoded the file to " + outputFile);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
