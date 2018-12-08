/**
 * 
 */
package com.maiereni.sample.lang;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Arrays;
import java.util.function.Consumer;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Petre Maierean
 *
 */
public class TestForEach {
	private static final Logger logger = LoggerFactory.getLogger(TestForEach.class);
	
	@Test
	public void testforEach() {
		logger.debug("Tests the ");
		//creating sample Collection
		final List<Integer> myList = Arrays.asList(new Integer[] {1,100, 101, 102, 1550, 1551});
		
		//traversing through forEach method of Iterable with anonymous class
		myList.forEach(new Consumer<Integer>() {
			private int index = 0;
			public void accept(Integer t) {
				assertEquals("", myList.get(index++), t);
			}
		});
		
		//traversing with Consumer interface implementation
		MyConsumer action = new MyConsumer();
		myList.forEach(action);
	}

	
	private class MyConsumer implements Consumer<Integer> {
		public void accept(Integer t) {
			logger.debug("This is the value {}", t);
		}
		
	}
}
