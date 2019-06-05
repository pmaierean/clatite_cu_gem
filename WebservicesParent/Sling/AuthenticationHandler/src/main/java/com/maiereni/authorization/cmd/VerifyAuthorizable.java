/**
 * Copyright 2019 Maiereni Software and Consulting Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */
package com.maiereni.authorization.cmd;

import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;

/**
 * Verifies if an id points to an authorizable entity
 * @author Petre Maierean
 *
 */
public class VerifyAuthorizable extends AbstractCommand {

	public VerifyAuthorizable(
			final ResourceResolverFactory resourceResolverFactory,
			final Resource resource)
			throws Exception {
		super(resourceResolverFactory, resource);
	}

	@Override
	protected Object process(Object o) throws Exception {
		String id = (String)o;
		UserManager manager = session.getUserManager();
		return new Boolean(isAuthorizable(manager, id));
	}

	@Override
	protected void validateArgument(final Object o) throws Exception {
		if (o == null)
			throw new Exception("The ID to verify for cannot be null");	
		if (!(o instanceof String))
			throw new Exception("The argument must be String");
	}

}
