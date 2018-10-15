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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Issuer;

/**
 * Unit test class for the SAMLUtils
 * @author Petre Maierean
 *
 */
public class SAMLUtilsTest {
	private SAMLUtils samlUtils;
	

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		samlUtils = new SAMLUtils();
	}

	@Test
	public void testResponseUnsignedAssertionUnmarshal() {
		try (InputStream is = getClass().getResourceAsStream("/samples/response_unsigned_assertion.xml")){
			String sXml = IOUtils.toString(is);
			Response response = samlUtils.fromString(Response.class, sXml);
			assertNotNull("Unexpected response", response);
			assertEquals("InResponseTo expected value", "34ec4de2-25ef-456c-8135-0e54e2383ac2", response.getInResponseTo());
			assertEquals("Issuer expected value", "https://idp.maiereni.com/sso", response.getIssuer().getValue());	
		}
		catch(Exception e) {
			fail("Failed to unmarshal object");
		}
	}
	
	@Test
	public void testCreateObject() {
		try {
			Issuer issuer = samlUtils.newXMLObject(Issuer.DEFAULT_ELEMENT_NAME, Issuer.class);
			assertNotNull(issuer);
		}
		catch(Exception e) {
			fail("Failed to create object");			
		}
	}
	@Test
	public void testCreateNullObject() {
		try {
			Issuer issuer = samlUtils.newXMLObject(null, Issuer.class);
			assertNotNull(issuer);
		}
		catch(Exception e) {
			e.printStackTrace();
			fail("Failed to create object");			
		}
	}
}
