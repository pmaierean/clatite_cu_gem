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

import org.junit.Before;
import org.junit.Test;

import javax.swing.plaf.synth.SynthTextAreaUI;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 *
 * @author Petre Maierean
 */
public class EncryptionProviderImplTest {
    private static final SecurityConfiguration CONFIG = initConfig();
    private EncryptionProvider provider;

    @Before
    public void setUp() throws Exception {
        provider = new EncryptionProviderImpl(CONFIG);
    }

    @Test
    public void testSigning() {
        try {
            String signature = provider.getSignature("My very simple token");
            assertNotNull("Some value", signature);
            boolean b = provider.isSigned("My very simple token", signature);
            assertTrue("Is signed", b);
        }
        catch(Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testEncrypting() {
        try {
            String plainText = "This is another simple one";
            String encrypted = provider.encrypt(plainText);
            assertNotNull("Some value", encrypted);
            String decrypted = provider.decrypt(encrypted);
            assertNotNull("Decrypted value is", decrypted);
            assertEquals("", plainText, decrypted);
        }
        catch(Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testEncryptingLonger() {
        try {
            String plainText = "{\"uuid\":\"" + UUID.randomUUID().toString() +"\",\"domain\":\"www.mamuth.com\",\"date\":\"08/23/2020 18:35:48\"}";
            String encrypted = provider.encrypt(plainText);
            assertNotNull("Some value", encrypted);
            String decrypted = provider.decrypt(encrypted);
            assertNotNull("Decrypted value is", decrypted);
            assertEquals("", plainText, decrypted);
        }
        catch(Exception e) {
            e.printStackTrace();
            fail();
        }
    }


    private static SecurityConfiguration initConfig() {
        SecurityConfiguration ret = new SecurityConfiguration();
        ret.setKeyStore("/sample.jks");
        ret.setKeyAlias("sample");
        ret.setKeyPass("changeit");
        ret.setKeyStorePass("changeit");
        ret.setKeyStoreType("JKS");
        ret.setEncryptionMethod("RSA/ECB/PKCS1Padding");
        return ret;
    }
}
