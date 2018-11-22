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
package org.apache.jackrabbit.oak.ocm.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.jackrabbit.oak.api.Result;
import org.apache.jackrabbit.oak.ocm.NodeConverter;
import org.apache.jackrabbit.oak.ocm.NodeConvertionException;
import org.apache.jackrabbit.oak.ocm.UnsupportedConvertionRequest;
import org.apache.jackrabbit.oak.ocm.annotation.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Petre Maierean
 *
 */
public class OakNodeConverterImpl implements NodeConverter {
	private static final Logger logger = LoggerFactory.getLogger(OakNodeConverterImpl.class);
	
	/**
	 * Convert a query result into a bean of the provided class. The class is expected to be annotated
	 * @param resultRow
	 * @param an annotated class
	 * @return an object 
	 */
	@Override
	public <T> List<T> convert(@Nonnull final Result result, @Nonnull final Class<T> clazz) throws NodeConvertionException {
		if (result != null && clazz != null) {
			if (!clazz.isAnnotationPresent(Node.class)) {
				throw new UnsupportedConvertionRequest();
			}
			Return<T> ret = new Return<T>();
			Node nt = clazz.getAnnotation(Node.class);
			String jcrType = nt.jcrType();
			String[] columns = result.getColumnNames();
			
			return ret.asList();
		}
		return null;
	}

	
	public class Return<T>  {
	    private List<T> list = new ArrayList<T>();

	    public void add(T t){
	        list.add(t);
	    }
	
	    public List<T> asList() {
	    	return list;
	    }
	}
}
