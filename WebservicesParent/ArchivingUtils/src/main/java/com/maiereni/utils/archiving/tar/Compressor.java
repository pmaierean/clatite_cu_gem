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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.annotation.Nonnull;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Petre Maierean
 *
 */
class Compressor {
	private static final Logger logger = LoggerFactory.getLogger(Compressor.class);

	/**
	 * Compresses the 
	 * @param input It can be either a single file or a directory
	 * @return
	 * @throws IOException
	 */
	public File compress(@Nonnull final File input) throws Exception {
		if (!input.exists())
			throw new Exception("The input file does not exist");
		File fOut = File.createTempFile("compress", "tar.gz");
        try (
       		FileOutputStream os = new FileOutputStream(fOut);
        	GzipCompressorOutputStream gos = new GzipCompressorOutputStream(os);
        	TarArchiveOutputStream out = new TarArchiveOutputStream(gos)){
        	out.setBigNumberMode(TarArchiveOutputStream.BIGNUMBER_STAR);
        	out.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
        	out.setAddPaxHeadersForNonAsciiNames(true);
       		addToArchiveCompression(out, input, ".");
        }
        return fOut;
    }
	
    private void addToArchiveCompression(TarArchiveOutputStream out, File file, String dir) throws IOException {
        String entry = dir + File.separator + file.getName();
        if (file.isFile()){
            out.putArchiveEntry(new TarArchiveEntry(file, entry));
            try (FileInputStream in = new FileInputStream(file)){
                IOUtils.copy(in, out);
            }
            out.closeArchiveEntry();
        } else if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null){
                for (File child : children){
                    addToArchiveCompression(out, child, entry);
                }
            }
        } else {
        	logger.error(file.getName() + " is not supported");
        }
    }
}
