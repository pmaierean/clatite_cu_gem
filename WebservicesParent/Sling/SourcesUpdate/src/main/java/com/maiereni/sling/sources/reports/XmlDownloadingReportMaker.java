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
package com.maiereni.sling.sources.reports;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.maiereni.sling.sources.BaseProjectDownloader;
import com.maiereni.sling.sources.DownloaderListener;
import com.maiereni.sling.sources.bo.Project;
import com.maiereni.sling.sources.bo.ProjectReport;
import com.maiereni.sling.sources.bo.ReportItem;
import com.maiereni.sling.sources.bo.Repository;

/**
 * Downloading Report file maker class
 * @author Petre Maierean
 *
 */
public class XmlDownloadingReportMaker implements DownloaderListener, Closeable {
	private static final Logger logger = LoggerFactory.getLogger(XmlDownloadingReportMaker.class);
	private String reportFile;
	private Map<String, ProjectReport> projectReports;
	
	public XmlDownloadingReportMaker(final String reportFile) {
		this.reportFile = reportFile;
	}
	
	@Override
	public void close() throws IOException {
/*		if (StringUtils.isNotBlank(reportFile)) {
	        try (StringWriter sw = new StringWriter()) {
				File f = new File(reportFile);
				File parentDir = f.getParentFile();
				if (!parentDir.isDirectory()) {
					if (!parentDir.mkdirs())
						throw new Exception("Cannot make folder at: " + parentDir.getPath());
				}
				logger.debug("Create report file: " + f.getPath());
				Document document = createXMLReport();
				
	            Transformer transformer = TransformerFactory.newInstance().newTransformer();
			    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
	            transformer.transform(new DOMSource(document), new StreamResult(sw));
	            FileUtils.writeStringToFile(f, sw.toString());
	            logger.debug("Successfully created the report file");
	        }
			catch(Exception e) {
				logger.error("Failed to create report file due to an exception", e);
			}
		}
*/
	}

	@Override
	public void notify(final Repository repository, final String step, final String description) {
	}	
}
