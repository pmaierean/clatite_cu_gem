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

import javax.jcr.Node;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.maiereni.aem.admin.bean.Id2Path;

/**
 * @author Petre Maierean
 *
 */
public class RemoveOptionalPermission extends RemovePermission {

	/**
	 * @param resourceResolverFactory
	 * @param commit
	 * @throws Exception
	 */
	public RemoveOptionalPermission(
		final ResourceResolverFactory resourceResolverFactory, 
		final Resource resource, 
		boolean commit)
		throws Exception {
		super(resourceResolverFactory, resource, commit);
	}

	@Override
	protected Object process(Object o) throws Exception {
		Id2Path arg = (Id2Path)o;
		boolean hasNode = false;
		try {
			Node node = session.getNode(arg.getPath());
			hasNode = true;
			if (StringUtils.isNotBlank(arg.getPrimaryTypeFilter()))
				if (!node.isNodeType(arg.getPrimaryTypeFilter()))
					hasNode = false;
		}
		catch(Exception e) {}
		if (hasNode)
			return super.process(arg);
		return null;
	}
}
