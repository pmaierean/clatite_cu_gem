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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maiereni.utils.lottoMax.bo.NumberRecord;
import com.maiereni.utils.lottoMax.bo.NumberRecords;
import com.maiereni.utils.lottoMax.bo.WeighedNumber;

/**
 * Encapsulated the functionality of weighing lottery numbers
 * @author Petre Maierean
 *
 */
public class WeighingProcessor {
	private static final Logger logger = LoggerFactory.getLogger(WeighingProcessor.class);
	private BigDecimal ageWeigh, frequencyWeigh;
	private WeighedNumberComparator comparator;
	
	public WeighingProcessor() {
		this.ageWeigh = new BigDecimal("100");
		this.frequencyWeigh = new BigDecimal("200");
		this.comparator = new WeighedNumberComparator();
	}
	/**
	 * Processes the argument
	 * @param numberRecords
	 * @return a list of weighed numbers
	 * @throws Exception
	 */
	public List<WeighedNumber> process(@Nonnull final NumberRecords numberRecords) throws Exception {
		logger.debug("Weigh numbers");
		List<WeighedNumber> ret = new ArrayList<WeighedNumber>();
		if (numberRecords.getNumberRecords() != null) {
			for(NumberRecord numberRecord: numberRecords.getNumberRecords()) {
				WeighedNumber weighedNumber = getBaseline(numberRecord);
				BigDecimal ageWeigh = calculateAgeWeigh(numberRecord, numberRecords.getMaxAge(), numberRecords.getMinAge());
				BigDecimal frequencyWeigh = calculateFequencyWeigh(numberRecord,numberRecords.getMaxFrequency(), numberRecords.getMinFrequency());
				weighedNumber.setWeight1(ageWeigh.add(frequencyWeigh));
				ret.add(weighedNumber);
			}
			ret.sort(comparator);
		}
		return ret;
	}

	private WeighedNumber getBaseline(final NumberRecord r) {
		WeighedNumber weighedNumber = new WeighedNumber();
		weighedNumber.setDaysAgo(r.getDaysAgo());
		weighedNumber.setDrawnAgo(r.getDrawnAgo());
		weighedNumber.setFrequency(r.getFrequency());
		weighedNumber.setLastDrawnDate(r.getLastDrawnDate());
		weighedNumber.setNumber(r.getNumber());
		return weighedNumber;
	}
	
	private BigDecimal calculateAgeWeigh(final NumberRecord numberRecord, int maxAge, int minAge) {
		double d1 = (maxAge - minAge);
		double d2 = (numberRecord.getDaysAgo() - minAge);
		double d =  d2 / d1;
		BigDecimal bd = new BigDecimal(d);
		return bd.multiply(ageWeigh);
	}

	private BigDecimal calculateFequencyWeigh(final NumberRecord numberRecord, int maxFequency, int minFequency) {
		double d1 = (maxFequency - minFequency);
		double d2 = (numberRecord.getFrequency() - minFequency);
		double d = d2 / d1;
		BigDecimal bd = new BigDecimal(d);
		return bd.multiply(frequencyWeigh);
	}

	
	public BigDecimal getAgeWeigh() {
		return ageWeigh;
	}

	public void setAgeWeigh(BigDecimal ageWeigh) {
		this.ageWeigh = ageWeigh;
	}

	public BigDecimal getFrequencyWeigh() {
		return frequencyWeigh;
	}

	public void setFrequencyWeigh(BigDecimal frequencyWeigh) {
		this.frequencyWeigh = frequencyWeigh;
	}

	class WeighedNumberComparator implements Comparator<WeighedNumber> {

		@Override
		public int compare(WeighedNumber o1, WeighedNumber o2) {
			return o1.getWeight1().compareTo(o2.getWeight1());
		}
		
	}
}
