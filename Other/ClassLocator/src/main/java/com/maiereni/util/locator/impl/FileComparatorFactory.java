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
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class FileComparatorFactory extends BaseFileComparator {
	private Map<String, BaseFileComparator> comparators = new HashMap<String, BaseFileComparator>();
	private static final String[] PATTERNS = {"xml", "properties", "text"};
	public FileComparatorFactory() {
		comparators.put("xml", new XMLFileComparator());
		comparators.put("properties", new PropertiesFileComparator());
		comparators.put("text", new TextFileComparator());
		comparators.put("*", new BinaryFileComparator());		
	}
	
	public FileComparatorFactory(String configFile) throws Exception {
		this();
		if (configFile != null) {
			InputStream is = null;
			try{
				is = new FileInputStream(configFile);
				Properties props = new Properties();
				props.load(is);
				setProperties(props);
			}
			finally {
				if (is != null)
					try{
						is.close();
					}
				catch(Exception e){}
			}
		}
	}

	private boolean asTextOnly, asBinaryOnly;
	
	public void setProperties(Properties props) {
		super.setProps(props);
		comparators.get("xml").setProperties(props);
		comparators.get("properties").setProperties(props);
		comparators.get("text").setProperties(props);
		comparators.get("*").setProperties(props);
		asTextOnly = "true".equalsIgnoreCase(props.getProperty("compare.asText", "false"));
		asBinaryOnly = "true".equalsIgnoreCase(props.getProperty("compare.asBinary", "false"));
	}
	
	protected void doCompare(File f1, File f2, Writer report) throws Exception {
		String sel1 = getPattern(f1);
		String sel2 = getPattern(f2);
		BaseFileComparator comparator = null;
		if (sel1.equals(sel2))
			comparator = comparators.get(sel1);
		else
			comparator = comparators.get("*");
		comparator.doCompare(f1, f2, report);
	}

	private String getPattern(File f) {
		if (asTextOnly)
			return "text";
		if (asBinaryOnly)
			return "*";
		String name = f.getName();
		for(int i=0; i<FileComparatorFactory.PATTERNS.length; i++)
			if (name.endsWith(FileComparatorFactory.PATTERNS[i]))
				return FileComparatorFactory.PATTERNS[i];
		return "*";
	}
}
