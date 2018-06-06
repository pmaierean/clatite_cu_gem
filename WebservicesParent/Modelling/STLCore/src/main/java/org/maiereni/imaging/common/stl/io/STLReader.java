/** ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */
package org.maiereni.imaging.common.stl.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import org.maiereni.imaging.common.stl.vo.Triangle;

/**
 * Base class that defines an STL stream reader
 * @author Petre Maierean
 */
public abstract class STLReader implements Closeable {
	
	protected InputStream is;
	protected String name;
	
	public STLReader(final InputStream is) throws IOException{
		if (is == null)
			throw new IOException("Unsupported call. The input stream cannot be null");
		this.is = is;
	}
	
	/**
	 * Get the name of the STL
	 * @return a string
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Read next triangle
	 * @return 
	 * @throws IOException
	 */
	public abstract Triangle nextTriangle() throws IOException;
	
	/**
	 * Close the contained Stream
	 */
    public void close() throws IOException {
    	if (is != null)
    		is.close();
    }
}
