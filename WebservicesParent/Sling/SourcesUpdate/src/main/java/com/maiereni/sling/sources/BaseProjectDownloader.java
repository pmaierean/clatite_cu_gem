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
package com.maiereni.sling.sources;

import java.io.File;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maiereni.sling.sources.bo.Project;
import com.maiereni.sling.sources.bo.Repository;

/**
 * @author Petre Maierean
 *
 */
public abstract class BaseProjectDownloader implements ProjectDownloader {
	public static final SimpleDateFormat SDF = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	protected DownloaderListener listener;
	protected File root;
	
	public BaseProjectDownloader(final DownloaderListener listener, final File root) {
		this.listener = listener;
		this.root = root;
	}
	
	protected abstract void doDownloadProject(final Repository repository) throws Exception;
	
	@Override
	public void downloadProject(final Repository repository) {
		if (listener != null) {
			listener.notify(repository, "start", "Starts downloading the project");
		}
		try {
			doDownloadProject(repository);
			if (listener != null) {
				listener.notify(repository, "end", "Successfully download the project");
			}			
		}
		catch(Exception e) {
			if (listener != null) {
				listener.notify(repository, "error", "Failed to download the project");
			}			
		}
	}
}
