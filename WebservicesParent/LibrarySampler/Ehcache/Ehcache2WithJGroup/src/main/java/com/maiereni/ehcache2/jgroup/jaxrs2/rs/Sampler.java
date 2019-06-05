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
package com.maiereni.ehcache2.jgroup.jaxrs2.rs;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.maiereni.ehcache2.jgroup.jaxrs2.bo.AllTextResponseBean;
import com.maiereni.ehcache2.jgroup.jaxrs2.bo.ResponseBean;
import com.maiereni.ehcache2.jgroup.jaxrs2.bo.TextEntry;
import com.maiereni.ehcache2.jgroup.jaxrs2.bo.TextResponseBean;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

/**
 * 
 * @author Petre Maierean
 *
 */
@Path("sampler")
@Produces("application/json")
@Component
public class Sampler {
	private static final Logger logger = LoggerFactory.getLogger(Sampler.class);
	
	@Autowired
	Cache clusteredCache;
	
	public Sampler() {
		logger.debug("Create the sampler class");
	}
	
	@GET
	@Path("ping")
	public String ping() {
		logger.debug("Calling ping");
		return "pong";
	}
	
	@POST
	@Path("addText")
	public ResponseBean addText(@BeanParam final TextEntry request) {
		ResponseBean ret = new ResponseBean();
		try {
			if (StringUtils.isAnyBlank(request.getKey(), request.getText())) {
				throw new Exception("None can be blank");
			}
			if (clusteredCache.isKeyInCache(request.getKey())) {
				clusteredCache.remove(request.getKey());
			}
			Element element = new Element(request.getKey(), request.getText());
			clusteredCache.put(element);
			ret.setCode(0);
			ret.setMessage("All good");
			logger.debug("The element has been added to the cache");
		}
		catch(Exception e) {
			ret.setCode(-1);
			logger.error("Failed to add ", e);
			ret.setMessage(e.getMessage());
		}
		return ret;
	}
	
	@GET
	@Path("getText")
	public TextResponseBean getText(@QueryParam("key") final String key) {
		TextResponseBean ret = new TextResponseBean();
		try {
			if (StringUtils.isBlank(key)) {
				throw new Exception("The key cannot be blank");
			}
			Element element = clusteredCache.get(key);
			if (element == null) {
				ret.setCode(1);
				ret.setMessage("Not found");				
			} 
			else if (element.isExpired()) {
				ret.setCode(2);
				ret.setMessage("Expired");
			}
			else {
				ret.setText(element.getObjectValue().toString());
				ret.setKey(key);
			}
		}
		catch(Exception e) {
			logger.error("Failed to add ", e);
			ret.setMessage(e.getMessage());
			ret.setCode(-1);
		}
		return ret;
	}
	
	@GET
	@Path("getAllTexts")
	public AllTextResponseBean getAllTexts() {
		AllTextResponseBean ret = new AllTextResponseBean();
		try {
			ret.setEntries(new ArrayList<TextEntry>());
			@SuppressWarnings("unchecked")
			List<String> keys = clusteredCache.getKeys();
			for(String key: keys) {
				Element element = clusteredCache.get(key);
				if (!(element == null || element.isExpired())) {
					TextEntry textEntry = new TextEntry();
					textEntry.setKey(key);
					textEntry.setText(element.getObjectValue().toString());
					ret.getEntries().add(textEntry);
				}
			}
		}
		catch(Exception e) {
			logger.error("Failed to list all texts ", e);
			ret.setMessage(e.getMessage());
			ret.setCode(-1);
		}
		return ret;
	}
}
