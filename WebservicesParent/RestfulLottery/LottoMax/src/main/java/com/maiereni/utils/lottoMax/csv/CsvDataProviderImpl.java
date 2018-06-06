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
package com.maiereni.utils.lottoMax.csv;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.cellprocessor.ParseDate;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import com.maiereni.utils.lottoMax.DataProvider;
import com.maiereni.utils.lottoMax.bo.NumberRecord;
import com.maiereni.utils.lottoMax.bo.NumberRecords;

/**
 * An implementation of the DataProvider that reads info from a CSV file
 * @author Petre Maierean
 *
 */
public class CsvDataProviderImpl implements DataProvider {
	private static final CellProcessor[] PROCESSORS = getProcessors();
	private static final Logger logger = LoggerFactory.getLogger(CsvDataProviderImpl.class);
	private File sourceFile;
	private boolean remove;
	
	CsvDataProviderImpl(@Nonnull final String csvFilePath, boolean remove) throws Exception {
		sourceFile = new File(csvFilePath);
		if (!sourceFile.exists())
			throw new Exception("Cannot locate the file");
		this.remove = remove;
	}
	
	/**
	 * Retrieve records for a date 
	 * 
	 * @param date
	 * @return records
	 * @throws Failed to process
	 */
	@Override
	public NumberRecords getNumberRecords(@Nonnull final Date date) throws Exception {
		NumberRecords ret = null;
		logger.debug("Parse the file at " + sourceFile.getPath());
        try (ICsvBeanReader beanReader = new CsvBeanReader(new FileReader(sourceFile), CsvPreference.STANDARD_PREFERENCE)) {
			// the header elements are used to map the values to the bean (names must match)
			final String[] header = beanReader.getHeader(true);
			
			List<NumberRecord> records = new ArrayList<NumberRecord>();
			NumberRecord record;
			int maxFrequency = 0, minFrequency =10000, maxAge = 0, minAge = 10000;
			while( (record = beanReader.read(NumberRecord.class, header, PROCESSORS)) != null ) {
				records.add(record);
				if (record.getFrequency() > maxFrequency)
					maxFrequency = record.getFrequency();
				if (record.getFrequency() < minFrequency)
					minFrequency = record.getFrequency();
				if (record.getDaysAgo() > maxAge)
					maxAge = record.getDaysAgo();
				if (record.getDaysAgo() < minAge)
					minAge = record.getDaysAgo();
			}
			logger.debug("Found a number of {} records", records.size());
			logger.debug("Max ago = {}, MinAge = {}, MaxFrequency = {}, MinFrequency = {}", maxAge, minAge, maxFrequency, minFrequency);
			ret = new NumberRecords();
			ret.setMaxAge(maxAge);
			ret.setMaxFrequency(maxFrequency);
			ret.setMinAge(minAge);
			ret.setMinFrequency(minFrequency);
			ret.setNumberRecords(records);
        }
		
		return ret;
	}
	
	private static final CellProcessor[] getProcessors() {        
        return new CellProcessor[] { 
            new ParseInt(), // Number
            new ParseInt(), // Frequency
            new ParseInt(), // Drawn Ago
            new ParseInt(), // Drawn Days Ago
            new ParseDate("yyyy-MM-dd") // Last drawn date
        };
	}

	@Override
	public void close() throws IOException {
		if (remove)
			if (sourceFile != null) {
				if (!sourceFile.delete())
					sourceFile.deleteOnExit();
				logger.debug("The file at {} has been deleted", sourceFile.getPath());
			}
	}
}
