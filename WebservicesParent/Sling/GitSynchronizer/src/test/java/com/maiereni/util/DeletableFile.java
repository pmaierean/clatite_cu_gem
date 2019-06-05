/**
 * ================================================================
 *  Copyright (c) 2017-2019 Maiereni Software and Consulting Inc
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
package com.maiereni.util;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

/**
 * @author Petre Maierean
 *
 */
public class DeletableFile implements Closeable {
	private File tmpFile;
	
	public DeletableFile() throws Exception {
		tmpFile = File.createTempFile("tmp", ".enc");
	}

	public DeletableFile(final String ext) throws Exception {
		tmpFile = File.createTempFile("tmp", ext);
	}

	public File getTmpFile() {
		return tmpFile;
	}
	
	public String getPath() {
		return tmpFile.getPath();
	}
	
	@Override
	public void close() throws IOException {
		if (tmpFile != null) {
			if (!tmpFile.delete())
				tmpFile.deleteOnExit();
		}
	}

}
