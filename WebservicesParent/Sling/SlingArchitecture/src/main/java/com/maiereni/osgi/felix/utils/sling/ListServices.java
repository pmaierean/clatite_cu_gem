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
import com.maiereni.osgi.felix.utils.bo.Service;
import com.maiereni.osgi.felix.utils.bo.Services;
import com.maiereni.utils.http.bo.ResponseBean;

/**
 * @author Petre Maierean
 *
 */
public class ListServices extends BaseAdminConsoleClient {
	private static final String GET_SERVICES_TEMPLATE = "http://%s/system/console/services.json";
	private static final String GET_SERVICE_TEMPLATE = "http://%s/system/console/services/%s.json";
	/**
	 * @param host
	 * @param mapper
	 */
	public ListServices(String host, ObjectMapper mapper) {
		super(host, mapper);
	}

	/**
	 * List all services
	 * @param cookies
	 * @return
	 * @throws Exception
	 */
	public List<Service> getServices(@Nonnull final List<Cookie> cookies) throws Exception {
		logger.debug("Find services");
		String url = String.format(GET_SERVICES_TEMPLATE, host);
		ResponseBean rd = get(url, EMPTY_PARAMS, NO_HEADER, cookies);
		try {
			Services services = mapper.readValue(rd.getBody(), Services.class);
			List<Service> ret = new ArrayList<Service>();
			for (Service s: services.getData()) {
				Service r = getService(cookies, s.getId());
				ret.add(r);
			}
			return ret;
		}
		catch(Exception e) {
			logger.error("Failed to convert\r\n{}", rd.getBody());
			throw e;
		}
	}
	/**
	 * Get a service
	 * @param cookies
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Service getService(@Nonnull final List<Cookie> cookies, @Nonnull final String id) throws Exception {
		String url = String.format(GET_SERVICE_TEMPLATE, host, id);
		ResponseBean rd = get(url, EMPTY_PARAMS, NO_HEADER, cookies);
		try {
			Services sr = mapper.readValue(rd.getBody(), Services.class);
			return sr.getData().get(0);
		}
		catch(Exception e) {
			logger.error("Failed to parse:\r\n{}", rd.getBody());
			throw e;
		}
	}
}
