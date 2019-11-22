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
package com.maiereni.trade.file.bo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Petre Maierean
 *
 */
public class UpdateItem implements Serializable {
	private static final long serialVersionUID = 6273835672665344198L;
	private String stateName;
	private BigDecimal ex2018, im2018;
	public String getStateName() {
		return stateName;
	}
	public void setStateName(String stateName) {
		this.stateName = stateName;
	}
	public BigDecimal getEx2018() {
		return ex2018;
	}
	public void setEx2018(BigDecimal ex2018) {
		this.ex2018 = ex2018;
	}
	public BigDecimal getIm2018() {
		return im2018;
	}
	public void setIm2018(BigDecimal im2018) {
		this.im2018 = im2018;
	}
	
}
