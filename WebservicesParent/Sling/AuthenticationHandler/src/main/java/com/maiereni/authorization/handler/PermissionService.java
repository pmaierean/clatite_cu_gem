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
package com.maiereni.authorization.handler;

import org.apache.sling.api.resource.Resource;

import com.maiereni.authorization.bo.Id2Path;
import com.maiereni.authorization.bo.IdPathPriviledges;

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
	 * @throws Exception
	 */
	void removePermission(Resource resource, Id2Path id2Path) throws Exception;
	/**
	 * Remove permissions on all child nodes of a given node
	 * 
	 * @param resource
	 * @param id2path
	 * @throws Exception
	 */
	void removePermissionsOnAllChildNodes(Resource resource, Id2Path id2Path) throws Exception;
	/**
	 * Set permission for an authorizable identity (either user ro group
	 * 
	 * @param resource
	 * @param idPathPriviledges
	 * @param commit commit the change
	 * @throws Exception
	 */
	void setPermission(Resource resource, IdPathPriviledges idPathPriviledges) throws Exception;
	/**
	 * Verifies if the resource exists
	 * 
	 * @param path
	 * @return
	 */
	boolean isResource(String path) throws Exception;
}
