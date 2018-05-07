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
package com.maiereni.npm.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Petre Maierean
 *
 */
@Controller
public class Index {
	private static final Logger logger = LoggerFactory.getLogger(Index.class);
	
	public Index() {
		logger.debug("Construct controller");
	}
	
	@RequestMapping(path = "/index", method = RequestMethod.GET)
	public String getIndex() {
		logger.debug("Get the main application");
		return "home";
	}
	
	@RequestMapping(path = "/npm", method = RequestMethod.GET)
	public String getNPM() {
	
		return "resolveNPM";
	}
}
