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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maiereni.host.bo.Replacement;
import com.maiereni.host.bo.Row;
import com.maiereni.host.bo.Translation;

/**
 * @author Petre Maierean
 *
 */
public class PageService {
	private static final Logger logger = LoggerFactory.getLogger(PageService.class);
	private List<Row> original;
	private List<String> init;
	
	PageService(final List<Row> original, final List<String> init) {
		this.original = original;
		this.init = init;
	}
	
	public Translation getTranslation(final List<String> rp) {
		List<String> rep = rp;
		if (rp == null) {
			rep = new ArrayList<String>();
			for(int i=0; i< 24; i++) {
				if (init != null && init.size() > i)
					rep.add(init.get(i));
				else
					rep.add(new String());
			}
		}		Translation ret = new Translation();
		ret.setOriginal(original);
		Map<String, Integer> counts = new HashMap<String, Integer>();
		List<Row> translated = new ArrayList<Row>();
		for(Row r: original) {
			Row tr = new Row();
			List<String> arr = new ArrayList<String>();
			for(String s : r.getCols()) {
				String r1 = getReplacement(rep, s);
				arr.add(r1);
				Integer count = counts.remove(s);
				if (count == null) {
					count = new Integer(1);
				}
				else {
					count = new Integer(count.intValue() + 1);
				}
				counts.put(s, count);
			}
			tr.setCols(arr);
			translated.add(tr);
		}
		ret.setTranslated(translated);

		List<Replacement> replacements = new ArrayList<Replacement>();
		for(int i=0; i<24; i++) {
			Replacement replacement = new Replacement();
			if (rep.size() > i)
				replacement.setValue(rep.get(i));
			else
				replacement.setValue(rep.get(0));
			Integer count = counts.get(Integer.toString(i + 1));
			if (count != null)
				replacement.setFrequency(count);
			replacements.add(replacement);
		}
		ret.setReplacements(replacements);
		return ret;
	}
	
	
	
	private String getReplacement(final List<String> replacements, final String ix) {
		if (replacements != null && StringUtils.isNotBlank(ix)) {
			try {
				int i = Integer.parseInt(ix);
				if (replacements.size() > i - 1) {
					return replacements.get(i-1);
				}
			}
			catch(Exception e) {
				logger.error("Failed to parse " + ix, e);
			}
		}
		return "";
	}
}
