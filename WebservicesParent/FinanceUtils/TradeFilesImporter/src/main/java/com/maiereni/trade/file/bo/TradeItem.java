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
public class TradeItem implements Serializable {
	private static final long serialVersionUID = -5860423314749675928L;
	private BigDecimal amountImport, amountExport, balance;
	private int year;
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public BigDecimal getAmountImport() {
		return amountImport;
	}
	public void setAmountImport(BigDecimal amountImport) {
		this.amountImport = amountImport;
	}
	public BigDecimal getAmountExport() {
		return amountExport;
	}
	public void setAmountExport(BigDecimal amountExport) {
		this.amountExport = amountExport;
	}
	public BigDecimal getBalance() {
		return balance;
	}
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
}
