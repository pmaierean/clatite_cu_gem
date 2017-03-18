/**
 * Copyright 2017 Maiereni Software and Consulting Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */
package org.maiereni.aem.utils.synchronizer;

import org.apache.maven.plugin.logging.Log;

/**
 * @author Petre Maierean
 *
 */
public class ConsoleLog implements Log {

	public boolean isDebugEnabled() {
		return true;
	}

	public void debug(CharSequence content) {
		System.out.println(content);
	}

	public void debug(CharSequence content, Throwable error) {
		System.out.println(content);
		error.printStackTrace();
	}

	public void debug(Throwable error) {
		error.printStackTrace();
	}

	public boolean isInfoEnabled() {
		return false;
	}

	public void info(CharSequence content) {
		System.out.println(content);
	}

	public void info(CharSequence content, Throwable error) {
		System.out.println(content);
		error.printStackTrace();
	}

	public void info(Throwable error) {
		error.printStackTrace();
	}

	public boolean isWarnEnabled() {
		return false;
	}

	public void warn(CharSequence content) {
		System.out.println(content);
	}

	public void warn(CharSequence content, Throwable error) {
		System.out.println(content);
		error.printStackTrace();
	}

	public void warn(Throwable error) {
		error.printStackTrace();
	}

	public boolean isErrorEnabled() {
		return false;
	}

	public void error(CharSequence content) {
		System.out.println(content);
	}

	public void error(CharSequence content, Throwable error) {
		System.out.println(content);
		error.printStackTrace();
	}

	public void error(Throwable error) {
		error.printStackTrace();
	}

}
