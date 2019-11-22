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
import java.io.FileReader;
import java.io.LineNumberReader;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.maiereni.trade.file.bo.Partner;
import com.maiereni.trade.file.bo.TradeItem;

/**
 * A program file that gets the Account balances from all EU countries up to date
 * 
 * @author Petre Maierean
 *
 */
public class EUAccountBalannce {
	private static final Logger logger = Logger.getLogger(EUAccountBalannce.class);
	private DecimalFormat df;
	private int minYear = 1988;
	private int maxYear = 2019;
	
	public EUAccountBalannce() {
		df = new DecimalFormat();
		df.setMaximumFractionDigits(3);
		df.setMinimumFractionDigits(0);
		df.setGroupingUsed(false);
	}

	public void writeEUBalance(final String template, final String dest) throws Exception {
		List<Partner> partners = readPartners(template);
		StringBuffer sb = new StringBuffer();
		sb.append("Year");
		for(int year = minYear; year<=maxYear; year++) {
			if (year > minYear && year < maxYear ) {
				sb.append(year);
			}
			else if (year == 2019) {
				
			}
			for(Partner p: partners) {
				if (year == minYear) {
					sb.append(",").append(p.getName());
				}
				else if (year == maxYear) {
					sb.append(",").append(df.format(p.getBalance()));
				}
				else if (year > minYear && year < maxYear){
					TradeItem ti = getTradeItem(p, year);
					if (ti == null) {
						sb.append(",0");
					}
					else {
						sb.append(",").append(df.format(ti.getBalance()));						
					}
				}
			}
			sb.append("\r\n");
		}
		FileUtils.writeStringToFile(new File(dest), sb.toString());
		logger.debug("Done writing the balance");
	}
	
	private TradeItem getTradeItem(final Partner p, final int year) {
		TradeItem ret = null;
		for(TradeItem r: p.getTradeItems()) {
			if (r.getYear() == year) {
				ret = r;
				break;
			}
		}
		return ret;
	}

	private List<Partner> readPartners(final String template) throws Exception {
		logger.debug("Read all partner states in the EU");
		List<String> states = FileParser.getEUStates();//getNEStates()
		List<Partner> partners = new ArrayList<Partner>();
		for(String state : states) {
			String path = String.format(template, "Romania_" + state);
			File f = new File(path);
			if (f.exists()) {
		        Partner p = readPartnerData(f);
		        p.setName(state);
		        partners.add(p);
			}
		}
		partners.sort(new Comparator<Partner>() {
			@Override
			public int compare(Partner o1, Partner o2) {
				return o1.getBalance().compareTo(o2.getBalance());
			}
		});
		logger.debug("Partners have been listed");
		return partners;
	}
	
	private Partner readPartnerData(final File f) throws Exception {
		Partner ret = new Partner();
		ret.setTradeItems(new ArrayList<TradeItem>());
		BigDecimal bd = new BigDecimal(0);
		try(FileReader fr = new FileReader(f);
			LineNumberReader lnr = new LineNumberReader(fr)) {
			int i= 0;
			for(String s=null; (s = lnr.readLine()) != null; ) {
				if (i>0) {
					String[] sp = s.split(",");
					TradeItem ti = new TradeItem();
					int year = Integer.parseInt(sp[0]);
					if (year > minYear && year < maxYear ) {
						ti.setYear(year);
						BigDecimal ex  = convertDiv1000(sp[1]);
						ti.setAmountExport(ex);
						BigDecimal in = convertDiv1000(sp[2]);
						ti.setAmountImport(in);
						ti.setBalance(ex.subtract(in));
						ret.getTradeItems().add(ti);
						bd = bd.add(ti.getBalance());
					}
				}
				i++;
			}
		}
		ret.setBalance(bd);
		return ret;
	}
	
	private BigDecimal convertDiv1000(String s) throws Exception {
		double d = Double.parseDouble(s);
		return new BigDecimal(d/1000);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			EUAccountBalannce parser = new EUAccountBalannce();
			if (args.length == 4) {
				parser.minYear = Integer.parseInt(args[2]);
				parser.maxYear = Integer.parseInt(args[3]);
			}
			parser.writeEUBalance(args[0], args[1]);
		}
		catch(Exception e) {
			logger.error("Exception ", e);
		}
	}

}
