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

import java.sql.DriverManager;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.maiereni.sling.logs.interpreter.persistence.InterpreterDao;
import com.maiereni.sling.logs.interpreter.persistence.pojo.Entry;
import com.maiereni.sling.logs.interpreter.persistence.pojo.EntryIndex;

/**
 * Main class of the interpreter of the log files
 * @author Petre Maierean
 *
 */
@Component
public class AuthenticationLogsMain {
	private static final String PARSE = "parseFile";
	private static final Logger logger = LoggerFactory.getLogger(AuthenticationLogsMain.class);
	
	@Autowired
	private InterpreterDao interpreterDao;
	
	/**
	 * Parse and add entryes to the database
	 * @param sFile
	 * @throws Exception
	 */
	public void parseFile(final String sFile) throws Exception {
		logger.debug("Starts parsing the file " + sFile);
		try (RecordsReader reader = new RecordsReader(sFile)) {
			for(Entry entry = null; (entry = reader.getNextEntry()) != null;) {
				logger.debug("Add entry");
				interpreterDao.addEntry(entry);
			}
		}
	}
	
	/**
	 * Associates log Entries based on common Records 
	 * @throws Exception
	 */
	public void updateAssociates() throws Exception {
		List<EntryIndex> entries = interpreterDao.getEntryIndex();
		for(EntryIndex entry: entries) {
			updateAssociate(entry);
		}
	}
	
	private void updateAssociate(final EntryIndex entry) throws Exception {
		List<Integer> preAssociates = null;
		int count = 0;
		for(; count< entry.getMaxIndex(); count++) {
			List<Integer> associates = interpreterDao.getAssociatedEntryIndexes(entry.getId(), count);
			if (preAssociates != null && preAssociates.size() > associates.size()) {
				break;
			}
			preAssociates = associates;
			
		}
		if (!(preAssociates == null || preAssociates.size() == 0)) {
			logger.debug("Found the minimum number of associates for entry {} at index {}", new Object[] {entry.getId(), count});
			for(Integer associatedId : preAssociates) {
				interpreterDao.addOrUpdateAssociates(entry.getId(), associatedId, count);
			}
		}
		else {
			logger.debug("No associates found for entry {}", entry.getId());
			interpreterDao.addOrUpdateAssociates(entry.getId(), null, count);
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			if (args.length < 1) {
				throw new Exception("Expected argument: file_to_parse [step]");
			}
			String step = PARSE;
			if (args.length > 1)
				step = args[1];
			DriverManager.registerDriver(new org.postgresql.Driver());
			ClassPathXmlApplicationContext app = new ClassPathXmlApplicationContext("/application.xml");
			AuthenticationLogsMain main = app.getBean(AuthenticationLogsMain.class);
			if (step.equals(PARSE)) {
				main.parseFile(args[0]);
			}
			main.updateAssociates();
			app.close();
		}
		catch(Exception e) {
			logger.error("There was a failure processing", e);
		}
	}

}
