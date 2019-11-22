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
package com.maiereni.host.sevice;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.maiereni.host.bo.Row;

/**
 * @author Petre Maierean
 *
 */
@Configuration
public class ServiceFactory {
	private static final Logger logger = LoggerFactory.getLogger(ServiceFactory.class);

	@Bean(name="pageService")
	public PageService getPageService() throws Exception {
		List<Row> original = getOriginal();
		List<String> init = getInitialValues();
		
		return new PageService(original, init);
	}
	
	private List<Row> getOriginal() throws Exception {
		String sName = System.getProperty("original.file", "C:\\Users\\Petre\\Documents\\books\\istorie\\Voynich\\pg1-flip.csv");
		List<Row> original = new ArrayList<Row>();
		try (FileInputStream fis = new FileInputStream(sName);
			InputStreamReader ir = new InputStreamReader(fis);
			LineNumberReader lnr = new LineNumberReader(ir)) {
			String ln = null;
			while((ln = lnr.readLine()) != null) {
				String[] arr = ln.split(",");
				Row r = new Row();
				List<String> rows = new ArrayList<String>();
				rows.addAll(Arrays.asList(arr));
				r.setCols(rows);
				original.add(r);
			}
		}
		logger.debug("The original has been loaded from " + sName);
		return original;
	}

	private List<String> getInitialValues() throws Exception {
		List<String> ret = null;
		String sName = System.getProperty("initial.values", "C:\\Users\\Petre\\Documents\\books\\istorie\\Voynich\\values.csv");
		File f = new File(sName);
		if (f.exists()) {
			try (FileInputStream fis = new FileInputStream(sName);
				InputStreamReader ir = new InputStreamReader(fis);
				LineNumberReader lnr = new LineNumberReader(ir)) {
				String ln = lnr.readLine();
				String[] arr = ln.split(",");
				ret = new ArrayList<String>();
				ret.addAll(Arrays.asList(arr));
			}
		}
		logger.debug("The initial values have been loaded from " + sName);
		return ret;
	}
	
	
}
