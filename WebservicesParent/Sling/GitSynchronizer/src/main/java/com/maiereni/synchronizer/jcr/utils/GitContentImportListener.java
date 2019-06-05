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
package com.maiereni.synchronizer.jcr.utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.StringWriter;

import org.apache.sling.jcr.contentloader.ContentImportListener;

/**
 * @author Petre Maierean
 *
 */
public class GitContentImportListener implements ContentImportListener, Closeable {
	private StringWriter sw;
	
	public GitContentImportListener() {
		sw = new StringWriter();
	}
	
	@Override
	public void onModify(String srcPath) {
		sw.write("E " + srcPath + "\r\n");
	}

	@Override
	public void onDelete(String srcPath) {
		sw.write("X " + srcPath + "\r\n");
	}

	@Override
	public void onMove(String srcPath, String destPath) {
		sw.write("M " + srcPath + "\r\n");
	}

	@Override
	public void onCopy(String srcPath, String destPath) {
		sw.write("C " + srcPath + "\r\n");
	}

	@Override
	public void onCreate(String srcPath) {
		sw.write("A " + srcPath + "\r\n");
	}

	@Override
	public void onReorder(String orderedPath, String beforeSibbling) {
		sw.write("R " + orderedPath + " " + beforeSibbling + "\r\n");
	}

	@Override
	public void onCheckin(String srcPath) {
		sw.write("< " + srcPath + "\r\n");
	}

	@Override
	public void onCheckout(String srcPath) {
		sw.write("> " + srcPath + "\r\n");
	}

	public String toString() {
		return sw.toString();
	}

	@Override
	public void close() throws IOException {
		sw.close();
	}
}
