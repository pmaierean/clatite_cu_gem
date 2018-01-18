/**
 * 
 */
package com.maiereni.sample.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Petre Maierean
 *
 */
@FunctionalInterface
public interface Processor {
	public static final Logger logger = LoggerFactory.getLogger(Processor.class);
	
	Integer addNumbers(Integer a, Integer b);
	
	public static void logValue(Integer a) {
		logger.debug("This is the value " + a);
	}
}
