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

import java.security.Principal;

import javax.jcr.security.AccessControlManager;

import org.apache.jackrabbit.api.security.principal.PrincipalManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;

import com.maiereni.authorization.bo.Id2Path;

/**
 * @author Petre Maierean
 *
 */
public class RemovePermission extends AbstractRemovePermission {
	/**
	 * @param resourceResolverFactory
	 * @throws Exception
	 */
	public RemovePermission(
		final ResourceResolverFactory resourceResolverFactory, 
		final Resource resource,
		boolean commit)
		throws Exception {
		super(resourceResolverFactory, resource, commit);
	}

	@Override
	protected Object doProcess(Object o) throws Exception {
		Id2Path arg = (Id2Path)o;
		logger.debug("Remove permission for {} on path {}", new Object[] { arg.getId(), arg.getId()});
		PrincipalManager pm = session.getPrincipalManager();
		Principal principal = pm.getPrincipal(arg.getId());
		if (principal == null)
			throw new Exception("Could not resolve a principal for: '" + arg.getId() + "'");
		AccessControlManager aMgr = session.getAccessControlManager();
		removePermission(aMgr, principal, arg.getPath());
		return null;
	}
	
	@Override
	protected boolean isOptionalNode(final Id2Path arg) {
		return false;
	}
}
