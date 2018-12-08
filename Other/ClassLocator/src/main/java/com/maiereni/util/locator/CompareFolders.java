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
import java.io.FileWriter;
import java.io.Writer;

import com.maiereni.util.locator.impl.FileComparatorFactory;

/**
 * Simple class that compares two folder structure, file to file and tells the
 * difference between them. Files are filtered by extension 
 * 
 * @author Petre Maierean
 *
 */
public class CompareFolders {
	
	public void compare(File folder1, File folder2, String compareProps) throws Exception{
		if (folder1 == null)
			throw new Exception("Parameter 1 is null");
		if (folder2 == null)
			throw new Exception("Parameter 2 is null");
		if (!folder1.isDirectory())
			throw new Exception(folder1.getPath() + "Is not a folder");
		if (!folder2.isDirectory())
			throw new Exception(folder2.getPath() + "Is not a folder");
		FileComparator fc = new FileComparatorFactory(compareProps);
		Writer w = null;
		try {
			w = new FileWriter("result.txt");
			compare(folder1, folder2, fc, w);
		}
		finally {
			if (w != null)
				try {
					w.close();
				}
			catch(Exception e){}
		}
	}
	
	private void compare(File folder1, File folder2, FileComparator fc, Writer w) throws Exception {
		if (!folder2.isDirectory()) {
			System.out.println("Folder: '" + folder2.getPath() + "' does not exist");
			if (w != null)
				w.write("Folder: '" + folder2.getPath() + "' does not exist\r\n");
			return;
		}
		File[] children = folder1.listFiles(fc);
		for(int i=0; i<children.length; i++) {
			String fname = children[i].getName();
			File f = new File(folder2, fname);
			if (children[i].isDirectory()) {
				compare(children[i], f,  fc, w);
			}
			else {
				fc.compareFiles(children[i], f, w);
			}
		}
	}
	
	public static void main(String[] args) {
		CompareFolders comparator = new CompareFolders();
		try {
			if (args.length < 3)
				throw new Exception("Expected arguments: folder1 folder2 file_selection_pattern");
			String folder1 = args[0];
			String folder2 = args[1];
			String properties = args[2];
			comparator.compare(new File(folder1), new File(folder2), properties);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
