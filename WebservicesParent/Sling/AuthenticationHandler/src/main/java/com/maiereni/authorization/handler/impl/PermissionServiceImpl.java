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
package com.maiereni.authorization.handler.impl;
import static org.apache.sling.engine.EngineConstants.FILTER_SCOPE_REQUEST;
import static org.apache.sling.engine.EngineConstants.SLING_FILTER_SCOPE;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maiereni.authorization.bo.Id2Path;
import com.maiereni.authorization.bo.IdPathPriviledges;
import com.maiereni.authorization.cmd.IsResource;
import com.maiereni.authorization.cmd.RemovePermission;
import com.maiereni.authorization.cmd.RemovePermissionsOnAllChildNodes;
import com.maiereni.authorization.cmd.SetPermission;
import com.maiereni.authorization.handler.PermissionService;

/**
 * Implementation of the PermissionService
 * @author Petre Maierean
 *
 */
@Component(
	service = PermissionService.class,
    name = "com.maiereni.authorization.handler.PermissionService",
    immediate = true,
    property = { SLING_FILTER_SCOPE + "=" + FILTER_SCOPE_REQUEST,
    		Constants.SERVICE_RANKING + ":Integer=1" }
)
public class PermissionServiceImpl implements PermissionService {
	private static final Logger logger = LoggerFactory.getLogger(PermissionServiceImpl.class);
	@Reference
	private ResourceResolverFactory resolverFactory;

	private int maxTestingCycles = 10;
	
	public ResourceResolverFactory getResolverFactory() {
		return resolverFactory;
	}

	public void setResolverFactory(ResourceResolverFactory resolverFactory) {
		this.resolverFactory = resolverFactory;
	}

	@Override
	public void removePermission(Resource resource, Id2Path id2Path) throws Exception {
		new RemovePermission(resolverFactory, resource, resource == null).processAndCommit(id2Path);
	}

	/**
	 * Remove permissions on all child nodes
	 * 
	 */
	@Override
	public void removePermissionsOnAllChildNodes(Resource resource, Id2Path id2Path) throws Exception {
		new RemovePermissionsOnAllChildNodes(resolverFactory, resource, resource == null).processAndCommit(id2Path);
	}

	/**
	 * Sets the permission
	 * @param resource null or the resource resolver
	 * @param idPathPriviledges
	 * @throws Exception
	 */
	@Override
	public void setPermission(final Resource resource, final IdPathPriviledges idPathPriviledges) throws Exception {
		Thread th = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					for(int i = 0; i<maxTestingCycles; i++) {
						SetPermission permission = new SetPermission(resolverFactory, null, true);
						if (permission.processAndCommit(idPathPriviledges) != null) {
							break;
						}
						try {
							Thread.sleep(10000L);
						}
						catch(Exception e) {
						}
						logger.debug("Try finding resource");
					}
				}
				catch(Exception e) {
					logger.error("Failed to set permission", e);
				}
			}
		});
		th.setDaemon(true);
		th.start();
	}

	/**
	 * Verifies if the resource exists
	 * 
	 * @param path
	 * @return
	 */
	public boolean isResource(String path) throws Exception {
		return new IsResource(resolverFactory, null, true).processAndCommit(path) != null;
	}
}
