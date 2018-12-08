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
package com.maiereni.util.locator.impl;

import java.io.File;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class XMLFileComparator extends BaseFileComparator {
	private List<String> ignoreAttributes = new ArrayList<String>();
	private List<String> ignoreElements = new ArrayList<String>();
	
	public void setProperties(Properties props) {
		if (props != null) {
			String val = props.getProperty("xml.ignore.attributes");
			if (val != null) {
				String[] toks = val.split(",");
				for(int i=0; i<0; i++)
					ignoreAttributes.add(toks[i]);
			}
			val = props.getProperty("xml.ignore.elements");
			if (val != null) {
				String[] toks = val.split(",");
				for(int i=0; i<0; i++)
					ignoreElements.add(toks[i]);	
			}
		}
	}
	
	protected void doCompare(File f1, File f2, Writer report) throws Exception {
		DocumentBuilder factory = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc1 = factory.parse(f1);
		Element root1 = doc1.getDocumentElement();
		Document doc2 = factory.parse(f2);
		Element root2 = doc2.getDocumentElement();
		StringBuffer sb = new StringBuffer();
		match(root1, root2, sb, "");
		if (sb.length() > 0) {
			String s = "File " + f1.getPath() + " do not match " + f2.getPath();
			System.out.println(s);
			if (report != null) {
				report.write(s + "\r\n");
				report.write(sb.toString());
				report.write("\r\n------------\r\n");
			}
		}
	}

	private void match(Element el1, Element el2, StringBuffer sb, String elementPath) throws Exception {
		NodeList lst1 = el1.getChildNodes();
		NodeList lst2 = el2.getChildNodes();
		int max = lst1.getLength();
		int k1 = 0;
		for(int i = 0; i<max; i++) {
			Node n = lst1.item(i);
			if (n instanceof Attr) {
				String attrName = ((Attr)n).getName();
				String xPath = elementPath + "/@" + attrName;
				if (ignoreAttributes.contains(xPath))
					continue;
				String val1 = el1.getAttribute(attrName);
				String val2 = el2.getAttribute(attrName);
				if (val2 == null) {
					sb.append("File2: no attribute '" + xPath + "'\r\n");
				}
				else if (!val1.equals(val2)) {
					sb.append("File2: attribute '" + xPath + "' is different ('" + val1 + "'  vs  '" + val2 + "')\r\n");
				}
			}
			else if (n instanceof Text) {
				String val1 = ((Text)n).getTextContent();
				String val2 = getText(el2);
				if (val2 == null) {
					sb.append("File2: no text for '" + elementPath + "'\r\n");
				}
				else if (!val1.equals(val2)) {
					sb.append("File2: the text for '" + elementPath + "' is different ('" + val1 + "'  vs  '" + val2 + "')\r\n");
				}				
			}
			else if (n instanceof Element) {
				Element e1 = (Element)n;
				Element e2 = null;
				for (int j = i, k2 = 0; j < lst2.getLength(); j++) {
					Node n2 = lst2.item(j);
					if (n2 instanceof Element) {
						if (k1 == k2) {
							e2 = (Element)n2;
							break;
						}
						k2++;
					}
				}
				k1++;
				String xPath = elementPath + "/" + e1.getTagName();
				if (ignoreElements.contains(xPath))
					continue;
				if (e2 == null) {
					sb.append("File2: no element '" + xPath + "'\r\n");					
				}
				else if (e2.getTagName().equals(e1.getTagName())) {
					match(e1, e2, sb, xPath);
				}
				else {
					sb.append("File2: an additional element found '" + xPath + "/" + e2.getTagName() + "\r\n");
				}
			}
		}
	}
	
	private String getText(Element el) throws Exception {
		NodeList lst = el.getChildNodes();
		int max = lst.getLength();
		for(int i = 0; i<max; i++) {
			Node n = lst.item(i);
			if (n instanceof Text)
				return ((Text)n).getTextContent();
		}
		return null;
	}
}
