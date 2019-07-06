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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.jar.Manifest;

import org.apache.commons.lang3.StringUtils;

import com.maiereni.synchronizer.git.service.bo.ContentPath;
import com.maiereni.synchronizer.git.service.bo.LayoutRules;

/**
 * A simple Sling Manifest File Builder
 * @author Petre Maierean
 *
 */
class SlingManifestFileBuilder {
	private static final String VERSION = "Manifest-Version: %s\r\n";
	private static final String DESCRIPTION_TPML = "Bundle-Description: %s\r\n";
	private static final String SYMBOLIC_NAME_TPML = "Bundle-SymbolicName: %s\r\n";
	private static final String LAST_MODIFIED_TPML = "Bnd-LastModified: %d\r\n";
	private static final String BUNDLE_MF_VERSION = "Bundle-ManifestVersion: %d\r\n";
	private static final String BUNDLE_VENDOR = "Bundle-Vendor: %s\r\n";
	protected static final String INITIAL_CONTENT_TPML = "Sling-Initial-Content: %s\r\n";
	private static final String BUNDLE_NAME_TMPL = "Bundle-Name: %s\r\n"; 
	private static final String BUNDLE_VERSION = "Bundle-Version: %s";
	private static final String FLAGS = ";overwrite:=true;uninstall:=true;path:=";

	/**
	 * Creates a manifest object for the specified rules and the contents associated
	 * @param rules
	 * @param contents
	 * @return
	 */
	public Manifest createManifestFile(final LayoutRules rules, final List<ContentPath> contents) throws Exception {
		byte[] buffer = getContent(rules, contents);
		try (ByteArrayInputStream is = new ByteArrayInputStream(buffer)) {
			return new Manifest(is);
		}
	}

	public byte[] getContent(final LayoutRules rules, final List<ContentPath> contents) throws Exception {
		try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			os.write(format(VERSION, "1.0"));
			os.write(format(DESCRIPTION_TPML, rules.getDescription()));
			os.write(format(SYMBOLIC_NAME_TPML, rules.getSymbolicName()));
			os.write(format(LAST_MODIFIED_TPML, System.currentTimeMillis()));
			os.write(format(BUNDLE_MF_VERSION, 2));
			StringBuffer sb = new StringBuffer();
			for(ContentPath content: contents) {
				if (sb.length() > 0)
					sb.append(";");
				sb.append(content.getName()).append(FLAGS).append(content.getPath());
			}
			os.write(format(INITIAL_CONTENT_TPML, sb.toString()));
			os.write(format(BUNDLE_VENDOR, rules.getVendor()));
			os.write(format(BUNDLE_NAME_TMPL, rules.getName()));
			os.write(format(BUNDLE_VERSION, rules.getVersion()));
			return os.toByteArray();
		}		
	}
	
	protected String formatEntry(final String tmpl, final String s) {
		String ret = "";
		if (StringUtils.isNotBlank(s)) {
			ret = String.format(tmpl, s);
			if (ret.length() > 70) {
				StringBuffer sb = new StringBuffer();
				for(int i=0; i<ret.length();) {
					int j = i + 70;
					if (i > 0) {
						sb.append("\r\n ");
						j = j - 1;
					}
					if ( j > ret.length() ) {
						j = ret.length();
					}
					sb.append(ret.substring(i, j));
					i = j;
				}
				ret = sb.toString();
			}
		}
		return ret;
	}
	
	private byte[] format(final String tmpl, final String s) {
		String ret = formatEntry(tmpl, s);
		return ret.getBytes();
	}

	private byte[] format(final String TMPL, final long l) {
		String ret = String.format(TMPL, l);
		return ret.getBytes();
	}
}
