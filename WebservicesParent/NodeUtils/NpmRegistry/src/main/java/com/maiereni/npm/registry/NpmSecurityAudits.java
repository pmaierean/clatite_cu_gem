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

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Petre Maierean
 *
 */
@Controller
public class NpmSecurityAudits {
	private static final Logger logger = LoggerFactory.getLogger(NpmSecurityAudits.class);
	@Autowired
	private HttpServletRequest context;
	
	@RequestMapping(path = "/-/npm/v1/security/audits", method = RequestMethod.GET)
	public String ping(Model model) {
		logger.debug("Ping the security stuff");
		return "home";
	}

	@RequestMapping(path = "/-/npm/v1/security/audits/quick", method = RequestMethod.POST)
	public String getSecurity(Model model) {
		logger.debug("Make the audits quick. The context");
		return "npm/auditQuick";
	}
}
