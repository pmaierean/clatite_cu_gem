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
import java.io.InputStream;

import javax.annotation.Nonnull;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.maiereni.utils.lottoMax.DataProvider;

/**
 * @author Petre Maierean
 *
 */
public class CSVDataProviderFactory {
	/**
	 * Get a data provider
	 * @param csvFile
	 * @return
	 * @throws Exception
	 */
	public DataProvider getDataProvider(@Nonnull final String csvFile) throws Exception {
		return new CsvDataProviderImpl(csvFile, false);
	}
	
	/**
	 * Get a data provider
	 * @param csvFile
	 * @return
	 * @throws Exception
	 */
	public DataProvider getDataProviderFromResource(@Nonnull final String resource) throws Exception {
		try (InputStream is = getClass().getResourceAsStream(resource)) {
			if (is == null)
				throw new Exception("No such resource " + resource);
			File fTemp = File.createTempFile("lottoMax", "csv");
			byte[] content = IOUtils.toByteArray(is);
			FileUtils.writeByteArrayToFile(fTemp, content);
			return new CsvDataProviderImpl(fTemp.getPath(), true);
		}
	}
}
