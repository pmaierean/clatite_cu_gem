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

import javax.sql.DataSource;

import org.apache.jackrabbit.oak.plugins.document.rdb.RDBDataSourceFactory;
import org.apache.jackrabbit.oak.plugins.document.rdb.RDBDocumentNodeStoreBuilder;
import org.apache.jackrabbit.oak.spi.blob.BlobStore;
import org.apache.jackrabbit.oak.spi.state.NodeStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;

import com.maiereni.oak.OakBeanFactory;
import com.maiereni.oak.bo.RepositoryProperties;
/**
 * A Spring Framework factory class to create a DocumentNodeStore
 * 
 * @author Petre Maierean
 *
 */
public abstract class AbstractJDBCDocumentNodeStoreOakBeanFactory extends OakBeanFactory {
	private static final Logger logger = LoggerFactory.getLogger(AbstractJDBCDocumentNodeStoreOakBeanFactory.class);
	public static final String URL_KEY = "jdbc.url";
	public static final String USER_KEY = "jdbc.user";
	public static final String PASSWORD_KEY = "jdbc.password";
	public static final String DATASOURCE_KEY = "jdbc.datasource";
	public static final String OAK_CACHE_SIZE = "oak.cacheSize";
	public static final String OAK_CLUSTER_ID = "oak.clusterId";
	public static final String OAK_BLOB_STORE = "oak.blob.store";
	/**
	 * Get a node store
	 * @return
	 * @throws Exception
	 */
	@Bean(name="nodeStore")
	public NodeStore getNodeStore(final RepositoryProperties properties) throws Exception {
		logger.debug("Create the Document node store");
		DataSource datasource = properties.get(DATASOURCE_KEY, DataSource.class);
		if (datasource == null) {
			datasource = createDatasource(properties);
		}
		RDBDocumentNodeStoreBuilder builder = new RDBDocumentNodeStoreBuilder();
		builder.setRDBConnection(datasource);
		long cacheSize = Long.parseLong(properties.getAsString(OAK_CACHE_SIZE, "1"));
		builder.memoryCacheSize(cacheSize);
		int clusterId = Integer.parseInt(properties.getAsString(OAK_CLUSTER_ID, "1"));
        builder.setClusterId(clusterId);
        BlobStore blobStore = properties.get(OAK_BLOB_STORE, BlobStore.class);
        if (blobStore != null) {
        	builder.setBlobStore(blobStore);
        }
        builder.setLogging(false);
        
		return builder.build();
	}

	private DataSource createDatasource(final RepositoryProperties properties) throws Exception {
		DataSource ret = null;
		String url = properties.getAsString(URL_KEY, null);
		if (url == null) {
			throw new Exception("Cannot find the URL for the database");
		}
		String user = properties.getAsString(USER_KEY, "");
		String pwd = properties.getAsString(PASSWORD_KEY, "");
		ret = RDBDataSourceFactory.forJdbcUrl(url, user, pwd);
		return ret;
	}

}
