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
package com.maiereni.host.web.jaxrs.service.impl;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.jcr.Binary;
import javax.jcr.Node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maiereni.host.web.util.ExceptionHolder;

/**
 * @author Petre Maierean
 *
 */
class NodePropertiesHelper {
	private static final Logger logger = LoggerFactory.getLogger(NodePropertiesHelper.class);
	/**
	 * Add properties to a node
	 * @param node
	 * @param properties
	 * @throws Exception
	 */
	public void addNodeProperties(@Nonnull final Node node, @Nonnull final Map<String, Object> properties)
		throws Exception {
		final ExceptionHolder holder = new ExceptionHolder();
		properties.keySet().forEach((s)->{
			try {
				Object val = properties.get(s);
				if (val instanceof String) 
					node.setProperty(s, (String)val);
				else if (val instanceof BigDecimal)
					node.setProperty(s, (BigDecimal)val);
				else if (val instanceof Long)
					node.setProperty(s, (Long)val);
				else if (val instanceof Double)
					node.setProperty(s, (Double)val);
				else if (val instanceof Calendar)
					node.setProperty(s, (Calendar)val);
				else if (val instanceof Boolean)
					node.setProperty(s, (Boolean)val);
				else if (val instanceof Binary)
					node.setProperty(s, (Binary)val);
				else if (val instanceof String[])
					node.setProperty(s, (String[])val);
			}
			catch(Exception e) {
				logger.error("Could not set property " + s);
				holder.setException(e);
			}
		});
		if (holder.isException())
			throw holder.getException();
	}
}
