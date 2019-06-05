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
package com.maiereni.jaas.handler;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import com.maiereni.jaas.handler.bo.TraceRecord;
import com.maiereni.jaas.handler.bo.TraceRecords;

/**
 * Parse an exception string to a list of Trace Records
 * @author Petre Maierean
 *
 */
class TraceRecordParser {
	private static final String AT = "at ";
	private static final String BRS = "(";
	private static final String BRE = ")";
	private static final String SC = ":";
	private static final String SQS = "[";
	private static final String SQE = "]";
	private static final String CALLER_START = "at org.apache.sling.resourceresolver.impl.ResourceResolverFactoryImpl.getServiceResourceResolver";
	
	public TraceRecords parse(final String s) throws Exception {
		TraceRecords ret = new TraceRecords();
		List<TraceRecord> trs = new ArrayList<TraceRecord>();
		ret.setRecords(trs);
		StringBuffer sb = new StringBuffer();
		try(ByteArrayInputStream bis = new ByteArrayInputStream(s.getBytes());
			InputStreamReader reader = new InputStreamReader(bis);
			LineNumberReader lnr = new LineNumberReader(reader)) {
			String ln = null;
			while((ln = lnr.readLine()) != null) {
				int i = ln.indexOf(AT);
				if (sb.length() > 0) {
					sb.append("\r\n").append(ln);
				}
				else if (ln.indexOf(CALLER_START) > 0) {
					sb.append(ln);
				}
				int j = ln.indexOf(BRS);
				int k = ln.indexOf(SC);
				int l = ln.indexOf(BRE);
				if (i > 0 && j > i && k > j && l > k) {
					TraceRecord tr = new TraceRecord();
					String c = ln.substring(i + AT.length(), j);
					int m = c.lastIndexOf(".");
					if (m > 0)
						c = c.substring(0, m);
					tr.setClassName(c);
					tr.setCodeLine(Integer.parseInt(ln.substring(k+1, l)));
					i = ln.indexOf(SQS, l);
					j = ln.indexOf(SQE, l);
					if (i > 0 && j > i) {
						tr.setBundle(ln.substring(i+1,  j));
					}
					trs.add(tr);
				}
			}
		}
		ret.setLine(sb.toString());
		return ret;
	}
}
