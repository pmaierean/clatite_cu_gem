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
package com.maiereni.utils.archiving;

import java.io.File;

/**
 * @author Petre Maierean
 *
 */
public interface ArchivingUtility {
	/**
	 * Decompresses an archive file into the destination directory
	 * @param archiveFile
	 * @return
	 * @throws Exception
	 */
	File decompress(File archiveFile) throws Exception;
	/**
	 * Compresses the input file or directory to the output file
	 * @param input
	 * @param name the name of the compressed file
	 * @return
	 * @throws Exception
	 */
	File compress(File input, String name) throws Exception;
}
