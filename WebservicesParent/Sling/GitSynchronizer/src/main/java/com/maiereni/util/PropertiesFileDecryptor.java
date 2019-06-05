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

import java.io.FileOutputStream;
import java.util.Properties;

import javax.annotation.Nonnull;

/**
 * Utility class to encrypt a properties file
 * To specify where to get the public key for the encryption, user the JVM properties as it follows:
 * <ul>
 * <li><b>-Djavax.net.ssl.keyStorePassword</b> to specify the key store password</li>
 * <li><b>-Djavax.net.ssl.keyStore</b> to specify the location of the key store</li>
 * <li><b>-Djavax.net.ssl.keyStoreType</b> to specify the type of the key store. The default is JKS</li>
 * <li><b>-Djavax.net.ssl.keyPassword</b> to specify the password for the key</li>
 * <li><b>-Djavax.net.ssl.keyAlias</b> to specify the alias of the key</li>
 * </ul>  
 * @author Petre Maierean
 *
 */
public class PropertiesFileDecryptor extends EncryptedFileLoader {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			if (args.length < 2) {
				throw new Exception("Expected arguments: file.properties file.enc");
			}
			PropertiesFileDecryptor util = new PropertiesFileDecryptor();
			Properties props = util.loadProperties(args[0]);
			util.savePropertiesToFile(props, args[1]);
			System.out.println("The decrypted properties file has been created");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void savePropertiesToFile(@Nonnull final Properties props, @Nonnull final String path) throws Exception {
		try(FileOutputStream os = new FileOutputStream(path)) {
			props.store(os, null);
		}
	}
}
