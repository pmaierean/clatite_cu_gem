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
import java.io.FileReader;
import java.io.LineNumberReader;
import java.io.Writer;
import java.util.Properties;

public class TextFileComparator extends BaseFileComparator {
	
	public void setProperties(Properties props) {
	
	}
	
	protected void doCompare(File f1, File f2, Writer report) throws Exception {
		FileReader reader1  = null;
		FileReader reader2 = null;
		String s1, s2;
		try(
			LineNumberReader lnr1 = new LineNumberReader(reader1 = new FileReader(f1));
			LineNumberReader lnr2 = new LineNumberReader(reader2 = new FileReader(f2));		) {
			for(int i=0;i<10000000;i++) {
				s1 = lnr1.readLine();
				s2 = lnr2.readLine();
				if (s1 == null && s2 == null)
					break;
				if (s1 == null)
					writeToReport(report, "File 1 is missing: '" + s2 + "'");
				else if (s2 == null)
					writeToReport(report, "File 2 is missing: '" + s1 + "'");
				else if (!s1.equals(s2))
					writeToReport(report, "'" + s1 + "'  !=  '" + s2 + "'");
			}
		}
		finally {
			if (reader1 != null)
				try{
					reader1.close();
				}
			catch(Exception e){}
			if (reader2 != null)
				try{
					reader2.close();
				}
			catch(Exception e){}
		}
	}

}
