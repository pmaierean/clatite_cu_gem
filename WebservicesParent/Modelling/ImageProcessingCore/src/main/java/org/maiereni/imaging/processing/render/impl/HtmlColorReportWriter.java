/** ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */
package org.maiereni.imaging.processing.render.impl;

import java.io.Closeable;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * @author Petre Maierean
 *
 */
class HtmlColorReportWriter implements Closeable {
	private static final String TPL = getHTMLTemplate();
	private String outputFile;
	private List<String> colors;
	
	public HtmlColorReportWriter(final String outputFile) {
		this.outputFile = outputFile;
	}
	
	public void setColors(final List<String> colors) {
		this.colors = colors;
	}
	
	@Override
	public void close() throws IOException {
		try (FileWriter sw = new FileWriter(outputFile)) {
			StringBuffer sb = new StringBuffer();
			StringBuffer sb1 = new StringBuffer();
			for(String color: colors) {
				String s = color.replaceAll("\\x2e0", "");
				String[] toks = s.split(":");
				if (sb.length() > 0) {
					sb.append(",");
					sb1.append(",");
				}
				sb.append("'#");
				for(int i=0;i<3; i++) {
					int n = Integer.parseInt(toks[i]);
					sb.append(Integer.toHexString(n));
				}
				sb.append("'");
				sb1.append("'").append(color).append("'");
			}
			sb.insert(0, "var hexCols = (").append(");\r\n");
			sb1.insert(0, "var cols = (").append(");\r\n").append(sb.toString());
			
			String res = String.format(TPL, sb1.toString());
			sw.write(res);
		}
	}
	
	private static String getHTMLTemplate() {
		StringBuffer sb = new StringBuffer();
		sb.append("<html>\r\n");
		sb.append("<head>\r\n");
		sb.append("\t\t<title>Color selector</title>\r\n");
		sb.append("\t\t</head>\r\n");
		sb.append("\t\t<body>\r\n");
		sb.append("\t\t\t<script type=\"text/javascript\">\r\n");
		sb.append("\t\t\t\t%s\r\n");
		sb.append("\t\t\t</script>\r\n");
		sb.append("\t\t\t<div class=\"picker\">&bnsp;</div>\r\n");
		sb.append("\t\t</body>\r\n");
		sb.append("\t\t</html>\r\n");		
		return sb.toString();
	}
}
