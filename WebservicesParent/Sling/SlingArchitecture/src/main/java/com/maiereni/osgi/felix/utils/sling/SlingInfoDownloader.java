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

import java.util.List;

import javax.servlet.http.Cookie;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maiereni.osgi.felix.utils.SlingDescriber;
import com.maiereni.osgi.felix.utils.bo.Bundle;
import com.maiereni.osgi.felix.utils.bo.Component;
import com.maiereni.osgi.felix.utils.bo.Service;
import com.maiereni.osgi.felix.utils.bo.SlingInfo;
import com.maiereni.utils.http.bo.ResponseBean;

/**
 * A utility class that downloads information from Sling
 * @author Petre Maierean
 *
 */
public class SlingInfoDownloader {
	private static final Logger logger = LoggerFactory.getLogger(SlingDescriber.class);
	private ObjectMapper mapper = new ObjectMapper();
	
	public SlingInfo getSlingInfo(final String host, final String user, final String password) throws Exception {
		SlingInfo ret = new SlingInfo();
		logger.debug("Downloading information from Sling");
		List<Cookie> auth = login(host, user, password);
		ret.setBundles(getBundles(host, auth));
		ret.setComponent(getComponents(host, auth));
		ret.setServices(getServices(host, auth));
		logger.debug("Done downloading information from Sling");
		return ret;
	}


	protected List<Service> getServices(final String host, final List<Cookie> auth) throws Exception {
		ListServices processor = new ListServices(host, mapper);
		return processor.getServices(auth);
	}
	
	protected List<Bundle> getBundles(final String host, final List<Cookie> auth) throws Exception {
		ListBundles processor = new ListBundles(host, mapper);
		return processor.getBundles(auth);
	}
	
	protected List<Component> getComponents(final String host, final List<Cookie> auth) throws Exception {
		ListComponents processor = new ListComponents(host, mapper);
		return processor.getComponents(auth);
	}
	
	protected List<Cookie> login(final String host, final String user, final String password) throws Exception {
		Login login = new Login(host, mapper);
		ResponseBean responseBean = login.login(user, password);
		logger.debug("Successfully logged in");
		return responseBean.getCookies();
	}

}
