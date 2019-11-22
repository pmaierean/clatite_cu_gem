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
package com.maiereni.host.bo;

import java.io.Serializable;
import java.util.List;

/**
 * @author Petre Maierean
 *
 */
public class Translation implements Serializable {
	private static final long serialVersionUID = 8506842550800033151L;
	private List<Row> original, translated;
	private List<Replacement> replacements;
	
	public List<Row> getOriginal() {
		return original;
	}
	public void setOriginal(List<Row> original) {
		this.original = original;
	}
	public List<Row> getTranslated() {
		return translated;
	}
	public void setTranslated(List<Row> translated) {
		this.translated = translated;
	}
	public List<Replacement> getReplacements() {
		return replacements;
	}
	public void setReplacements(List<Replacement> replacements) {
		this.replacements = replacements;
	}
	
}
