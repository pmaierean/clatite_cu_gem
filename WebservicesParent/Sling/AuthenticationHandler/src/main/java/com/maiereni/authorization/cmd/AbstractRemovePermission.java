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

import javax.jcr.Node;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;

import com.maiereni.authorization.bo.Id2Path;

/**
 * @author Petre Maierean
 *
 */
public abstract class AbstractRemovePermission extends AbstractModifyingCommand {

	/**
	 * @param resourceResolverFactory
	 * @param askToCommit
	 * @throws Exception
	 */
	public AbstractRemovePermission(
		final ResourceResolverFactory resourceResolverFactory, 
		final Resource resource,
		boolean askToCommit)
			throws Exception {
		super(resourceResolverFactory, resource, askToCommit);
	}
	
	protected abstract Object doProcess(Object o) throws Exception;
	
	@Override
	protected Object process(Object o) throws Exception {
		Id2Path arg = (Id2Path)o;
		if (isOptionalNode(arg))
			return doProcess(arg);
		return null;
	}
	
	protected boolean isOptionalNode(final Id2Path arg) {
		boolean hasNode = false;
		try {
			Node node = session.getNode(arg.getPath());
			hasNode = true;
			if (StringUtils.isNotBlank(arg.getPrimaryTypeFilter()))
				if (!node.isNodeType(arg.getPrimaryTypeFilter()))
					hasNode = false;
		}
		catch(Exception e) {}
		return hasNode;
	}

	@Override
	protected void validateArgument(Object o) throws Exception {
		if (o == null)
			throw new Exception("The argument cannot be null");
		if (!(o instanceof Id2Path))
			throw new Exception("The argument must be an Id2Path");
		Id2Path arg = (Id2Path)o;
		if (StringUtils.isBlank(arg.getId()))
			throw new Exception("No id");
		if (StringUtils.isBlank(arg.getPath()))
			throw new Exception("No path");
	}

}
