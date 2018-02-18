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
package com.maiereni.blpapi.importer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositoryCache;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.impl.DefaultServiceLocator.ErrorHandler;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.LocalRepositoryManager;
import org.junit.Before;
import org.junit.Test;

import com.maiereni.blpapi.importer.bo.PackageBean;

/**
 * @author Petre Maierean
 *
 */
public class BlpApiImporterTest {
	public static final String PACKAGE_LOCATION_PROPERTY = "com.maiereni.blpapi.importer.location";
	private static final Log logger = new SystemStreamLog();
	private BlpApiImporter apiImporter;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	    DefaultServiceLocator serviceLocator = MavenRepositorySystemUtils.newServiceLocator();
	    serviceLocator.setErrorHandler(new ErrorHandler() {
	        @Override
	        public final void serviceCreationFailed(final Class<?> type, final Class<?> impl, final Throwable exception) {
	          if (exception != null) {
	            exception.printStackTrace();
	          }
	        }
	      });
	    LocalRepository localRepository = new LocalRepository(new File(System.getProperty("user.dir", ".m2/repository")));
	    RepositorySystem repositorySystem = serviceLocator.getService(RepositorySystem.class);
	    assertNotNull(repositorySystem);
	    DefaultRepositorySystemSession repositorySystemSession = MavenRepositorySystemUtils.newSession();
	    LocalRepositoryManager localRepositoryManager = repositorySystem.newLocalRepositoryManager(repositorySystemSession, localRepository);
	    repositorySystemSession.setLocalRepositoryManager(localRepositoryManager);
	    assertNotNull(repositorySystemSession);
	    repositorySystemSession.setOffline(true);
	    repositorySystemSession.setCache(new DefaultRepositoryCache());
	    
	    apiImporter = new BlpApiImporter();
	    apiImporter.setLog(new SystemStreamLog());
	    apiImporter.repoSession = repositorySystemSession;
	    apiImporter.repositorySystem = repositorySystem;
	}

	@Test
	public void testVersion() {
		try {
			apiImporter.isVersionUpToDate(BlpApiImporter.DEFAULT_GROUP_ID, BlpApiImporter.DEFAULT_API_ID,"3.11.1.1");
		}
		catch(Exception e) {
			logger.error("Failed to process", e);
			fail("Unexpected behavior");
		}
	}

	@Test
	public void testImport() {
		String root = System.getProperty(PACKAGE_LOCATION_PROPERTY);
		if (StringUtils.isNotBlank(root)) {
			PackageBean packageBean = new PackageBean();
			packageBean.setArtifactId(BlpApiImporter.DEFAULT_API_ID);
			packageBean.setGroupId(BlpApiImporter.DEFAULT_GROUP_ID);
			packageBean.setPath(root);
			try {
				File f = apiImporter.findJar(packageBean);
				assertNotNull(f);
				assertTrue(f.exists());
				String version = apiImporter.getActualVersion(packageBean);
				assertNotNull(version);
				packageBean.setVersion(version);
				String newVersion = apiImporter.updateVersion(packageBean);
				assertEquals(version, newVersion);
			}
			catch(Exception e) {
				logger.error("Failed to process", e);
				fail("Unexpected behavior");
			}
			
		}

	}
}
