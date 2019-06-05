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
package com.maiereni.sling.logs.interpreter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.maiereni.sling.logs.interpreter.persistence.pojo.Record;

/**
 * @author Petre Maierean
 *
 */
public class RecordsReaderTest {
	private RecordsReader recordsReader = new RecordsReader();
	
	@Test
	public void testParsing() {
		Record r = recordsReader.getRecord("	at org.apache.sling.resourceresolver.impl.ResourceResolverFactoryImpl.getServiceResourceResolver(ResourceResolverFactoryImpl.java:79)");
		assertNotNull("Not null", r);
		assertEquals("Expected classname", "org.apache.sling.resourceresolver.impl.ResourceResolverFactoryImpl.getServiceResourceResolver", r.getClassName());
		assertEquals("Expected line number", "79", "" + r.getLineNumber());
	}

}
