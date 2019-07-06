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

import com.maiereni.synchronizer.git.service.bo.LayoutRule;

import bsh.StringUtil;
/**
 * @author Petre Maierean
 *
 */
public class ExclusionPatternFilter implements FileFilter {
	public static final String GIT_EXCLUSION_PATTERN = ".*(\\x2egit).*";
	public static final String GIT_INCLUSION_PATTERN = ".*";
	private Pattern inclusionPattern, exclusionPattern;
	
	public ExclusionPatternFilter(final LayoutRule layoutRule) {
		if (StringUtils.isNotBlank(layoutRule.getExclusionPattern())){
			exclusionPattern = Pattern.compile(layoutRule.getExclusionPattern());
		}
		else {
			exclusionPattern = Pattern.compile(GIT_EXCLUSION_PATTERN);			
		}
		if (StringUtils.isNotBlank(layoutRule.getInclusionPattern())) {
			inclusionPattern = Pattern.compile(layoutRule.getInclusionPattern());
		}
		else {
			inclusionPattern = Pattern.compile(GIT_INCLUSION_PATTERN);			
		}
	}

	@Override
	public boolean accept(File f) {
		boolean b = true;
		if (exclusionPattern.matcher(f.getPath()).matches()) {
			b = false;
		}
		else if (!inclusionPattern.matcher(f.getPath()).matches()) {
			b = false;
		}
		return b;
	}

}
