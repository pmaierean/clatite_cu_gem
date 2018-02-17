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
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * Simple decompressor utility class 
 * @author Petre Maierean
 *
 */
class Decompressor {

	/**
	 * Decompresses a tar.gz file to a temporary directory
	 * 
	 * @param in
	 * @param dest
	 * @return
	 * @throws IOException
	 */
	public File decompress(@Nonnull final File in, @Nonnull final File dest) throws Exception {
		if (!in.exists())
			throw new Exception("Cannot find the input file");
		if (!dest.isDirectory())
			throw new Exception("No such destination");
		File out = null;
        boolean hasEntries = false;
        try (
        	FileInputStream is = new FileInputStream(in);
        	GzipCompressorInputStream gis =	new GzipCompressorInputStream(is);
        	TarArchiveInputStream fin = new TarArchiveInputStream(gis)){
            TarArchiveEntry entry;
            while ((entry = fin.getNextTarEntry()) != null) {
            	hasEntries = true;
            	if (entry.isDirectory()) {
                    continue;
                }
            	String name = entry.getName();
            	if (name.startsWith("./"))
            		name = name.substring(2);
            	File crt = processEntry(dest, fin, name);
            	if (out == null) {
            		out = findParent(dest, crt);
            	}
            }
        }
        catch(Exception e) {
    		FileUtils.deleteDirectory(out);
    		throw e;
        }
        finally {
        	if (!hasEntries) {
        		if (out != null)
        			FileUtils.deleteDirectory(out);
        		throw new Exception("Failed to decompress the input file");
        	}
        }
        return out;
	}

	private File findParent(final File dest, final File crt) {
		File ret = crt.getParentFile();
		if (!ret.getParentFile().equals(dest))
			ret = findParent(dest, ret);
		return ret;
	}
	
	private File processEntry(final File out, final TarArchiveInputStream fin, final String name) throws Exception {
        File curfile = new File(out, name);
        File parent = curfile.getParentFile();
        if (!parent.exists())
            parent.mkdirs();
        try (FileOutputStream os = new FileOutputStream(curfile)) {
	        IOUtils.copy(fin, os);
        }		
        return curfile;
	}
	
	/**
	 * Decompresses a tar.gz file to a temporary directory
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public File decompress(@Nonnull final File in) throws Exception {
		File out = createTempDir();
        try (TarArchiveInputStream fin = new TarArchiveInputStream(new FileInputStream(in))){
            TarArchiveEntry entry;
            while ((entry = fin.getNextTarEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }
                File curfile = new File(out, entry.getName());
                File parent = curfile.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }
                IOUtils.copy(fin, new FileOutputStream(curfile));
            }
            return out;
        }
        catch(Exception e) {
        	FileUtils.deleteDirectory(out);
        	throw e;
        }
	}

	File createTempDir() throws Exception {
		File f = File.createTempFile("tmp", "deco");
		int ix = f.getPath().lastIndexOf(".");
		if (!f.delete())
			f.deleteOnExit();
		File dir = new File(f.getPath().substring(0, ix));
		if (!dir.mkdirs())
			throw new Exception("Could not create temp directory");
		return dir;
	}
}
