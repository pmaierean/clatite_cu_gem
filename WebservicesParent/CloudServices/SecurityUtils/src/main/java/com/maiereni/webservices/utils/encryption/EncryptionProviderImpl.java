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

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import javax.crypto.Cipher;
import java.security.*;

/**
 * An implementation of the Encryption provider that uses Bouncy Castle library
 * Provides an encryption method that uses the local Certificate
 *
 * @author Petre Maierean
 */
public final class EncryptionProviderImpl extends X509CertificateLoader implements EncryptionProvider {
    private static final Log logger = LogFactory.getLog(EncryptionProviderImpl.class);
    private static String UTF8 = "UTF-8";
    private static final String DEFAULT_SIGNING_METHOD = "SHA256withRSA";
    private static final String DEFAULT_ENCRYPTION_METHOD = "RSA";
    private String encryptionMethod, signingMethod;
    static {
        registerProvider();
    }
    EncryptionProviderImpl(final SecurityConfiguration configuration) throws Exception {
       super(configuration);
       this.encryptionMethod = configuration.encryptionMethod;
       if (StringUtils.isBlank(this.encryptionMethod)) {
           this.encryptionMethod = DEFAULT_ENCRYPTION_METHOD;
       }
       this.signingMethod = configuration.signingMethod;
       if (StringUtils.isBlank(this.signingMethod)) {
           this.signingMethod = DEFAULT_SIGNING_METHOD;
       }
    }

    /**
     * Validates that the token contains a valid signature
     * @param token
     * @return
     */
    @Override
    public boolean isSigned(String token, String signature) {
        boolean ret = false;
        if (StringUtils.isNotBlank(token)) {
            try {
                byte[] data = Base64.decodeBase64(signature);
                ret = verifSignedData(token.getBytes(UTF8), data);
            }
            catch (Exception e) {
                logger.error("Cannot verify signature", e);
            }
        }
        return ret;
    }

    /**
     * Get the signature for a string
     * @param token
     * @return
     * @throws Exception
     */
    @Override
    public String getSignature(String token) throws Exception {
        String ret = null;
        if (StringUtils.isNotBlank(token)) {
            byte[] signature = signData(token.getBytes(UTF8));
            return Base64.encodeBase64String(signature);
        }
        return ret;
    }

    /**
     * Encrypt a string
     * @param token
     * @return
     * @throws Exception
     */
    @Override
    public String encrypt(String token) throws Exception {
        String ret = null;
        if (StringUtils.isNotBlank(token)) {
            byte[] encrypted = encryptOrDecrypt(token.getBytes(UTF8), Cipher.ENCRYPT_MODE);
            ret = Base64.encodeBase64String(encrypted);
        }
        return ret;
    }

    /**
     * Decrypt a string
     * @param token
     * @return
     * @throws Exception
     */
    @Override
    public String decrypt(String token) throws Exception {
        String ret = null;
        if (StringUtils.isNotBlank(token)) {
            byte[] data = Base64.decodeBase64(token);
            byte[] decrypted = encryptOrDecrypt(data, Cipher.DECRYPT_MODE);
            ret = new String(decrypted, UTF8);
        }
        return ret;
    }


    private byte[] encryptOrDecrypt(byte[] data, int mode) throws Exception {
        Cipher cipher = Cipher.getInstance(encryptionMethod, BouncyCastleProvider.PROVIDER_NAME);
        if (mode ==  Cipher.ENCRYPT_MODE ) {
            cipher.init(Cipher.ENCRYPT_MODE, getPrivateKey());
        }
        else {
            cipher.init(Cipher.DECRYPT_MODE, getPublicKey());
        }
        return cipher.doFinal(data);
    }

    private boolean verifSignedData(byte[] data, byte[] digitalSignature) throws Exception {
        Signature signature = Signature.getInstance(signingMethod, BouncyCastleProvider.PROVIDER_NAME);
        signature.initVerify(getPublicKey());
        signature.update(data);
        return signature.verify(digitalSignature);
    }

    private byte[] signData(byte[] data) throws Exception {
        Signature signature = Signature.getInstance(signingMethod, BouncyCastleProvider.PROVIDER_NAME);
        SecureRandom secureRandom = new SecureRandom();
        byte bytes[] = new byte[20];
        secureRandom.nextBytes(bytes);
        signature.initSign(getPrivateKey(), secureRandom);
        signature.update(data);

        return signature.sign();
    }

    public static void registerProvider() {
        boolean isProvider = false;
        Provider[]	providers = Security.getProviders();
        for(Provider provider: providers) {
            if (provider instanceof BouncyCastleProvider) {
                isProvider = true;
                break;
            }
        }
        if (!isProvider)
            Security.addProvider(new BouncyCastleProvider());
    }
}
