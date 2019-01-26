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
package com.maiereni.osgi.felix.utils.sling;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maiereni.osgi.felix.utils.bo.Component;
import com.maiereni.osgi.felix.utils.bo.Components;
import com.maiereni.utils.http.bo.ResponseBean;

/**
 * @author Petre Maierean
 *
 */
public class ListComponents extends BaseAdminConsoleClient {
	public static final String COMPONENTS_LIST_TEMPLATE = "http://%s/system/console/components.json";
	public static final String COMPONENT_LIST_TEMPLATE = "http://%s/system/console/components/%s.json";
	public ListComponents(String host, ObjectMapper mapper) {
		super(host, mapper);
	}

	public List<Component> getComponents(final List<Cookie> auth) throws Exception {
		logger.debug("Find components");
		String url = String.format(COMPONENTS_LIST_TEMPLATE, host);
		ResponseBean resp = get(url, EMPTY_PARAMS, NO_HEADER, auth);
		String s = resp.getBody();
		Components cs = mapper.readValue(s, Components.class);
		List<Component> ret = new ArrayList<Component>();
		for(int i= 0; i< cs.getData().size(); i++) {
			Component actual = getComponent(auth, "" + i);
			if (actual == null) {
				actual = cs.getData().get(i);
			}
			ret.add(actual);
		}
		return ret;
	}
	
	public Component getComponent(final List<Cookie> auth, final String id) throws Exception {
		try {
			String url = String.format(COMPONENT_LIST_TEMPLATE, host, id);
			ResponseBean resp = get(url, EMPTY_PARAMS, NO_HEADER, auth);
			String s = resp.getBody();
			Components cs = mapper.readValue(s, Components.class);
			return cs.getData().size() > 0 ? cs.getData().get(0) : null;
		}
		catch(Exception e) {
		}
		return null;
	}
}
