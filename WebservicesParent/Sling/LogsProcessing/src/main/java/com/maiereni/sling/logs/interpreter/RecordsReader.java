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
package com.maiereni.sling.logs.interpreter;

import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.HashSet;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maiereni.sling.logs.interpreter.persistence.pojo.Entry;
import com.maiereni.sling.logs.interpreter.persistence.pojo.Record;

/**
 * @author Petre Maierean
 *
 */
class RecordsReader implements Closeable {
	private static final String LINE = "com.maiereni.jaas.handler.DetectSourceCache Detected new job for user null ";
	private static final String SQBRB = "[";
	private static final String SQBRE = "]";
	
	private static final String AT = "at ";
	private static final Logger logger = LoggerFactory.getLogger(RecordsReader.class);
	private FileReader fileReader;
	private LineNumberReader lineNumberReader;
	private String saved;
	private int line;
	
	public RecordsReader(final String fileName) throws Exception {
		this();
		if (StringUtils.isBlank(fileName))
			throw new Exception("The file name cannot be null");
		File f = new File(fileName);
		if (!f.exists())
			throw new Exception("The file does not exist");
		fileReader = new FileReader(f);
		lineNumberReader = new LineNumberReader(fileReader);
	}
	
	RecordsReader() {
	}
	
	public Entry getNextEntry() throws Exception {
		Entry ret = null;
		int index = 0;
		for(String s = saved; (s = lineNumberReader.readLine()) != null;) {
			line++;
			try {
				if (s != null) {
					if (s.indexOf(LINE) > 0) {
						if (ret != null) {
							saved = s;
							break;
						}
						int i = s.indexOf(SQBRB);
						int j = s.indexOf(SQBRE);
						if (i>0 && j>i) {
							String threadName = s.substring(i+1, j);
							ret = new Entry();
							ret.setLineNumber(line);
							ret.setThreadName(threadName);
							ret.setRecords(new HashSet<Record>());
						}
					}
					else if (ret != null && s.indexOf(AT) > 0) {
						Record r = getRecord(s);
						if (r == null)
							throw new Exception("Invalid line");
						r.setIndex(index++);
						ret.getRecords().add(r);
					}
				}
			}
			catch(Exception e) {
				logger.error("Cannot parse at line: " + line, e);
				throw e;
			}
		}
		return ret;
	}
	
	protected Record getRecord(final String s) {
		Record ret = new Record();
		String[] toks = s.split("[\\x20\\x28\\x29\\x3a]");
		ret.setClassName(toks[1]);
		try {
			ret.setLineNumber(Integer.parseInt(toks[3]));
		}
		catch(Exception e) {
			ret.setLineNumber(-1);
		}
		return ret;
	}
	
	@Override
	public void close() throws IOException {
		if (lineNumberReader != null) {
			try {
				lineNumberReader.close();
			}
			catch(Exception e) {
				logger.error("Failed to close the Line Number Reader", e);
			}
		}
		if (fileReader != null) {
			try {
				fileReader.close();
			}
			catch(Exception e) {
				logger.error("Failed to close the file reader", e);
			}
		}
	}

}
