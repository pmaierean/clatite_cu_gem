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
package com.maiereni.consulting.website;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maiereni.authorization.bo.IdPathPriviledges;
import com.maiereni.authorization.handler.PermissionService;

/**
 * @author Petre Maierean
 *
 */
@Component(name = "com.maiereni.consulting.WebPage", immediate = true )
public class WebPage {
	private static final String ANONYMOUS = "anonymous";
	private static final String READ = "jcr:read";
	private final Logger log = LoggerFactory.getLogger(WebPage.class);
	private static final String CLIENT_LIBS = "/design/consulting/clientlibs";

	private static final String CSS_PATH = CLIENT_LIBS + "/css/";
	private static final String[] CSS_FILES = new String[] {
		"all.css", "all.min.css", "bootstrap.css", "bootstrap.css.map", "bootstrap.min.css", 
		"bootstrap.min.css.map", "freelancer.css", "freelancer.min.css", "magnific-popup.css"
	};

	private static final String JS_PATH = CLIENT_LIBS + "/js/";
	private static final String[] JS_FILES = new String[] {
		"bootstrap.bundle.js","bootstrap.bundle.js.map","bootstrap.bundle.min.js","bootstrap.bundle.min.js.map",
		"contact_me.js","contact_me.min.js","freelancer.js","freelancer.min.js","index.js","index.js.map",
		"jqBootstrapValidation.js","jqBootstrapValidation.min.js","jquery.easing.compatibility.js",
		"jquery.easing.js","jquery.easing.min.js","jquery.js","jquery.magnific-popup.js",
		"jquery.magnific-popup.min.js","jquery.min.js","jquery.min.map"
	};
	
    @Reference
    private PermissionService permission;
	
	@Activate
	protected void activate(ComponentContext componentContext)
		throws Exception {
		try {
			log.debug("Activate ApplicationAuthenticationHandler");
			setPriviledgeToNode(CLIENT_LIBS);
		}
		catch(Exception e) {
			log.error("Cannot set the permissions up", e);
		}
	}
	
	private void setPriviledgeToNode(final String path) throws Exception {
		IdPathPriviledges idPathPriviledges = new IdPathPriviledges(ANONYMOUS, path, ".*", new String[] { READ });
		permission.setPermission(null, idPathPriviledges);
		idPathPriviledges.setAsynchronous(true);
		log.debug("Allow anonymous access to: " + path);
	}
}
