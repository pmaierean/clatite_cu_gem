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
import org.maiereni.aem.admin.bean.Id2Path;
import org.maiereni.aem.admin.bean.IdPathPriviledges;
import org.maiereni.aem.admin.bean.PrivilegePathRuleError;

/**
 * Defines the API of a service that tests permissions based on a rule definitions 
 * @author Petre Maierean
 *
 */
public interface PermissionService {
	/**
	 * Remove all permissions on a resource
	 * 
	 * @param resource
	 * @param id2path
	 * @param commit
	 * @throws Exception
	 */
	void removePermission(final Resource resource, final Id2Path id2Path, final boolean commit) throws Exception;
	/**
	 * Remove permission on an optional resource
	 * 
	 * @param resource
	 * @param id2path
	 * @param commit
	 * @throws Exception
	 */
	void removePermissionOptionalPath(final  Resource resource, final Id2Path id2Path, final boolean commit)
			throws Exception;
	/**
	 * Remove permissions on all child nodes of a given node
	 * 
	 * @param resource
	 * @param id2path
	 * @param commit
	 * @throws Exception
	 */
	void removePermissionsOnAllChildNodes(final  Resource resource, final Id2Path id2Path, final boolean commit)
			throws Exception;
	/**
	 * Set permission for an authorizable identity (either user ro group
	 * 
	 * @param resource
	 * @param idPathPriviledges
	 * @param commit commit the change
	 * @throws Exception
	 */
	void setPermission(final  Resource resource, final IdPathPriviledges idPathPriviledges, final boolean commit)
			throws Exception;
	/**
	 * Read the privilege rules defined at the configPath and checks them against the actual privilege of an
	 * authorizable  
	 * 
	 * @param resource
	 * @param authorizable
	 * @param configPath the configuration path
	 * @return
	 * @throws Exception
	 */
	List<PrivilegePathRuleError> matchPermissionRules(final  Resource resource, final String id, final String configPath) throws Exception;
}
