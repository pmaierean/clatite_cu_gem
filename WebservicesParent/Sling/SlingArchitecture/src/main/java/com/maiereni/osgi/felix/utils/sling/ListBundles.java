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

import javax.annotation.Nonnull;
import javax.servlet.http.Cookie;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maiereni.osgi.felix.utils.bo.Bundle;
import com.maiereni.osgi.felix.utils.bo.Bundles;
import com.maiereni.utils.http.bo.ResponseBean;

/**
 * @author Petre Maierean
 *
 */
public class ListBundles extends BaseAdminConsoleClient {
	private static final String BUNDLES_LIST_TEMPLATE = "http://%s/system/console/bundles.json";
	private static final String BUNDLE_TEMPLATE = "http://%s/system/console/bundles/%s.json";
	
	public ListBundles(@Nonnull final String host, @Nonnull final ObjectMapper mapper) {
		super(host, mapper);
	}
	/**
	 * Lists all the bundles from the console
	 * @param auth
	 * @return
	 * @throws Exception
	 */
	public List<Bundle> getBundles(@Nonnull final List<Cookie> auth) throws Exception {
		String url = String.format(BUNDLES_LIST_TEMPLATE, host);
		logger.debug("Find bundles");
		ResponseBean resp = get(url, EMPTY_PARAMS, NO_HEADER, auth);
		String s = resp.getBody();
		Bundles b = mapper.readValue(s, Bundles.class);
		List<Bundle> ret = new ArrayList<Bundle>();
		for(Bundle bdl: b.getData()) {
			Bundle actual = getBundle(auth, bdl.getId());
			ret.add(actual);
		}
		return ret;
	}
	
	/**
	 * Get information of a bundle
	 * @param auth
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Bundle getBundle(@Nonnull final List<Cookie> auth, String id) throws Exception {
		String url = String.format(BUNDLE_TEMPLATE, host, id);
		ResponseBean resp = get(url, EMPTY_PARAMS, NO_HEADER, auth);
		String s = resp.getBody();
		Bundles b = mapper.readValue(s, Bundles.class);
		return b.getData().size() > 0 ? b.getData().get(0): null;
	}

}
