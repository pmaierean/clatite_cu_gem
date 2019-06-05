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

import javax.jcr.ValueFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;

import com.maiereni.authorization.bo.GroupUser;

/**
 * Verifies if group exists and if not then creates them
 * @author Petre Maierean
 *
 */
public class VerifyOrCreateGroup extends AbstractCommand {
	public VerifyOrCreateGroup(
		final ResourceResolverFactory resourceResolverFactory,
		final Resource resource)
		throws Exception {
		super(resourceResolverFactory, resource);
	}

	@Override
	protected Object process(Object o) throws Exception {
		GroupUser groupUser = (GroupUser)o;
		if (StringUtils.isEmpty(groupUser.getGroupId()))
			throw new Exception("The groupId cannot be null");
		UserManager manager = session.getUserManager();
		if (!isAuthorizable(manager, groupUser.getGroupId())) {
			ValueFactory vf = session.getValueFactory();
			logger.debug("Create group {}", groupUser.getGroupId());
			Group group = manager.createGroup(groupUser.getGroupId());
			if (StringUtils.isNotBlank(groupUser.getGroupName()))
				group.setProperty("cq:group-name",
						vf.createValue(groupUser.getGroupName()));
		}
		if (groupUser.isCommit()) {
			session.save();
			session.refresh(false);
		}

		
		return null;
	}
	@Override
	protected void validateArgument(final Object o) throws Exception {
		if (o == null)
			throw new Exception("The ID to verify for cannot be null");	
		if (!(o instanceof GroupUser))
			throw new Exception("The argument must be GroupUser");
	}
}
