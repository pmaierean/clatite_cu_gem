/**
 * Copyright 2017 Maiereni Software and Consulting Inc
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
package org.maiereni.aem.admin.command;

import java.security.Principal;

import javax.jcr.security.AccessControlManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.api.security.principal.PrincipalManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.maiereni.aem.admin.bean.IdPathPriviledges;

/**
 * @author PMaierean
 *
 */
public class SetPermission extends AbstractModifyingCommand {

	/**
	 * @param resourceResolverFactory
	 * @param askToCommit
	 * @throws Exception
	 */
	public SetPermission(
		final ResourceResolverFactory resourceResolverFactory,
		final Resource resource,
		boolean askToCommit) throws Exception {
		super(resourceResolverFactory, resource, askToCommit);
	}

	@Override
	protected Object process(Object o) throws Exception {
		IdPathPriviledges arg = (IdPathPriviledges)o;
		logger.debug("Set permission for group {} on path {}", new Object[] {
				arg.getId(), arg.getPath()});
		PrincipalManager pm = session.getPrincipalManager();
		Principal principal = pm.getPrincipal(arg.getId());
		if (principal == null)
			throw new Exception("Could not resolve a principal for: '" + arg.getId() + "'");
		AccessControlManager aMgr = session.getAccessControlManager();
		setPermission(aMgr, principal, arg.getPath(), arg.getPrivileges());
		
		return null;
	}

	@Override
	protected void validateArgument(Object o) throws Exception {
		if (o == null)
			throw new Exception("Invalid case. The argument cannot be null");
		if (!(o instanceof IdPathPriviledges))
			throw new Exception("Invalid case. The argument must be of type IdPathPriviledges");
		IdPathPriviledges arg = (IdPathPriviledges)o;
		if (StringUtils.isBlank(arg.getId()))
			throw new Exception("No id");
		if (StringUtils.isBlank(arg.getPath()))
			throw new Exception("No path");
		if (arg.getPrivileges() == null || arg.getPrivileges().length==0)
			throw new Exception("Null or blank privileges");
	}

}
