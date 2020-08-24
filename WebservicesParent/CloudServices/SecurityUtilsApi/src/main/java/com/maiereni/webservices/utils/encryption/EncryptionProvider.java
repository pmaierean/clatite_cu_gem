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

/**
 * An API definition for an encryption provider
 */
public interface EncryptionProvider {
    /**
     * Validates is the token has been signed
     * @param token
     * @param signature
     * @return
     */
    boolean isSigned(String token, String signature);

    /**
     * Signs and encrypts a string
     * @param token
     * @return
     */
    String getSignature(String token) throws Exception;

    /**
     * Signs and encrypts a string
     * @param token
     * @return
     */
    String encrypt(String token) throws Exception;

    /**
     * Decrypt a token
     * @param token
     * @return
     * @throws Exception
     */
    String decrypt(String token) throws Exception;
}
