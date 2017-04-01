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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Value;
import javax.jcr.security.AccessControlEntry;
import javax.jcr.security.AccessControlList;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.AccessControlPolicy;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.maiereni.aem.admin.bean.GroupBean;
import org.maiereni.aem.admin.bean.GroupUser;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.PageManagerFactory;

/**
 * Resolves the users and groups that have been refereed to in the ACL of a page 
 * @author Petre Maierean
 *
 */
public class FindUsers4App extends AbstractCommand {
	private PageManagerFactory pageManagerFactory;
	private PageManager pageManager;
	
	public FindUsers4App(
		final ResourceResolverFactory resourceResolverFactory, 
		final Resource resource,
		final PageManagerFactory pageManagerFactory)
			throws Exception {
		super(resourceResolverFactory, resource);
		if (pageManagerFactory == null) 
			throw new Exception("Invalid case. The page manager factory cannot be null");
		this.pageManagerFactory = pageManagerFactory;
	}

	@Override
	protected Object process(Object o) throws Exception {
		String path = (String)o;

		Page page = pageManager.getPage(path);
		if (page == null)
			throw new Exception("No page found at: " + path);
		logger.debug("A page was resolved to the path {} ", path);
		List<String> seen = new ArrayList<String>();
		List<GroupBean> groupBeans = new ArrayList<GroupBean>();
		AccessControlManager aMgr = session.getAccessControlManager();
		UserManager userManager = session.getUserManager();
		AccessControlPolicy[] policies = aMgr.getEffectivePolicies(path);
		for(AccessControlPolicy policy : policies) {
			if (policy instanceof AccessControlList) {
				AccessControlList lst = (AccessControlList)policy;
				for(AccessControlEntry entry : lst.getAccessControlEntries()) {
					Principal principal = entry.getPrincipal();
					Authorizable authorizable = userManager.getAuthorizable(principal);
					if (authorizable instanceof Group) { 
						Group group = (Group) authorizable;
						String groupName = group.getID();
						if (!seen.contains(groupName)) {
							GroupBean bean = getGroupBean(group);
							groupBeans.add(bean);
							seen.add(groupName);
						}
					}
				}
			}
		}
		logger.debug("Found a number of {} groups assigned to the path", groupBeans.size());
		return groupBeans;
	}
	
	@Override
	protected void validateArgument(final Object o) throws Exception {
		if (o == null)
			throw new Exception("The resource path cannot be null");	
		if (!(o instanceof String))
			throw new Exception("The argument must be String");	
	}

	protected GroupBean getGroupBean(final Group group) 
		throws Exception {
		String groupName = group.getID();
		GroupBean bean = new GroupBean();
		bean.setName(groupName);
		bean.setId(group.getID());
		bean.setName(getProperty(group, "cq:group-name"));
		Iterator<Authorizable> iPrincipals = group.getDeclaredMembers();
		while (iPrincipals.hasNext()) {
			Authorizable a = (Authorizable)iPrincipals.next();
			GroupUser user = convert(a);
			user.setGroupId(groupName);
			if (bean.getGroupUser() == null)
				bean.setGroupUser(new ArrayList<GroupUser>());
			bean.getGroupUser().add(user);
		}
		return bean;
	}
	
	protected GroupUser convert(final Authorizable authorizable) throws Exception {
		GroupUser groupUser = new GroupUser();
		if (authorizable instanceof User) {
			User user = (User) authorizable;
			groupUser.setUserName(user.getID());
			groupUser.setFirstName(getProperty(user, "cq:first-name"));
			groupUser.setLastName(getProperty(user, "cq:last-name"));
			groupUser.setFirstName(getProperty(user, "profile/email"));
		}
		return groupUser;
	}

	private String getProperty(final User user, final String key) throws Exception {
		Value[] vals = user.getProperty(key);
		if (vals != null && vals.length == 0)
			return vals[0].toString();
		return null;
	}
	
	private String getProperty(final Group group, final String key) throws Exception {
		Value[] vals = group.getProperty(key);
		if (vals != null && vals.length == 0)
			return vals[0].toString();
		return null;
	}

	protected void initSession(final ResourceResolver resourceResolver) throws Exception {
		super.initSession(resourceResolver);
		pageManager = pageManagerFactory.getPageManager(resourceResolver);
	}
}
