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
package com.maiereni.utils.lottoMax.bo;

import java.io.Serializable;
import java.util.List;

/**
 * @author Petre Maierean
 *
 */
public class NumberRecords implements Serializable {
	private static final long serialVersionUID = 2644849260250098332L;
	private int maxAge, minAge, maxFrequency, minFrequency;
	private List<NumberRecord> numberRecords;
	public int getMaxAge() {
		return maxAge;
	}
	public void setMaxAge(int maxAge) {
		this.maxAge = maxAge;
	}
	public int getMinAge() {
		return minAge;
	}
	public void setMinAge(int minAge) {
		this.minAge = minAge;
	}
	public int getMaxFrequency() {
		return maxFrequency;
	}
	public void setMaxFrequency(int maxFrequency) {
		this.maxFrequency = maxFrequency;
	}
	public int getMinFrequency() {
		return minFrequency;
	}
	public void setMinFrequency(int minFrequency) {
		this.minFrequency = minFrequency;
	}
	public List<NumberRecord> getNumberRecords() {
		return numberRecords;
	}
	public void setNumberRecords(List<NumberRecord> numberRecords) {
		this.numberRecords = numberRecords;
	}
}
