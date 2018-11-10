/**
 * ================================================================
 *  Copyright (c) 2017-2018 Maiereni Software and Consulting Inc
 * ================================================================
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.maiereni.oak.documentStore;

import org.apache.jackrabbit.oak.plugins.document.DocumentNodeStoreBuilder;
import org.apache.jackrabbit.oak.spi.state.NodeStore;
import org.springframework.stereotype.Component;

import com.maiereni.oak.OakBeanFactory;
import com.maiereni.oak.bo.RepositoryProperties;
/**
 * A Spring Framework factory class to create a DocumentNodeStore
 * 
 * @author Petre Maierean
 *
 */
@Component
public class DocumentNodeStoreOakBeanFactory extends OakBeanFactory {

	/**
	 * Get the properties for the Oak repository
	 * @return
	 */
	@Override
	public RepositoryProperties getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Get a node store
	 * @return
	 * @throws Exception
	 */
	@Override
	public NodeStore getNodeStore() throws Exception {
		DocumentNodeStoreBuilder builder = DocumentNodeStoreBuilder.newDocumentNodeStoreBuilder();
		
		return builder.build();
	}

}
