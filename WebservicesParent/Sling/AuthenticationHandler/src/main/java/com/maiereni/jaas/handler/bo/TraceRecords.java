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
package com.maiereni.jaas.handler.bo;

import java.io.Serializable;
import java.util.List;

/**
 * @author Petre Maierean
 *
 */
public class TraceRecords implements Serializable {
	private static final long serialVersionUID = 6005729854346330998L;
	private String line;
	private List<TraceRecord> records;
	public String getLine() {
		return line;
	}
	public void setLine(String line) {
		this.line = line;
	}
	public List<TraceRecord> getRecords() {
		return records;
	}
	public void setRecords(List<TraceRecord> records) {
		this.records = records;
	}
	public boolean equals(Object o) {
		boolean ret = false;
		if (o instanceof TraceRecords) {
			TraceRecords traceRecords = (TraceRecords)o;
			if (this.line.equals(traceRecords.line)) {
				ret = true;
			}
			else if (traceRecords.records.size() == records.size()){
				ret = true;
				for(int i=0; i<records.size(); i++) {
					TraceRecord r1 = traceRecords.records.get(i);
					TraceRecord r2 = records.get(i);
					if (!r1.equals(r2)) {
						ret = false;
						break;
					}
				}
			}
		}
		return ret;
	}
}
