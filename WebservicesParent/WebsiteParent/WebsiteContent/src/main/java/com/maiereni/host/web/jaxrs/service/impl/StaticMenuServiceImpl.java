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

import java.util.ArrayList;
import java.util.Locale;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;

import com.maiereni.host.web.bo.Menu;
import com.maiereni.host.web.bo.MenuItem;
import com.maiereni.host.web.jaxrs.service.MenuService;

/**
 * @author Petre Maierean
 *
 */
public class StaticMenuServiceImpl implements MenuService {
	private static final Logger logger = LoggerFactory.getLogger(StaticMenuServiceImpl.class);
	private MessageSource messageSource;
	
	StaticMenuServiceImpl(@Nonnull final MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	/**
	 * Get the menu for the given user and locale
	 * @param userId identifies the user
	 * @param locale identifies the language of the call
	 * @return the menu
	 */
	@Override
	public Menu getMenu(final String userId, @Nonnull final Locale locale) {
		Menu menu = new Menu();
		menu.setMenus(new ArrayList<MenuItem>());
		menu.setType("root");
		
		return menu;
	}

}
