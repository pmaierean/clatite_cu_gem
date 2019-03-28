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
package net.sf.ehcache.distribution.jgroups;

import net.sf.ehcache.config.FactoryConfiguration;

/**
 * @author Petre Maierean
 *
 */
public class JGroupsCacheManagerPeerProviderConfiguration
	extends FactoryConfiguration<JGroupsCacheManagerPeerProviderConfiguration> {
	
	public JGroupsCacheManagerPeerProviderConfiguration() {
		this("classpath:/udp.xml", "sample");
	}

	public JGroupsCacheManagerPeerProviderConfiguration(final String cfgFile, final String channelName) {
		this.fullyQualifiedClassPath = JGroupsCacheManagerPeerProviderFactory.class.getName();
		this.propertySeparator = ",";
		this.properties = "file=" + cfgFile + ",channelName=" + channelName;
	}
}
