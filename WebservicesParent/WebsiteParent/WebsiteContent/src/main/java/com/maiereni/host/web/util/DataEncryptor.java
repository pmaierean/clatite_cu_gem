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
package com.maiereni.host.web.util;

/**
 * The API of a data encryptor
 * @author Petre Maierean
 *
 */
public interface DataEncryptor {
	/**
	 * Encrypt the content of an array of bytes
	 * @param data
	 * @return
	 * @throws Exception
	 */
	byte[] encryptData(byte[] data) throws Exception;
	/**
	 * Decrypt the content of the array of bytes
	 * @param encryptedData
	 * @return
	 * @throws Exception
	 */
	byte[] decryptData(byte[] encryptedData) throws Exception;
}
