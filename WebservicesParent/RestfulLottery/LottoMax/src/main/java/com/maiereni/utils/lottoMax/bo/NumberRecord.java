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
import java.util.Date;

/**
 * @author Petre Maierean
 *
 */
public class NumberRecord implements Serializable {
	private static final long serialVersionUID = 5748869478403449711L;
	private int number,frequency,drawnAgo,daysAgo;
	private Date lastDrawnDate;
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public int getFrequency() {
		return frequency;
	}
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	public int getDrawnAgo() {
		return drawnAgo;
	}
	public void setDrawnAgo(int drawnAgo) {
		this.drawnAgo = drawnAgo;
	}
	public int getDaysAgo() {
		return daysAgo;
	}
	public void setDaysAgo(int daysAgo) {
		this.daysAgo = daysAgo;
	}
	public Date getLastDrawnDate() {
		return lastDrawnDate;
	}
	public void setLastDrawnDate(Date lastDrawnDate) {
		this.lastDrawnDate = lastDrawnDate;
	}
}
