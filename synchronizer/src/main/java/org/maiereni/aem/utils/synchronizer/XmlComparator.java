/**
 * Copyright 2017 Maiereni Software and Consulting Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */
package org.maiereni.aem.utils.synchronizer;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Utility class that compares two XML files
 * @author Petre Maierean
 *
 */
abstract class XmlComparator {
	private DocumentBuilder documentBuilder;

	public XmlComparator() throws MojoExecutionException {
		try {
			documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		}
		catch(Exception e) {
			getLog().error("Failed to initialize a document builder", e);
			throw new MojoExecutionException("Failed to create comparator", e);
		}
	}
	/**
	 * Checks if a given tag name is to be ignored
	 * @param tagName
	 * @return
	 */
	public boolean ignoreTag(final String tagName) {
		return false;
	}
	/**
	 * Checks is a given attribute name is to be ignored
	 * @param attributeName
	 * @return
	 */
	public boolean ignoreAttribute(final String attributeName) {
		return false;
	}
	
	/**
	 * Verifies if any of the two files contain the same XML 
	 * @param f1
	 * @param f2
	 * @return
	 */
	public boolean isSame(final File f1, final File f2) throws MojoExecutionException {
		try {
			Document d1 = documentBuilder.parse(f1);
			Document d2 = documentBuilder.parse(f2);
			return isSame(d1.getDocumentElement(), d2.getDocumentElement());
		}
		catch(Exception e) {
			getLog().error("Failed to compare the two files", e);
			throw new MojoExecutionException("Failed to compare the two files", e);
		}
	}

	protected abstract Log getLog();

	
	private boolean isSame(final Element e1, final Element e2) throws Exception {
		boolean ret = true;
		String tagName1 = e1.getTagName();
		if (!ignoreTag(tagName1)) {
			if (!tagName1.equals(e2.getTagName()))
				ret = false;
			else if (!(isMatchAttributes(e1, e2) || isMatchAttributes(e2, e1)))
				ret = false;
			else {
				NodeList n1 = e1.getChildNodes();
				NodeList n2 = e2.getChildNodes();
				int nr1 = n1.getLength();
				int nr2 = n2.getLength();
				if (nr1 != nr2)
					ret = false;
				else
					for (int i=0; i < nr1; i++) {
						Node a1 = n1.item(i);
						if (a1 instanceof Element) {
							Element se1 = (Element)a1;
							if (!ignoreTag(se1.getTagName())) {
								NodeList nl2 = e2.getElementsByTagName(se1.getTagName());
								boolean match = false;
								for(int j=0; j<nl2.getLength(); j++) {
									if (isSame(se1, (Element)nl2.item(j))) {
										match = true;
										break;
									}
								}
								if (!match) {
									ret = false;
									break;
								}
							}
						}
					}
			}
		}
		return ret;
	}
	
	
	private boolean isMatchAttributes(final Element e1, final Element e2) throws Exception {
		boolean ret = true;
		NamedNodeMap nnm1 = e1.getAttributes();
		int size = nnm1.getLength();
		for(int i=0; i<size; i++) {
			Attr a1 = (Attr)nnm1.item(i);
			if (!hasMatchingAttribute(a1, e2)) {
				ret = false;
				break;
			}
		}
		return ret;
	}
	
	private boolean hasMatchingAttribute(final Attr attr, final Element e) throws Exception {
		boolean ret = true;
		String attrName = attr.getName();
		if (!ignoreAttribute(attrName)) {
			String attrValue1 = attr.getValue();
			String attrValue2 = e.getAttribute(attrName);
			if (!(attrValue2 != null && attrValue1.equals(attrValue2))) {
				ret = false;
			}
		}
		return ret;
	}
}
