/**
 * 
 */
package com.maiereni.sample.lang;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Demonstrates the use of parallel stream processing of the content of a collection
 * @author Petre Maierean
 *
 */
public class ParallelStreamProcessing {
	private static final Logger logger = LoggerFactory.getLogger(ParallelStreamProcessing.class);
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		List<Integer> myList = new ArrayList<>();
		for(int i=0; i<100; i++) myList.add(i);
		Stream<Integer> sequentialStream = myList.stream();
		
		Stream<Integer> parallelStream = myList.parallelStream();
		Stream<Integer> highNums = parallelStream.filter(p -> p > 90);
		highNums.forEach(p -> logger.debug("High Nums parallel="+p));
		
		Stream<Integer> highNumsSeq = sequentialStream.filter(p -> p > 90);
		highNumsSeq.forEach(p -> logger.debug("High Nums sequential="+p));
	}

}
