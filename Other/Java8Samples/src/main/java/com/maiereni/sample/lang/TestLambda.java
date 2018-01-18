/**
 * 
 */
package com.maiereni.sample.lang;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.junit.Test;

/**
 * @author Petre Maierean
 *
 */
public class TestLambda {

	@Test
	public void testLambda1() {
		List<String> simpleList = Arrays.asList(new String[] {"A simple string", "Second string", "Another one", "123", "whatever this is"});
		String maxHash = new ProcessString().getMaxHash(simpleList);
		assertEquals("The maximum hash is exepected", "Another one", maxHash);
	}
	
	@Test	
	public void testLambda2() {
		List<Integer> arr = Arrays.asList(new Integer[] {1, 7, 22, 56, 81, 101, 191});
		int sum = SumConditional.sumWithCondition(arr, r -> true);
		assertEquals("Sum all", "459", "" + sum);
		int r1 = SumConditional.sumWithCondition(arr, r -> r <= 22);
		assertEquals("Sum small", "30", "" + r1);
		int r2 = SumConditional.sumWithCondition(arr, r -> r > 22);
		assertEquals("Sum small", "429", "" + r2);
	}
	
	private class ProcessString {
		private String result;
		
		public String getMaxHash(final List<String> arr) {
			arr.forEach(s -> {
				if (result == null || result.hashCode() < s.hashCode())
					result = s;
			});
			return result;
		}
	}
	
	public interface SumConditional {
		public static int sumWithCondition(List<Integer> numbers, Predicate<Integer> predicate) {
		    return numbers.parallelStream()
		    		.filter(predicate)
		    		.mapToInt(i -> i)
		    		.sum();
		}
	}
}
