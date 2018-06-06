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
package com.maiereni.utils.lottoMax;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maiereni.utils.lottoMax.bo.NumberRecords;
import com.maiereni.utils.lottoMax.bo.WeighedNumber;
import com.maiereni.utils.lottoMax.csv.CSVDataProviderFactory;

/**
 * Unit test for the weighing processor
 * @author Petre Maierean
 *
 */
public class WeighingProcessorTest {
	private static final Logger logger = LoggerFactory.getLogger(WeighingProcessorTest.class);
	private CSVDataProviderFactory factory = new CSVDataProviderFactory();
	private WeighingProcessor processor = new WeighingProcessor();
	
	@Test
	public void testWithCSV() throws Exception {
		try (DataProvider dataProvider = factory.getDataProviderFromResource("/numbers.csv")) {
			NumberRecords numberRecords = dataProvider.getNumberRecords(new Date());
			assertNotNull("The input is not null", numberRecords);
			assertNotNull("The input is not null", numberRecords.getNumberRecords());
			List<WeighedNumber> weighedNumbers = processor.process(numberRecords);
			assertNotNull("Not null", weighedNumbers);
			assertEquals("Equals in size", numberRecords.getNumberRecords().size(), weighedNumbers.size());
			logger.debug("The result is \r\n" + listResult(weighedNumbers));
		}
		catch(Exception e) {
			logger.error("Failed to test", e);
			fail("Unexpected failure");
		}
	}
	
	private static final String TEMPLATE = "%d,%d,%d,%s";
	private String listResult(final List<WeighedNumber> results) {
		StringBuffer sb = new StringBuffer();
		for(WeighedNumber n: results) {
			sb.append(String.format(TEMPLATE, n.getNumber(),n.getDaysAgo(), n.getFrequency(), "" + n.getWeight1().intValue())).append("\r\n");
		}
		return sb.toString();
	}

}
