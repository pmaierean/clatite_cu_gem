/**
 * ================================================================
 *  Copyright (c) 2017-2019 Maiereni Software and Consulting Inc
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
package com.maiereni.authorization.cmd;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;

/**
 * @author Petre Maierean
 *
 */
public class IsResource extends AbstractCommand {

	public IsResource(ResourceResolverFactory resourceResolverFactory, Resource resource, boolean askToCommit)
			throws Exception {
		super(resourceResolverFactory, resource, askToCommit);
	}

	@Override
	protected Object process(Object o) throws Exception {
		return isResource((String)o) ? Boolean.TRUE : null;
	}

	@Override
	protected void validateArgument(Object o) throws Exception {
		if (!(o != null && o instanceof String)) {
			throw new Exception("Unexpected argument");
		}
	}

}
