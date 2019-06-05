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

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;
/**
 * 
 * @author Petre Maierean
 *
 */
public abstract class AbstractModifyingCommand extends AbstractCommand {

	public AbstractModifyingCommand(
			final ResourceResolverFactory resourceResolverFactory, 
			final Resource resource, 
			boolean askToCommit)
			throws Exception {
		super(resourceResolverFactory, resource, askToCommit);
	}

	public AbstractModifyingCommand(
			final ResourceResolverFactory resourceResolverFactory, 
			final Resource resource) 
			throws Exception {
		super(resourceResolverFactory);
	}
	
	/**
	 * Ensures that if the class is using an administrative session then it is saved
	 * after the modification
	 */
	public Object processAndCommit(final Object o) throws Exception {
		if (session == null)
			this.askToCommit = true;
		return super.processAndCommit(o);
	}

}
