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
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.maiereni.trade.file.bo.Countries;
import com.maiereni.trade.file.bo.Country;
import com.maiereni.trade.file.bo.Partner;
import com.maiereni.trade.file.bo.TradeItem;

/**
 * @author Petre Maierean
 *
 */
public class DirParser extends FileParser {
	private static final Logger logger = Logger.getLogger(DirParser.class);
	private static final BigDecimal ONE = new BigDecimal(1);
	
	private Marshaller marshaller;
	private DecimalFormat df;

	public DirParser(List<String> states) throws Exception {
		super(states);
		JAXBContext jc = JAXBContext.newInstance(Countries.class);
		marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		df = new DecimalFormat();
		df.setMaximumFractionDigits(3);
		df.setMinimumFractionDigits(0);
		df.setGroupingUsed(false);
	}
	
	/**
	 * Parse all files in the folder
	 * @param path
	 * @throws Exception
	 */
	public Countries parseAll(final String path) throws Exception {
		Countries ret = new Countries();
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
					Country c = parse(f);
					if (c != null) {
						if (ret.getCountries() == null) {
							ret.setCountries(new ArrayList<Country>());
							ret.getCountries().add(c);
						}
						else {
							for(Country cs : ret.getCountries()) {
								if (cs.getName().equals(c.getName())) {
									merge(cs, c);
									break;
								}
							}
						}
					}
				}
			}
			else {
				logger.error("The path " + path + " does not contain any files to parse");				
			}
		}
		else {
			logger.error("The path " + path + " does not point to a directory");
		}
		return ret;
	}
	
	private void merge(final Country country, final Country c) {
		if (c.getPartners() != null) {
			if (country.getPartners() == null) {
				country.setPartners(c.getPartners());
			}
			else {
				for(Partner p: c.getPartners()) {
					boolean notFound = true;
					for(Partner p1: country.getPartners()) {
						if (p1.getName().equals(p.getName())) {
							merge(p1, p);
							notFound = false;
							break;
						}
					}
					if (notFound) {
						country.getPartners().add(p);
					}
				}
			}
		}
	}
	
	private void merge(final Partner partner, final Partner p) {
		if (p.getTradeItems() != null) {
			if (partner.getTradeItems() == null) {
				partner.setTradeItems(p.getTradeItems());
			}
			else {
				for(TradeItem t: p.getTradeItems()) {
					boolean notFound = true;
					for(TradeItem ti: partner.getTradeItems()) {
						if (ti.getYear() == t.getYear()) {
							if (ti.getAmountExport() == null && t.getAmountExport() != null) {
								ti.setAmountExport(t.getAmountExport());
							}
							else if (ti.getAmountImport() == null && t.getAmountImport() != null) {
								ti.setAmountImport(t.getAmountImport());
							}
							notFound = false;
							break;
						}
					}
					if (notFound) {
						partner.getTradeItems().add(t);
					}
				}
			}
		}
	}
	
	public void consolidate(final Countries countries) {
		for(Country c: countries.getCountries()) {
			for(Partner p: c.getPartners()) {
				BigDecimal balance = new BigDecimal(0);
				for(TradeItem ti : p.getTradeItems()) {
					BigDecimal ex = ti.getAmountExport();
					if (ex == null)
						ex = new BigDecimal(0);
					else
						ex = new BigDecimal(ex.doubleValue()/1000);
					BigDecimal in = ti.getAmountImport();
					if (in == null)
						in = new BigDecimal(0);
					else
						in = new BigDecimal(in.doubleValue()/1000);
					BigDecimal bd = ex.subtract(in);
					ti.setBalance(bd);
					balance = balance.add(bd);
				}
				p.setBalance(balance);
			}
		}		
	}
	
	public void saveResult(final Countries countries, final String outputXML) throws Exception {
		File f = new File(outputXML);
		try (FileOutputStream is = new FileOutputStream(f)) {
			marshaller.marshal(countries, is);
		}
		logger.debug("The content has been saved to " + outputXML);
	}
	
	public void saveResultCSV(final Countries countries, final String state, final String fileTemplate) throws Exception {
		List<Country> ctrs = countries.getCountries();
		for(Country ctr: ctrs) {
			List<Partner> partners = ctr.getPartners();
			for(Partner p: partners) {	
				if (p.getName().equals(state)) {
					String path = String.format(fileTemplate, ctr.getName() + "_" + state);
					StringBuffer sb = new StringBuffer();
					sb.append("year,export,import,balance,percent");
					for(TradeItem ti : p.getTradeItems()) {
						BigDecimal ex = ti.getAmountExport();
						if (ex == null) {
							ex = new BigDecimal(0);
						}
						BigDecimal in = ti.getAmountImport();
						BigDecimal bd = ti.getBalance();
						BigDecimal proc = null;
						if (in == null) {
							proc = ONE;
						}
						else {
							double d = (ex.doubleValue() / in.doubleValue()) - 1;
							proc = new BigDecimal(d);
						}
						sb.append("\r\n");
						sb.append(ti.getYear()).append(",").append(df.format(ex)).append(",");
						if (in == null)
							sb.append("0");
						else
							sb.append(df.format(in));
						sb.append(",").append(df.format(bd)).append(",");
						sb.append(df.format(proc));
					}
					FileUtils.writeStringToFile(new File(path), sb.toString());
					logger.debug("Written file to " + path);
					break;
				}
				
			}
			
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			List<String> states = getEUStates();//getNEStates();
			DirParser parser = null;
			boolean euCSV = false;
			if (args.length > 1) {
				String dest = args[1];
				if (dest.indexOf("EU")>=0) {
					parser = new DirParser(states);
				}
				else if (dest.endsWith("%s.csv")) {
					parser = new DirParser(states);
					euCSV = true;
				}
			}
			
			if (parser == null) {
				parser = new DirParser(null);
			}
			Countries countries = parser.parseAll(args[0]);
			parser.consolidate(countries);
			if (args.length > 1) {
				if (euCSV) {
					for(String state : states) {
						parser.saveResultCSV(countries, state, args[1]);
					}
				}
				else {
					parser.saveResult(countries, args[1]);
				}
			}
		}
		catch(Exception e) {
			logger.error("Failed to process", e);
		}
	}
	


}
