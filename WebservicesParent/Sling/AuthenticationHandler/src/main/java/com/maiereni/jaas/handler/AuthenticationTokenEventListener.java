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
package com.maiereni.jaas.handler;

import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An event listener that detects the changes in the JCR node hierarchy related to authentication
 * @author Petre Maierean
 *
 */
public class AuthenticationTokenEventListener implements EventListener {
	private static final Logger log = LoggerFactory.getLogger(AuthenticationTokenEventListener.class);

	@Override
	public void onEvent(EventIterator events) {
		log.debug("A new event");
	}

}
