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
import java.io.StringWriter;
import java.io.Writer;
import java.util.Properties;

import com.maiereni.util.locator.FileComparator;

public abstract class BaseFileComparator implements FileComparator {
	private Properties props;
	private String[] ignore;
	
	protected abstract void doCompare(File f1, File f2, Writer report) throws Exception ;
	
	public void compareFiles(File f1, File f2, Writer report) throws Exception {
		StringWriter sw = new StringWriter();
		if (f1 == null || f2 == null) 
			throw new Exception("Null arguments");
		if (!f1.isFile()) {
			System.out.println("Argument 1 does not point to a file");
			sw.write("Argument 1 does not point to a file\r\n");
			return;
		}
		else if (!f2.isFile()) {
			System.out.println("Argument 2 does not point to a file");
			sw.write("Argument 2 does not point to a file\r\n");
		} else
			doCompare(f1, f2, sw);
		String s = sw.toString();
		if (s != null && s.length() > 0) {
			if (report != null) {
				report.write(f2.getPath() + "\r\n---------------\r\n");
				report.write(s + "\r\n-------------------\r\n");
			}
		}
	}
	
	protected void writeToReport(Writer report, String s) throws Exception {
		System.out.println(s);
		if (report != null)
			report.write(s + "\r\n");
	}

	public Properties getProps() {
		return props;
	}

	public void setProps(Properties props) {
		this.props = props;
		String s = props.getProperty("ignore.pattern");
		if (s != null)
			ignore = s.split(";");
	}

	public boolean accept(File f) {
		if (!(f == null  || ignore == null)) {
			String name = f.getName();
			if ("ejbModule".equals(name))
				System.out.println("Here");
			for(int i=0; i<ignore.length; i++)
				if (name.matches(ignore[i]))
					return false;
		}
		return true;
	}
}
