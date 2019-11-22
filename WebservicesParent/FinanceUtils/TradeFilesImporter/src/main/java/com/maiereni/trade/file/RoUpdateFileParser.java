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
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.maiereni.trade.file.bo.UpdateItem;

/**
 * @author Petre Maierean
 *
 */
public class RoUpdateFileParser extends FileParser {
	private static final Logger logger = Logger.getLogger(RoUpdateFileParser.class);
	private Map<String, String> translation;
	private BigDecimal euro2dollar;
	private DecimalFormat df;
	
	public RoUpdateFileParser(final BigDecimal euro2dollar) throws Exception {
		super(null);
		this.states =  getNEStates();//getEUStates();
		this.translation = getTranslations();
		this.euro2dollar = euro2dollar;
		df = new DecimalFormat();
		df.setMaximumFractionDigits(3);
		df.setMinimumFractionDigits(0);
		df.setGroupingUsed(false);
	}

	public void updateLatestData(final String inCSVPath, final String fileTemplate) throws Exception {
		Map<String, UpdateItem> updateItems = parse(inCSVPath);
		for(String state : states) {
			String roState = translation.get(state);
			if (StringUtils.isNotBlank(roState)) {
				UpdateItem update = updateItems.get(roState);
				if (update != null) {
					String path = String.format(fileTemplate, "Romania_" + state);
					File f = new File(path);
					if (f.exists()) {
						ContentWithFlag cf = getContent(f);
						if (!cf.hasRecord2018) {
							BigDecimal ex = update.getEx2018().multiply(euro2dollar);
							BigDecimal in = update.getIm2018().multiply(euro2dollar);
							BigDecimal dif = ex.subtract(in);
							double d = (ex.doubleValue() / in.doubleValue()) - 1;
							StringBuffer sb = new StringBuffer();
							sb.append(cf.content);
							sb.append("2018,").append(df.format(ex)).append(",").append(df.format(in)).append(",");
							sb.append(df.format(dif)).append(",").append(df.format(d)).append("\r\n");
							logger.debug("File has been updated at " + path);
							FileUtils.writeStringToFile(f, sb.toString());
						}
					}
				}
			}
		}
	}
	
	protected ContentWithFlag getContent(final File f) throws Exception {
		ContentWithFlag ret = new ContentWithFlag();
		try (FileReader r = new FileReader(f);
			LineNumberReader lnr = new LineNumberReader(r)) {
			StringBuffer sb = new StringBuffer();
			String s = null;
			while((s = lnr.readLine())!= null) {
				sb.append(s).append("\r\n");
				if (s.startsWith("2018,")) {
					ret.hasRecord2018 = true;
				}
			}
			ret.content = sb.toString();
		}

		return ret;
	}
	
	protected Map<String, UpdateItem> parse(final String inCSVPath) throws Exception {
		Map<String, UpdateItem> ret = new HashMap<String, UpdateItem>();
		try (FileReader r = new FileReader(inCSVPath);
				LineNumberReader lnr = new LineNumberReader(r)) {
				String s = null;
				for(int i=0;(s = lnr.readLine())!= null;i++) {
					if (StringUtils.isNotBlank(s.trim())) {
						try {
							String[] toks = s.split(",");
							UpdateItem updateItem = new UpdateItem();
							updateItem.setStateName(toks[0]);
							updateItem.setEx2018(new BigDecimal(toks[1]));
							updateItem.setIm2018(new BigDecimal(toks[2]));
							ret.put(toks[0], updateItem);
						}
						catch(Exception e) {
							logger.error("At line " + i + "  => " + s, e);
						}
					}
				}
			}
		
		return ret;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			RoUpdateFileParser parser = new RoUpdateFileParser(new BigDecimal("1103.5"));
			parser.updateLatestData(args[0], args[1]);
		}
		catch(Exception e) {
			logger.error("Failed to process", e);
		}
	}

	private class ContentWithFlag {
		protected String content;
		protected boolean hasRecord2018;
	}
}
