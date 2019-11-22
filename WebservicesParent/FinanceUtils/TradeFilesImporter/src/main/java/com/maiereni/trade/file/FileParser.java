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
package com.maiereni.trade.file;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.maiereni.trade.file.bo.Country;
import com.maiereni.trade.file.bo.Partner;
import com.maiereni.trade.file.bo.TradeItem;

/**
 * @author Petre Maierean
 *
 */
public class FileParser {
	private static final Logger logger = Logger.getLogger(FileParser.class);
	protected List<String> states;
	
	public FileParser(final List<String> states) {
		this.states = states;
	}
	
	
	/**
	 * Parse an XLSX file 
	 * @param f
	 * @return
	 * @throws Exception
	 */
	public Country parse(final File f) throws Exception {
		Country ret = null;
        int year = -1;
		logger.error("Parse the file at: " + f.getPath());
        try (XSSFWorkbook workBook = new XSSFWorkbook(f);) {
	        XSSFSheet selSheet = workBook.getSheetAt(0);
	        if (selSheet.getSheetName().equals("Sheet1")) {
	        	selSheet = workBook.getSheetAt(1);
	        }
	        Iterator<Row> rowIterator = selSheet.iterator();
			for (int count = 0;rowIterator.hasNext(); count++) {
	            Row row = rowIterator.next();				
				if (count > 0) {
					if (ret == null) {
						ret = new Country();
						ret.setPartners(new ArrayList<Partner>());
					}
					Partner partner = new Partner();
					partner.setTradeItems(new ArrayList<TradeItem>());
					TradeItem ti = new TradeItem();
					partner.getTradeItems().add(ti);
					int type = -1;
		            Iterator<Cell> cellIterator = row.cellIterator();
					for(int i=0; cellIterator.hasNext(); i++) {
						Cell cell = cellIterator.next();
						String s = null;
						switch (cell.getCellTypeEnum()) {
		                case STRING:
		                	s = cell.getStringCellValue();
		                	if (i == 0) {
		                		ret.setName(s);
		                	}
		                	else if (i == 1) {
		                		partner.setName(s);
		                	}
		                	else if (i == 3) {
		                		if (s.equals("Export")) {
		                			type = 0;
		                		}
		                		else if (s.equals("Import")) {
		                			type = 1;
		                		}
		                	}
		                    break;
		                case NUMERIC:
		                	if (i == 2) {
		                		year = new BigDecimal(cell.getNumericCellValue()).intValue();
		                		ti.setYear(year);
		                	}
		                	else if (i == 5){
		                		if (type == 0) {
		                			ti.setAmountExport(new BigDecimal(cell.getNumericCellValue()));
		                		}
		                		else if (type == 1) {
		                			ti.setAmountImport(new BigDecimal(cell.getNumericCellValue()));	                			
		                		}
		                	}
		                    break;
		                default:
		                }
						if (i == 5) {
							if (states == null || states.contains(partner.getName())) {
								ret.getPartners().add(partner);								
							}
							break;
						}
					}
				}
			}
        }
		if (ret != null) {
			logger.debug("Has found a number of " + ret.getPartners().size() + " for year " + year);
		}
		else {
			logger.error("Nothing found in the file");
		}
		return ret;
	}
	
	protected static List<String> getEUStates() throws Exception {
		List<String> ret =  new ArrayList<String> ();
		try (InputStream is = DirParser.class.getResourceAsStream("/states.csv");
			InputStreamReader r = new InputStreamReader(is);
			LineNumberReader lnr = new LineNumberReader(r)) {
			String s = null;
			while((s = lnr.readLine())!= null) {
				String[] ar = s.split(",");
				if (ar[1].equals("EU"))
					ret.add(ar[0].trim());
			}
		}
		return ret;
	}

	protected static List<String> getNEStates() throws Exception {
		List<String> ret =  new ArrayList<String> ();
		try (InputStream is = DirParser.class.getResourceAsStream("/states.csv");
			InputStreamReader r = new InputStreamReader(is);
			LineNumberReader lnr = new LineNumberReader(r)) {
			String s = null;
			while((s = lnr.readLine())!= null) {
				String[] ar = s.split(",");
				if (ar[1].equals("NE"))
					ret.add(ar[0].trim());
			}
		}
		return ret;
	}

	protected static Map<String,String> getTranslations() throws Exception {
		Map<String,String>  ret =  new HashMap<String,String> ();
		try (InputStream is = DirParser.class.getResourceAsStream("/states_translation.csv");
			InputStreamReader r = new InputStreamReader(is);
			LineNumberReader lnr = new LineNumberReader(r)) {
			String s = null;
			while((s = lnr.readLine())!= null) {
				String[] toks = s.split(",");
				ret.put(toks[0].trim(), toks[1].trim());
			}
		}
		return ret;
	}
}
