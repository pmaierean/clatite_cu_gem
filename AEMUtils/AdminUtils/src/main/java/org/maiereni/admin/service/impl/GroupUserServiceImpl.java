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
package org.maiereni.admin.service.impl;

import java.util.List;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.maiereni.aem.admin.bean.GroupBean;
import org.maiereni.aem.admin.bean.GroupUser;
import org.maiereni.aem.admin.command.FindUsers4App;
import org.maiereni.aem.admin.command.VerifyAuthorizable;
import org.maiereni.aem.admin.command.VerifyOrCreateGroup;
import org.maiereni.aem.admin.command.VerifyOrCreateUserAccount;
import org.maiereni.aem.admin.service.GroupUserService;

import com.day.cq.wcm.api.PageManagerFactory;


/**
 * An implementation of the GroupUserService
 * @author Petre Maierean
 *
 */
@Component(
        label = "Utility Group and User Service",
       	immediate = true, 
        metatype = true,
        policy = ConfigurationPolicy.OPTIONAL
)
@Service
public class GroupUserServiceImpl implements GroupUserService {
	@Reference
	private ResourceResolverFactory resolverFactory;
	@Reference
	private PageManagerFactory pageManagerFactory;
	
	/**
	 * List all group users that have permissions on a give web application
	 * @param resource
	 * @param contentPath
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<GroupBean> findUsers4App(final Resource resource, final String contentPath)
		throws Exception {
		return (List<GroupBean>)new FindUsers4App(resolverFactory, resource, pageManagerFactory).processAndCommit(contentPath);
	}
	/**
	 * Test if the id is Authotizable
	 * 
	 * @param resource - if null then use admin session
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public boolean hasAuthorizable(final Resource resource, final String id)
			throws Exception {
		return ((Boolean)new VerifyAuthorizable(resolverFactory, resource).processAndCommit(id)).booleanValue();
	}

	/**
	 * Verify or create group
	 * 
	 * @param resource
	 * @param groupUser
	 * @throws Exception
	 */
	public void verifyOrCreateGroup(final Resource resource, final GroupUser groupUser) 
		throws Exception {
		new VerifyOrCreateGroup(resolverFactory, resource).processAndCommit(groupUser);
	}

	/**
	 * Verify or create user
	 * 
	 * @param resource
	 * @param groupUser
	 * @throws Exception
	 */
	public void verifyOrCreateUserAccount(final Resource resource, final GroupUser groupUser) throws Exception {
		new VerifyOrCreateUserAccount(resolverFactory, resource).processAndCommit(groupUser);
	}

	public ResourceResolverFactory getResolverFactory() {
		return resolverFactory;
	}
	public void setResolverFactory(ResourceResolverFactory resolverFactory) {
		this.resolverFactory = resolverFactory;
	}
	public PageManagerFactory getPageManagerFactory() {
		return pageManagerFactory;
	}
	public void setPageManagerFactory(PageManagerFactory pageManagerFactory) {
		this.pageManagerFactory = pageManagerFactory;
	}
}
