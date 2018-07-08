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
package com.maiereni.host.web.jaxrs.service.bo;

import java.io.Serializable;
import java.util.Locale;

/**
 * A base request bean for services
 * @author Petre Maierean
 *
 */
public abstract class BaseRequest implements Serializable {
	private static final long serialVersionUID = -609470033530965687L;
	private String repoUser;
	private Locale locale;
	
	public Locale getLocale() {
		return locale;
	}
	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	public String getRepoUser() {
		return repoUser;
	}
	public void setRepoUser(String repoUser) {
		this.repoUser = repoUser;
	}
}
