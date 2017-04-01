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

import javax.jcr.ValueFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.maiereni.aem.admin.bean.GroupUser;

/**
 * Verifies if a user exists and if not creates it
 * @author Petre Maierean
 *
 */
public class VerifyOrCreateUserAccount extends AbstractCommand {
	
	public VerifyOrCreateUserAccount(		
		final ResourceResolverFactory resourceResolverFactory,
		final Resource resource) throws Exception {
		super(resourceResolverFactory, resource);
	}

	@Override
	protected Object process(Object o) throws Exception {
		if (o == null)
			throw new Exception("The argument cannot be null");
		GroupUser groupUser = (GroupUser)o;
		UserManager manager = session.getUserManager();
		if (!isAuthorizable(manager, groupUser.getUserName())) {
			ValueFactory vf = session.getValueFactory();
			logger.debug("Create user {}", groupUser.getUserName());
			User user = manager.createUser(groupUser.getUserName(),
					groupUser.getPwd());
			if (StringUtils.isNotBlank(groupUser.getFirstName()))
				user.setProperty("cq:first-name",
						vf.createValue(groupUser.getFirstName()));
			if (StringUtils.isNotBlank(groupUser.getLastName()))
				user.setProperty("cq:last-name",
						vf.createValue(groupUser.getLastName()));
			if (StringUtils.isNotBlank(groupUser.getEmail()))
				user.setProperty("profile/email",
						vf.createValue(groupUser.getEmail()));
			if (StringUtils.isNotBlank(groupUser.getGroupId())) {
				Authorizable agrp = manager.getAuthorizable(groupUser
						.getGroupId());
				if (agrp == null)
					throw new Exception(groupUser.getGroupId()
							+ " is an unexisting group");
				if (agrp instanceof Group) {
					logger.debug("Added user {} to group {}", new Object[] {
							groupUser.getUserName(), groupUser.getGroupId() });
					((Group) agrp).addMember(user);
				} else
					throw new Exception(groupUser.getGroupId()
							+ " is not a group name");
			}
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
