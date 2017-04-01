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
package org.maiereni.aem.admin.service;

import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.maiereni.aem.admin.bean.GroupBean;
import org.maiereni.aem.admin.bean.GroupUser;

/**
 * Defines a service to verify and read groups and users
 * @author Petre Maierean
 *
 */
public interface GroupUserService {
	/**
	 * Test if the id is Authotizable
	 * 
	 * @param resource
	 * @param id
	 * @return
	 * @throws Exception
	 */
	boolean hasAuthorizable(final Resource resource, final String id)
		throws Exception;
	/**
	 * Verify or create group. If the groupUser.isCommit() is false that the 
	 * group is not actually created until the session is saved
	 * 
	 * @param resource
	 * @param groupUser
	 * @throws Exception
	 */
	void verifyOrCreateGroup(final Resource resource, final GroupUser groupUser) 
		throws Exception;
	/**
	 * Verify of create user
	 * 
	 * @param resource
	 * @param groupUser
	 * @throws Exception
	 */
	void verifyOrCreateUserAccount(final Resource resource, final GroupUser groupUser) 
		throws Exception;
	/**
	 * List all group users that have permissions on a give web application
	 * @param resource
	 * @param contentPath
	 * @return
	 * @throws Exception
	 */
	List<GroupBean> findUsers4App(final Resource resource, final String contentPath)
		throws Exception;
}
