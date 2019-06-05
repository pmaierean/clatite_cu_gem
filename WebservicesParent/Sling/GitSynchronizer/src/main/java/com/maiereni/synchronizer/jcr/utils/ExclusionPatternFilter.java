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

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
/**
 * @author Petre Maierean
 *
 */
public class ExclusionPatternFilter implements FileFilter {
	public static final String GIT_EXCLUSION_PATTERN = ".*(\\x2egit).*";
	private Pattern pattern;
	
	public ExclusionPatternFilter() {
	}

	public ExclusionPatternFilter(final String s) {
		if (StringUtils.isNotBlank(s)) {
			pattern = Pattern.compile(s);
		}
		else {
			pattern = Pattern.compile(GIT_EXCLUSION_PATTERN);
		}
	}

	@Override
	public boolean accept(File f) {
		return !pattern.matcher(f.getPath()).matches();
	}

}
