/**
 * ================================================================
 * Copyright (c) 2017-2020 Maiereni Software and Consulting Inc
 * ================================================================
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.maiereni.encryption.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Enumeration;

/**
 * Retries the Encryption key
 * @author Petre Maierean
 */
public class EncryptionKeyProviderProviderImpl implements EncryptionKeyProvider {
    private static final String JKS = "jks";
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private String keyStore, keyStorePass, keyPass, keyStoreType, keyAlias;

    public EncryptionKeyProviderProviderImpl(
        final String keyStore,
        final String keyStorePass,
        final String keyPass) throws Exception {
        this(keyStore, keyStorePass, keyPass, JKS, null);
    }

    public EncryptionKeyProviderProviderImpl(
        final String keyStore,
        final String keyStorePass,
        final String keyPass,
        final String keyStoreType,
        final String keyAlias) throws Exception {
        this.keyStore = keyStore;
        this.keyStorePass = keyStorePass;
        this.keyPass = keyPass;
        this.keyStoreType = keyStoreType;
        this.keyAlias = keyAlias;
        init();
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public String getKeyAlias() {
        return keyAlias;
    }

    private void init() throws Exception {
        if (StringUtils.isBlank(keyStore)) {
            throw new Exception("The Key store cannot be blank");
        }
        File f = new File(keyStore);
        if (f.isFile()) {
            try (InputStream is = new FileInputStream(f)) {
                init(is);
            }
        }
        else {
            try (InputStream is = getClass().getResourceAsStream(keyStore)) {
                init(is);
            }
        }
     }

     private void init(final InputStream is) throws Exception {
        if (is  == null) {
            throw new Exception("The input stream is null");
        }
        KeyStore keyStore = null;
        if (StringUtils.isBlank(keyStoreType) || keyStoreType.equalsIgnoreCase(JKS)) {
            keyStore = KeyStore.getInstance(JKS);
        }
        else {
            keyStore = KeyStore.getInstance(keyStoreType);
        }
        keyStore.load(is, StringUtils.isNotBlank(keyStorePass) ? keyStorePass.toCharArray(): null);
        Enumeration<String> es = keyStore.aliases();
        while(es.hasMoreElements()) {
            String alias = es.nextElement();
            if (keyStore.isKeyEntry(alias)) {
                if (StringUtils.isBlank(keyAlias) || keyAlias.equals(alias)) {
                    keyAlias = alias;
                    KeyStore.PrivateKeyEntry pk = null;
                    if (StringUtils.isNotBlank(keyPass)) {
                        pk = (KeyStore.PrivateKeyEntry) keyStore.getEntry(alias, new KeyStore.PasswordProtection(keyPass.toCharArray()));
                    }
                    else {
                        pk = (KeyStore.PrivateKeyEntry) keyStore.getEntry(alias, null);
                    }
                    privateKey = pk.getPrivateKey();
                    publicKey = pk.getCertificate().getPublicKey();
                    break;
                }
            }
        }
        if (privateKey == null || publicKey == null) {
            throw new Exception("Could not initializa from the provided keystore");
        }
     }
}
