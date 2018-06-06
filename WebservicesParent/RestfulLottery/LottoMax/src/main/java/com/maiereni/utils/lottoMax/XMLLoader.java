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
package com.maiereni.utils.lottoMax;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Petre Maierean
 *
 */
public class XMLLoader {
	private static final Logger logger = LoggerFactory.getLogger(XMLLoader.class);

	public void convertXML(final String fin, final String fOut) throws Exception {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = builder.parse(new File(fin));
		Element root = doc.getDocumentElement();
		NodeList nl = root.getElementsByTagName("tbody");
		if (nl.getLength() == 1) {
			Element body = (Element) nl.item(0);
			nl = body.getElementsByTagName("tr");
			int size = nl.getLength();
			List<Draw> draws = new ArrayList<Draw>();
			StringBuffer sb = new StringBuffer();
			for(int i =1; i<size; i++) {
				Element el = (Element)nl.item(i);
				NodeList nll = el.getElementsByTagName("td");
				int sz1 = nll.getLength();
				if (sz1 < 9)
					continue;
				Draw draw = null;
				for ( int j=0; j<sz1; j++) {
					Element sel = (Element) nll.item(j);
					if (j == 0) {
						String txt = sel.getTextContent();
						draw = new Draw(txt);
					}
					else if (j < 8) {
						Integer nr = getNumber(sel);
						draw.numbers.add(nr);
					}
					else if (j == 8) {
						Integer nr = getNumber2(sel);
						draw.numbers.add(nr);
						draws.add(draw);
						if (sb.length() > 0)
							sb.append("\r\n");
						sb.append(draw.toString());
					}
				}
			}
			logger.debug("Found a number of {} draws", draws.size());
			FileUtils.writeStringToFile(new File(fOut), sb.toString());
		}
	}

	private Integer getNumber2(final Element el) throws Exception {
		NodeList nl = el.getElementsByTagName("button");
		Element e = (Element)nl.item(0);
		return getNumber(e);
	}
	
	private Integer getNumber(final Element el) throws Exception {
		NodeList nl = el.getElementsByTagName("b");
		Element e = (Element)nl.item(0);
		return new Integer(e.getTextContent());
	}
	
	public static void main(String[] args) {
		try {
			new XMLLoader().convertXML(args[0], args[1]);
		}
		catch(Exception e) {
			logger.error("Failed to process", e);
		}
	}
	private static final SimpleDateFormat SDF = new SimpleDateFormat("MMM dd, yyyy"); // Fri, Feb 9, 2018  
	private static final SimpleDateFormat SDF1 = new SimpleDateFormat("dd-MM-yyyy");
	private class Draw {
		private Date date;
		private List<Integer> numbers;
		public Draw(final String date) throws Exception {
			String s = date.trim();
			int ix = s.indexOf(",");
			s = s.substring(ix+1).trim();
			this.date = SDF.parse(s);
			this.numbers = new ArrayList<Integer>();
		}
		
		public String toString() {
			StringBuffer sb = new StringBuffer();
			for(Integer number: numbers) {
				if (sb.length() > 0)
					sb.append(",");
				sb.append(number);
			}
			sb.insert(0, ",").insert(0,SDF1.format(date));
			return sb.toString();
		}
	}
}
