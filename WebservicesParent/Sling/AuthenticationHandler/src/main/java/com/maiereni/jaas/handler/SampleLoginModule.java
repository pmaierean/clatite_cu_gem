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

import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Sample Login Module
 * @author Petre Maierean
 *
 */
public class SampleLoginModule implements LoginModule {
	private static final Logger log = LoggerFactory.getLogger(SampleLoginModule.class);
	private static final DetectSourceCache cache = new DetectSourceCache();
    private Subject subject;
    private CallbackHandler callbackHandler;
    private Map<String, ?> sharedState;
    private Map<String, ?> options;
	@Override
	public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState,
		Map<String, ?> options) {
		this.subject = subject;
		this.callbackHandler = callbackHandler;
		this.sharedState = sharedState;
		this.options = options;
	}
    
	
	@Override
	public boolean login() throws LoginException {
		try {
			NameCallback nc = new NameCallback("The name", "simple");
			PasswordCallback pwc = new PasswordCallback("the password", false);
			Callback[] callbacks = new Callback[] {
				nc, pwc
			};
			callbackHandler.handle(callbacks);
			if (pwc.getPassword() != null) {
				log.error("The user name is " + nc.getName() + ". The password is " + new String(pwc.getPassword()), new Exception(""));
			}
			/*else {
				cache.recordEvent(nc.getName(), new Exception());
			}*/
		}
		catch(Exception e) {
			log.error("Failed to process", e);
		}
		return false;
	}

	@Override
	public boolean commit() throws LoginException {
		return false;
	}

	@Override
	public boolean abort() throws LoginException {
		return false;
	}

	@Override
	public boolean logout() throws LoginException {
		return false;
	}

}
