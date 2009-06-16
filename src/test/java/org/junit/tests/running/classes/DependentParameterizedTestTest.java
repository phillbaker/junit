package org.junit.tests.running.classes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.matchers.JUnitMatchers.containsString;

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
import org.junit.runner.notification.RunListener;
import org.junit.runners.DependentParameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.model.InitializationError;

public class DependentParameterizedTestTest {
	@RunWith(DependentParameterized.class)
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
	public void countBeforeRun() throws Exception {
		//should only report the single @Test decorated method
		//as opposed to Parameterized's report of the cross
		//product of parameters and tests
		Runner runner= Request.aClass(FibonacciTest.class).getRunner();
		assertEquals(1, runner.testCount());
	}

	@Test
	public void countAfterRun() {
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
	public void prePlansNamedCorrectly() throws Exception {
		Runner runner= Request.aClass(FibonacciTest.class).getRunner();
		Description description= runner.getDescription();
		assertEquals("No Tests", description.getChildren().get(0).getDisplayName());
	}

	@Test
	public void countsEmptyTestsCorrectly() throws Exception {
		Runner runner= Request.aClass(FibonacciTest.class).getRunner();
		Description description= runner.getDescription();
		assertEquals(1, description.getChildren().size());
	}

	@Test
	public void testsAreRooted() {
		JUnitCore core= new JUnitCore();
		final Description[] startDesc = new Description[1];
	    core.addListener(new RunListener() {
		@Override
			public void testRunStarted(Description desc){
			startDesc[0] = desc;
		}
	    });
	    core.run(FibonacciTest.class);
	    assertTrue(!startDesc[0].toString().equals("null"));
	}

	private static String fLog;

	@RunWith(DependentParameterized.class)
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

	static public class ParametersOrder extends BeforeAndAfter {
		public ParametersOrder(int x) {
			super(x);
		}
		@Parameters
		public static Collection<Object[]> data() {
			fLog+= "parameters ";
			return Arrays.asList(new Object[][] { { 3 } });
		}
	}

	@Test
	public void parametersAreRunAfterBeforeClass() {
		fLog= "";
		JUnitCore.runClasses(ParametersOrder.class);
		assertEquals("before parameters after ", fLog);
	}

	@RunWith(DependentParameterized.class)
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

	@RunWith(DependentParameterized.class)
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

	@RunWith(DependentParameterized.class)
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

	@RunWith(DependentParameterized.class)
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

	@RunWith(DependentParameterized.class)
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
		DependentParameterized dp= new DependentParameterized(PrivateConstructor.class);
		dp.initParameters();
	}
}
