/**
 * 
 */
package com.maiereni.sample.lang;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.junit.Test;

/**
 * @author Petre Maierean
 *
 */
public class TestProcessor {
	
	@Test
	public void testProcessor() {
		MyConsumer consumer = new MyConsumer();
		//creating sample Collection
		final List<Integer> myList = Arrays.asList(new Integer[] {1,100, 101, 102, 1550, 1551});
		
		//traversing through forEach method of Iterable with anonymous class
		myList.forEach(consumer);
		assertEquals("Expected result", "3405", consumer.getSum());
	}

	private class MyConsumer implements Consumer<Integer> {
		private Integer sum = new Integer(0);
		private Processor processor = null;
		public MyConsumer() {
			processor = new Processor() {
				@Override
				public Integer addNumbers(Integer a, Integer b) {
					return !(a==null||b==null)? a + b: null;
				}
			};
		}
		public void accept(Integer t) {
			sum = processor.addNumbers(sum, t);
		}
		
		public String getSum() {
			Processor.logValue(sum);
			return sum.toString();
		}
		
	}
}
