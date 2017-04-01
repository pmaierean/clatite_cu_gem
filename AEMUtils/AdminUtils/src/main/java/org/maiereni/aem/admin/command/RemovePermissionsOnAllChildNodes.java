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

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.security.AccessControlManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.api.security.principal.PrincipalManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.maiereni.aem.admin.bean.Id2Path;

/**
 * 
 * @author Petre Maierean
 *
 */
public class RemovePermissionsOnAllChildNodes extends AbstractRemovePermission {

	/**
	 * @param resourceResolverFactory
	 * @param askToCommit
	 * @throws Exception
	 */
	public RemovePermissionsOnAllChildNodes(
		final ResourceResolverFactory resourceResolverFactory, 
		final Resource resource,
		boolean askToCommit)
		throws Exception {
		super(resourceResolverFactory, resource, askToCommit);
	}

	@Override
	protected Object doProcess(Object o) throws Exception {
		Id2Path arg = (Id2Path)o;
		PrincipalManager pm = session.getPrincipalManager();
		Principal principal = pm.getPrincipal(arg.getId());
		if (principal == null)
			throw new Exception("Could not resolve a principal for: '" + arg.getId() + "'");
		AccessControlManager aMgr = session.getAccessControlManager();

		Node n = session.getNode(arg.getPath());
		NodeIterator iter = n.getNodes();
		while (iter.hasNext()) {
			Node child = iter.nextNode();
			if (child.getName().equals("rep:policy"))
				continue;
			String childPath = child.getPath();
			if (StringUtils.isNotBlank(arg.getPrimaryTypeFilter())) {
				if (!child.isNodeType(arg.getPrimaryTypeFilter()))
					continue;
			}
			removePermission(aMgr, principal, childPath);
		}
		return null;
	}
}
