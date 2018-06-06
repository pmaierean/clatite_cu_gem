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

import java.math.BigDecimal;

/**
 * @author Petre Maierean
 *
 */
public class WeighedNumber extends NumberRecord {
	private static final long serialVersionUID = 6198590764086932334L;
	private BigDecimal weight1, weight2;
	public BigDecimal getWeight1() {
		return weight1;
	}
	public void setWeight1(BigDecimal weight1) {
		this.weight1 = weight1;
	}
	public BigDecimal getWeight2() {
		return weight2;
	}
	public void setWeight2(BigDecimal weight2) {
		this.weight2 = weight2;
	}
}
