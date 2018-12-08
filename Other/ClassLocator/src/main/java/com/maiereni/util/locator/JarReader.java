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
package com.maiereni.util.locator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Jar reader
 * 
 * @author Petre Maierean
 *
 */
public class JarReader {
	private File outputFolder;
	private List<File> extractedFile = new ArrayList<File>();
	
	public List<File> getExtractedFile() {
		return extractedFile;
	}

	public File getOutputFolder() {
		return outputFolder;
	}

	public void setOutputFolder(File outputFolder) {
		this.outputFolder = outputFolder;
		if (!this.outputFolder.exists())
			this.outputFolder.mkdirs();
	}

	public void init() {
		extractedFile.clear();
		if (outputFolder != null) {
			File[] children = outputFolder.listFiles();
			if (children != null) {
				int max = children.length;
				for(int i=0; i<max; i++)
					remove(children[i]);
			}
		}
	}
	
	private void remove(File f) {
		if (f.isDirectory()) {
			File[] children = f.listFiles();
			if (children != null) {
				int max = children.length;
				for(int i=0; i<max; i++)
					remove(children[i]);
			}
		}
		if (!f.delete())
			f.deleteOnExit();
	}
	
	public boolean isInJar(File jarFile, String className) throws Exception  {
		boolean ret = false;
		try(ZipFile file = new ZipFile(jarFile);){
			Enumeration<? extends ZipEntry> children = file.entries();
			while(children.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) children.nextElement();
				String name = entry.getName();
				if (name.indexOf(className)>=0)
					if (outputFolder != null && outputFolder.exists()) {
						File storage = getOutputFile(name);
						InputStream is = file.getInputStream(entry);
						try {
							extract(is, storage);
						}
						finally {
							if (is != null)
								try{
									is.close();
								}
								catch(Exception e) {
									
								}
						}
						if (!extractedFile.contains(storage))
							extractedFile.add(storage);
					}
					else {
						ret = true;
						break;
					}
			}
		}
		catch(Exception e) {
			System.out.println("Error opening: " + jarFile.getPath());
			e.printStackTrace();
		}
		return ret;
	}
	
	private void extract(InputStream is, File storage) throws Exception {
		FileOutputStream os = null;
		try {
			os = new FileOutputStream(storage);
			byte[] buffer = new byte[4098];
			for(int i=0; (i = is.read(buffer))>0; ) {
				os.write(buffer, 0, i);
			}
		}
		finally{
			if (os != null)
				try{
					os.close();
				}
				catch(Exception e) {
					
				}			
		}
	}
	
	private File getOutputFile(String name) {
		String s = name.substring(0, name.lastIndexOf("/"));
		if (s.startsWith("/"))
			s = s.substring(1);
		File fDir = new File(outputFolder, s);
		System.out.println("Make output directory " + fDir.getPath());
		if (!fDir.exists())
			fDir.mkdirs();
		File res = new File(fDir, name.substring(name.lastIndexOf("/") + 1));
		System.out.println("Create temp storage file: " + res.getPath());
		return res;
	}
}
