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
package com.maiereni.oak.filestore;

import java.io.File;

import org.springframework.context.ApplicationContext;

import com.maiereni.oak.BaseOakTests;

/**
 * @author Petre Maierean
 *
 */
public abstract class BaseFileStoreTest extends BaseOakTests {
	protected static final ApplicationContext APPLICATION_CONTEXT;
	
	public ApplicationContext getApplicationContext() {
		return APPLICATION_CONTEXT;
	}

	static {
		File f = null;
		try {
			f  = File.createTempFile("test", ".me");
			String path = f.getPath();
			path = path.substring(0,  path.length() - 3);
			File tmpDir = new File(path);
			if (!tmpDir.mkdirs()) {
				throw new Exception();
			}
			System.setProperty(FileSystemOakBeanFactory.REPO_PATH_KEY, tmpDir.getPath());
			APPLICATION_CONTEXT = initialize("/com/maiereni/sample/oak/test/filestore/application.xml");
		}
		catch(Exception e) {
			throw new ExceptionInInitializerError(e);
		}
		finally {
			if (f != null) {
				if (!f.delete())
					f.deleteOnExit();
			}
		}
	}
}
