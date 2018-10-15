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
package com.maiereni.saml.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.annotation.Nonnull;

import org.opensaml.security.credential.Credential;
import org.opensaml.security.x509.BasicX509Credential;

/**
 * @author Petre Maierean
 *
 */
public class CertsUtils {
	/**
	 * Load PKCS12 credentials
	 * @param keyStore
	 * @param password
	 * @return
	 * @throws KeyStoreException
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 * @throws UnrecoverableEntryException
	 */
    public Credential loadPKCS12Credential(@Nonnull final String keyStore, @Nonnull final String password) 
    	throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableEntryException {
        char[] pwd = password.toCharArray();

        KeyStore store = KeyStore.getInstance("PKCS12");
        try (FileInputStream stream = new FileInputStream(keyStore)) {
            store.load(stream, pwd);
        }

        KeyStore.ProtectionParameter protectionParameter = new KeyStore.PasswordProtection(pwd);
        KeyStore.PrivateKeyEntry pkEntry =
                (KeyStore.PrivateKeyEntry) store.getEntry("1", protectionParameter);
        PrivateKey pk = pkEntry.getPrivateKey();

        X509Certificate certificate = (X509Certificate) pkEntry.getCertificate();
        return new BasicX509Credential(certificate, pk);
    }
}
