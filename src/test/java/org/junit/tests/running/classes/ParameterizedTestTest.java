package org.junit.tests.running.classes;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.experimental.results.PrintableResult.testResult;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.model.InitializationError;

public class ParameterizedTestTest {
	@RunWith(Parameterized.class)
	static public class FibonacciTest {
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
			assertEquals(fExpected, fib(fInput));
		}

		private int fib(int x) {
			return 0;
		}
	}

	@Test
	public void count() {
		Result result= JUnitCore.runClasses(FibonacciTest.class);
		assertEquals(7, result.getRunCount());
		assertEquals(6, result.getFailureCount());
	}

	@Test
	public void failuresNamedCorrectly() {
		Result result= JUnitCore.runClasses(FibonacciTest.class);
		assertEquals(String
				.format("test[1](%s)", FibonacciTest.class.getName()), result
				.getFailures().get(0).getTestHeader());
	}

	@Test
	public void countBeforeRun() throws Exception {
		Runner runner= Request.aClass(FibonacciTest.class).getRunner();
		assertEquals(7, runner.testCount());
	}

	@Test
	public void plansNamedCorrectly() throws Exception {
		Runner runner= Request.aClass(FibonacciTest.class).getRunner();
		Description description= runner.getDescription();
		assertEquals("[0]", description.getChildren().get(0).getDisplayName());
	}

	@RunWith(Parameterized.class)
	static public class ExtendedParameters extends FibonacciTest {
		public ExtendedParameters(int input, int expected) {
			super(input, expected);
		}
	}

	@Test
	public void runsExtendedParametersClass() {
		Result result= JUnitCore.runClasses(ExtendedParameters.class);
		assertEquals(7, result.getRunCount());
		assertEquals(6, result.getFailureCount());
	}

	@RunWith(Parameterized.class)
	static public class OverriddenParameters extends FibonacciTest {
		public OverriddenParameters(int input, int expected) {
			super(input, expected);
		}
		@Parameters
		public static Collection<Object[]> data() {
			return Arrays.asList(new Object[][] { { 0, 0 }, { 1, 1 } });
		}
		@Parameters
		public static Collection<Object[]> moreData() {
			return Arrays.asList(new Object[][] { { 0, 0 }, { 1, 1 }, { 2, 1 } });
		}
	}

	@Test
	public void runsOverriddenParametersClass() {
		Result result= JUnitCore.runClasses(OverriddenParameters.class);
		assertEquals(2, result.getRunCount());
		assertEquals(1, result.getFailureCount());
	}

	private static String fLog;

	@RunWith(Parameterized.class)
	static public class BeforeAndAfter {
		@BeforeClass
		public static void before() {
			fLog+= "before ";
		}

		@AfterClass
		public static void after() {
			fLog+= "after ";
		}

		public BeforeAndAfter(int x) {

		}

		@Parameters
		public static Collection<Object[]> data() {
			return Arrays.asList(new Object[][] { { 3 } });
		}

		@Test
		public void aTest() {
		}
	}

	@Test
	public void beforeAndAfterClassAreRun() {
		fLog= "";
		JUnitCore.runClasses(BeforeAndAfter.class);
		assertEquals("before after ", fLog);
	}

	@RunWith(Parameterized.class)
	static public class EmptyTest {
		@BeforeClass
		public static void before() {
			fLog+= "before ";
		}

		@AfterClass
		public static void after() {
			fLog+= "after ";
		}
	}

	@Test
	public void validateClassCatchesNoParameters() {
		Result result= JUnitCore.runClasses(EmptyTest.class);
		assertEquals(1, result.getFailureCount());
	}

	@RunWith(Parameterized.class)
	static public class MultipleParametersMethods {
		@Test
		public int test() {
			return 0;
		}

		@Parameters
		public static Collection<Object[]> data() {
			return Arrays.asList(new Object[][] { { 0 } });
		}

		@Parameters
		public static Collection<Object[]> moreData() {
			return Arrays.asList(new Object[][] { { 0 }, { 0 } });
		}
	}

	@Test
	public void firstParametersMethodUsed() {
		Result result= JUnitCore.runClasses(MultipleParametersMethods.class);
		assertEquals(1, result.getRunCount());
	}

	@RunWith(Parameterized.class)
	static public class IncorrectTest {
		@Test
		public int test() {
			return 0;
		}

		@Parameters
		public static Collection<Object[]> data() {
			return Collections.singletonList(new Object[] {1});
		}
	}

	@Test
	public void failuresAddedForBadTestMethod() throws Exception {
		Result result= JUnitCore.runClasses(IncorrectTest.class);
		assertEquals(1, result.getFailureCount());
	}

	@RunWith(Parameterized.class)
	static public class ProtectedParametersTest {
		@Parameters
		protected static Collection<Object[]> data() {
			return Collections.emptyList();
		}

		@Test
		public void aTest() {
		}
	}

	@Test
	public void meaningfulFailureWhenParametersNotPublic() throws Exception {
		Result result= JUnitCore.runClasses(ProtectedParametersTest.class);
		String expected= String.format(
				"No public static parameters method on class %s",
				ProtectedParametersTest.class.getName());
		assertEquals(expected, result.getFailures().get(0).getMessage());
	}

	@RunWith(Parameterized.class)
	static public class WrongElementType {
		@Parameters
		public static Collection<String> data() {
			return Arrays.asList("a", "b", "c");
		}

		@Test
		public void aTest() {
		}
	}

	@Test
	public void meaningfulFailureWhenParameterListsAreNotArrays() {
		String expected= String.format(
				"%s.data() must return a Collection of arrays.",
				WrongElementType.class.getName());
		assertThat(testResult(WrongElementType.class).toString(),
				containsString(expected));
	}
	
	@RunWith(Parameterized.class)
	static public class PrivateConstructor {
		private PrivateConstructor(int x) {

		}

		@Parameters
		public static Collection<Object[]> data() {
			return Arrays.asList(new Object[][] { { 3 } });
		}

		@Test
		public void aTest() {
		}
	}
	
	@Test(expected=InitializationError.class)
	public void exceptionWhenPrivateConstructor() throws Throwable {
		new Parameterized(PrivateConstructor.class);
	}
}
