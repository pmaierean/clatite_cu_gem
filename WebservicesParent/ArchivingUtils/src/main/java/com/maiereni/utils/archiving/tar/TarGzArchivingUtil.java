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
package com.maiereni.utils.archiving.tar;

import java.io.File;

import javax.annotation.Nonnull;

import org.apache.commons.io.FileUtils;

import com.maiereni.utils.archiving.BaseArchivingUtility;

/**
 * An ArchivingUtility class that uses tar.gz type
 * @author Petre Maierean
 *
 */
public class TarGzArchivingUtil extends BaseArchivingUtility {
	private Compressor compressor;
	private Decompressor decompressor;

	public TarGzArchivingUtil() throws Exception {
		super();
		compressor = new Compressor();
		decompressor = new Decompressor();
	}
	
	/**
	 * Decompresses an archive file into the destination directory
	 * @param archiveFile
	 * @return
	 * @throws Exception
	 */
	@Override
	public File decompress(@Nonnull final File archiveFile) throws Exception {
		return decompressor.decompress(archiveFile, destDir);
	}

	/**
	 * Compresses the input file or directory to the output file
	 * @param input
	 * @param name
	 * @throws Exception
	 */
	@Override
	public File compress(@Nonnull final File input, @Nonnull final String name) throws Exception {
		if (!input.exists())
			throw new Exception("The input file does not exist");
		File f = compressor.compress(input);
		File dest = null;
		if (name.indexOf(delim) < 0)
			dest = new File(destDir, name);
		else
			dest = new File(name);
		FileUtils.copyFile(f, dest);
		if (!f.delete())
			f.deleteOnExit();
		return dest;
	}

}
