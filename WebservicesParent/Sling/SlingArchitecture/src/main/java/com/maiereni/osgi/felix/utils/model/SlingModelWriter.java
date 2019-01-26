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
import java.io.FileOutputStream;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maiereni.sling.info.Model;
import com.maiereni.sling.info.ObjectFactory;

/**
 * Writes models to XML file
 * @author Petre Maierean
 *
 */
class SlingModelWriter {
	private static final Logger logger = LoggerFactory.getLogger(SlingModelWriter.class);
	private ObjectFactory factory;
	private Marshaller marshaller;
	
	public SlingModelWriter(final ObjectFactory factory, final Marshaller marshaller) {
		this.factory = factory;
		this.marshaller = marshaller;
	}
	/**
	 * Writes the sling model into a file
	 * @param model
	 * @param xmlFile
	 * @throws Exception
	 */
	public void writetoXML(final Model model, final String xmlFile) throws Exception {
		if (model != null && StringUtils.isNotBlank(xmlFile)) {
			logger.debug("Write model to file");
			try (FileOutputStream os = new FileOutputStream(new File(xmlFile))) {
				JAXBElement<Model> jaxbElement = factory.createModel(model);
				marshaller.marshal(jaxbElement, os);
				logger.debug("File has writtern to " + xmlFile);
			}
		}
	}

}
