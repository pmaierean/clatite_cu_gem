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
package com.maiereni.osgi.felix.utils.model;

import java.io.File;
import java.io.FileInputStream;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang3.StringUtils;

import com.maiereni.sling.info.Model;
import com.maiereni.sling.info.ObjectFactory;

/**
 * A model reader
 * @author Petre Maierean
 *
 */
class SlingModelReader {
	private Unmarshaller unmarshaller;
	
	public SlingModelReader(final ObjectFactory factory, final Unmarshaller unmarshaller) {
		this.unmarshaller = unmarshaller;
	}
	
	
	public Model loadFromXml(String xmlFile) throws Exception {
		Model ret = null;
		if (StringUtils.isNoneBlank(xmlFile)) {
			try (FileInputStream fi = new FileInputStream(new File(xmlFile))) {
				JAXBElement<Model> m = unmarshaller.unmarshal(new StreamSource(fi), Model.class);
				ret = m.getValue();
			}
		}
		return ret;
	}

}
