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
package com.maiereni.osgi.felix.utils.bo;

import java.io.Serializable;
import java.util.List;

/**
 * @author Petre Maierean
 *
 */
public class Bundles implements Serializable {
	private static final long serialVersionUID = 3998406790694433209L;
	private String status;
	private int[] s;
	private List<Bundle> data;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int[] getS() {
		return s;
	}
	public void setS(int[] s) {
		this.s = s;
	}
	public List<Bundle> getData() {
		return data;
	}
	public void setData(List<Bundle> data) {
		this.data = data;
	}
}
