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
package com.maiereni.host.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.maiereni.host.bo.Translation;
import com.maiereni.host.sevice.PageService;

/**
 * @author Petre Maierean
 *
 */
@Controller
public class HomeController {
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	@Autowired
	private PageService pageService;
	
	/**
	 * Get the home page
	 * @param model
	 * @return
	 */
	@RequestMapping("/index")
	public String getIndex(final Model model) {
		logger.debug("Get the home page");
		Translation translation = pageService.getTranslation(null);
		model.addAttribute("translation", translation);
		return "home";
	}
	
	@RequestMapping("/update")
	public String update(final HttpServletRequest request, final Model model) {
		List<String> replacements = new ArrayList<String>();
		for(int i=1; i<24; i++) {
			String s = request.getParameter("s" + i);
			if (StringUtils.isNotBlank(s)) {
				replacements.add(s.trim());
			}
			else {
				replacements.add("");
			}
		}
		logger.debug("Get the home page");
		Translation translation = pageService.getTranslation(replacements);
		model.addAttribute("translation", translation);
		return "home";
	}
}
