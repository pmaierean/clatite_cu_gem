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
package com.maiereni.osgi.felix.utils.model;

import org.apache.commons.lang3.StringUtils;

import com.maiereni.sling.info.Bundle;
import com.maiereni.sling.info.Model;

/**
 * @author Petre Maierean
 *
 */
class BundleGroupQualifier implements PostProcessor {
	public static final String FELIX = "osgi";
	public static final String JCR = "jcr";
	public static final String SLING = "sling";
	public static final String OTHER = "other";

	/**
	 * Post processes the model
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@Override
	public Model updataModel(final Model model) throws Exception {
		Model ret = model;
		for(Bundle bundle: ret.getBundles()) {
			if (StringUtils.isBlank(bundle.getGroup())) {
				String name = bundle.getName();
				if (name.contains("felix")) {
					bundle.setGroup(FELIX);
				}
				else if (name.contains("jackrabbit") || name.contains("jcr")) {
					bundle.setGroup(JCR);
				}
				else if (name.contains("sling") || name.contains("composum")) {
					bundle.setGroup(SLING);
				}
				else {
					bundle.setGroup(OTHER);
				}
			}
		}
		return ret;
	}
}
