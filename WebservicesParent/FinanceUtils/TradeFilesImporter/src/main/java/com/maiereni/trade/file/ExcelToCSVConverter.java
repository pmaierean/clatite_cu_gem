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
import java.io.FileFilter;
import java.io.FileInputStream;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author Petre Maierean
 *
 */
public class ExcelToCSVConverter {
	private static final Logger logger = Logger.getLogger(ExcelToCSVConverter.class);

	public void convertAll(String path) throws Exception {
		File fDir = new File(path);
		if (fDir.isDirectory()) {
			logger.debug("Start parsing files from " + path);
			File[] children = fDir.listFiles(new FileFilter() {
				@Override
				public boolean accept(File f) {
					return f.getName().endsWith("xlsx");
				}
			});
			if (children != null) {
				for(File f: children) {
					convertSelectedSheetInXLXSFileToCSV(f);
				}
			}
		}
	}
	
    private void convertSelectedSheetInXLXSFileToCSV(File xlsxFile) throws Exception {
    	logger.debug("Convert " + xlsxFile.getPath());
        FileInputStream fileInStream = new FileInputStream(xlsxFile);
 
        // Open the xlsx and get the requested sheet from the workbook
        XSSFWorkbook workBook = new XSSFWorkbook(fileInStream);
        XSSFSheet selSheet = workBook.getSheetAt(0);
        if (selSheet.getSheetName().equals("Sheet1")) {
        	selSheet = workBook.getSheetAt(1);
        }

        StringBuffer sb = new StringBuffer();
 
        // Iterate through all the rows in the selected sheet
        Iterator<Row> rowIterator = selSheet.iterator();
        while (rowIterator.hasNext()) {
            int i = 0;
            Row row = rowIterator.next();
 
            // Iterate through all the columns in the row and build ","
            // separated string
            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                if (i > 0) {
                    sb.append(",");
                }
                i++; 
                // If you are using poi 4.0 or over, change it to
                // cell.getCellType
                switch (cell.getCellTypeEnum()) {
                case STRING:
                    sb.append("\"").append(cell.getStringCellValue()).append("\"");
                    break;
                case NUMERIC:
                    sb.append(cell.getNumericCellValue());
                    break;
                case BOOLEAN:
                    sb.append(cell.getBooleanCellValue());
                    break;
                default:
                }
            }
            sb.append("\r\n");
        }
        workBook.close();
        String name = xlsxFile.getPath().replace("xlsx", "csv");
        File dest = new File(name);
        FileUtils.writeStringToFile(dest, sb.toString());
        logger.debug("Saved " + name);
    }
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			new ExcelToCSVConverter().convertAll(args[0]);
		}
		catch(Exception e) {
			logger.error("Failed to convert", e);
		}
	}

}
