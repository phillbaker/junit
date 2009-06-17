package org.junit.tests.running.classes;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.DependentParameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Parameterized;

@RunWith(DependentParameterized.class)
public class FibonacciTest {
	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] { { 0, 0 }, { 1, 1 }, { 2, 1 },
				{ 3, 2 }, { 4, 3 }, { 5, 5 }, { 6, 8 } });
	}

	private int fInput;

	private int fExpected;

	public FibonacciTest(int input, int expected) {
		fInput= input;
		fExpected= expected;
	}

	@Test
	public void test() {
		System.out.println("here");
		assertEquals(fExpected, fib(fInput));
	}

	private int fib(int x) {
		return 0;
	}
}
