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
package com.maiereni.synchronizer.jcr.utils;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author Petre Maierean
 *
 */
public class SlingManifestFileBuilderTest {
	private static final String VAL = "SLING-INF/content/consulting;overwrite:=true;uninstall:=true;path:=/content/consulting;SLING-INF/apps/consulting;overwrite:=true;uninstall:=true;path:=/apps/consulting;SLING-INF/design/consulting;overwrite:=true;uninstall:=true;path:=/design/consulting";
	private static final String FORMATED_VAL = "Sling-Initial-Content: SLING-INF/content/consulting;overwrite:=true;un\r\n" + 
			                                   " install:=true;path:=/content/consulting;SLING-INF/apps/consulting;ove\r\n" + 
			                                   " rwrite:=true;uninstall:=true;path:=/apps/consulting;SLING-INF/design/\r\n" + 
			                                   " consulting;overwrite:=true;uninstall:=true;path:=/design/consulting\r\n";
	@Test
	public void testFormatEntry() {
		SlingManifestFileBuilder mfBuilder = new SlingManifestFileBuilder();
		String s = mfBuilder.formatEntry(SlingManifestFileBuilder.INITIAL_CONTENT_TPML, VAL);
		assertEquals("Expected value", FORMATED_VAL, s);
	}
}
