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
package com.maiereni.sample.sparx;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

import org.sparx.Collection;
import org.sparx.Repository;

/**
 * @author Petre Maierean
 *
 */
public class BaseClient implements Closeable {
	protected Repository repository;
	private File eaFile;
	
	public BaseClient(final File f) throws Exception {
		if (f == null)
			throw new Exception("The argument cannot be null");
		if (!f.exists())
			throw new Exception("Cannot find the file at " + f.getPath());
		this.eaFile = f;
	}
	
	public Repository getRepository() {
		refresh();
		return repository;
	}
	
	protected String[] getLessOne(final String[] names) {
		String[] ret = new String[names.length - 1];
		for(int i=1; i<names.length; i++) {
			ret[i-1] = names[i];
		}
		return ret;
	}
	
	
	/**
	 * Close the file and the repository object
	 */
	@Override
	public void close() throws IOException {
		if (repository != null) {
			repository.CloseFile();
			repository.Exit();
			repository.destroy();
			repository = null;
		}
	}
	
	public void refresh() {
		if (repository != null) {
			repository.RefreshOpenDiagrams(true);
			Collection<org.sparx.Package> models = repository.GetModels();
			if (models != null) {
				models.Refresh();	
			}
			else {
				System.out.println("ERROR: The reposiotry is corrupted");
				try {
					close();					
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		if (repository == null) {
			System.out.println("Open repository");
			repository = new Repository();
			if (!repository.OpenFile(eaFile.getPath())) {
				System.out.println("ERROR: The file cannot be open " + eaFile.getPath());
			}
			System.out.println("The repository is open");
		}
	}
}
