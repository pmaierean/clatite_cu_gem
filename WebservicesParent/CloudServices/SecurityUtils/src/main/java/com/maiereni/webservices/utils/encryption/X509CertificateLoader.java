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
package com.maiereni.webservices.utils.encryption;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

/**
 * A loader of X509Certificate
 * @author Petre Maierean
 */
public class X509CertificateLoader {
    private static final String JKS = "JKS";
    private X509Certificate certificate;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    public X509CertificateLoader(final SecurityConfiguration configuration) throws Exception {
        loadCertificate(configuration);
    }

    public X509Certificate getCertificate() {
        return certificate;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    private void loadCertificate(final SecurityConfiguration configuration) throws Exception {
        if (configuration == null) {
            throw new Exception("No configuration has been provided");
        }
        if (StringUtils.isAllBlank(configuration.getKeyStorePass(), configuration.getKeyPass())) {
            throw new Exception("Invalid configuration has been provided");
        }
        X509Certificate ret = null;
        try (InputStream is = getInputStream(configuration.getKeyStore())) {
            load(configuration, is);
        }
    }

    private InputStream getInputStream(String keyStore) throws Exception {
        if (StringUtils.isBlank(keyStore)) {
            throw new Exception("The Key store cannot be blank");
        }
        InputStream ret = null;
        File f = new File(keyStore);
        if (f.isFile()) {
            ret = new FileInputStream(f);
        }
        else {
            ret = getClass().getResourceAsStream(keyStore);
        }
        if (ret == null) {
            throw new Exception("Cannot find the specified keyStore at " + keyStore);
        }
        return ret;
    }

    private void load(final SecurityConfiguration configuration, final InputStream is)
            throws Exception {
        if (is  == null) {
            throw new Exception("The input stream is null");
        }
        String keyStoreType = configuration.getKeyStoreType();
        KeyStore keyStore = null;
        if (StringUtils.isBlank(keyStoreType) || keyStoreType.equalsIgnoreCase(JKS)) {
            keyStore = KeyStore.getInstance(JKS);
        }
        else {
            keyStore = KeyStore.getInstance(keyStoreType);
        }
        String keyStorePass = configuration.getKeyStorePass();
        String keyAlias = configuration.getKeyAlias();
        String keyPass = configuration.getKeyPass();
        keyStore.load(is, StringUtils.isNotBlank(keyStorePass) ? keyStorePass.toCharArray(): null);
        Enumeration<String> es = keyStore.aliases();
        X509Certificate ret = null;
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
                    certificate = (X509Certificate) pk.getCertificate();
                    break;
                }
            }
        }
        if (certificate == null) {
            throw new Exception("Could not load the certificate from the Keystore");
        }
    }
}
