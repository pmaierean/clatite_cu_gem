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
package com.maiereni.osgi.felix.utils.validator;

import java.io.File;
import java.io.FileInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Validates that all the service references are actually met for any bundle
 * @author Petre Maierean
 *
 */
public class ServiceReferenceChecker {
	private static final Logger logger = LoggerFactory.getLogger(ServiceReferenceChecker.class);
	private DocumentBuilder documentBuilder;
	private XPathFactory xpathFactory;
	
	public ServiceReferenceChecker() throws Exception {
		documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		xpathFactory = XPathFactory.newInstance();
	}
	
	public boolean validate(final File f) throws Exception {
		boolean ret = false;
		try(FileInputStream fis = new FileInputStream(f)) {
			Document document = documentBuilder.parse(fis);
			XPath xpath = xpathFactory.newXPath();
			NodeList nl = (NodeList)xpath.evaluate("//component/reference[@status='Satisfied']", document, XPathConstants.NODESET);
			logger.debug("Found a number of {} satisfied references", nl.getLength());
			for(int i=0; i<nl.getLength(); i++) {
				Element el = (Element) nl.item(i);
				Element p = (Element)el.getParentNode();
				String serviceId = el.getAttribute("serviceId");
				String type = el.getAttribute("attrName");
				String cardinality = el.getAttribute("cardinality");
				if (StringUtils.isBlank(serviceId) && !cardinality.startsWith("0")) {
					NodeList nll = (NodeList)xpath.evaluate("//service/type[text()='" + type + "']", document, XPathConstants.NODESET);
					if (nll.getLength() == 0) {
						logger.error("Invalid reference in component " + p.getAttribute("id") + " of type " + type + ". Missing id and type cannot be resolved");					
					}
				}
				else {
					NodeList nll = (NodeList)xpath.evaluate("//services/service[@id='" + serviceId + "']", document,  XPathConstants.NODESET);
					if (nll.getLength() == 0) {
						if (!cardinality.startsWith("0"))
							logger.error("Invalid reference in component " + p.getAttribute("id") + " of type " + type + ". Cannot resolve the serviceId " + serviceId);						
					}
					else if (nll.getLength() == 1) {
						Element se = (Element) nll.item(0);
						if (!matchType(se, type)) {
							logger.error("Invalid reference in component " + p.getAttribute("id") + " of type " + type + ". Resolve the serviceId " + serviceId + " to a wrong type{s) ");													
						}
					}
					else if (nll.getLength() > 1) {
						if (cardinality.equals("0..n")) {
							for(int j=0; j<nll.getLength(); j++) {
								Element se = (Element) nll.item(j);
								if (!matchType(se, type)) {
									logger.error("Invalid reference in component " + p.getAttribute("id") + " of type " + type + ". Resolve the serviceId " + serviceId + " to a wrong type{s) ");													
								}
							}
						}
						else {
							logger.error("Invalid reference in component " + p.getAttribute("id") + " of type " + type + ". Too many references " + serviceId);	
						}
					}
				}
			}
		}
		return ret;
	}
	
	private boolean matchType(final Element el, final String type) throws Exception {
		boolean ret = false;
		if (type.equals("javax.jcr.Repository")) {
			logger.debug("Here");
		}
		NodeList nl = el.getElementsByTagName("type");
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<nl.getLength(); i++) {
			String sType = ((Element)nl.item(i)).getTextContent();
			if (type.equals(sType)) {
				ret = true;
				break;
			}
			if (sb.length() > 0)
				sb.append(",");
			sb.append(sType);
		}
		if (!ret)
			logger.error("Found types: " + sb.toString());
		return ret;
	}
	
	public static void main(String[] args) {
		try {
			if (args.length == 0) {
				throw new Exception("Expected argument: model_file.xml");
			}
			ServiceReferenceChecker checker = new ServiceReferenceChecker();
			checker.validate(new File(args[0]));
		}
		catch(Exception e) {
			logger.error("Failed to process", e);
		}
	}
}
