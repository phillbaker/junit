package org.junit.runners;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.internal.runners.statements.RunDependentParameters;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * <p>
 * The custom runner <code>DependentParameterized</code> extends
 * <code>Parameterized</code> to collect parameters after any methods
 * annotated <code>&#064;BeforeClass</code> have been called. This is
 * appropriate for situations where a database connection might need
 * to be initialized to create the parameters.
 * Instances are created for the cross-product of the test methods
 * and the test data elements at runtime. Since the number of test
 * data elements may be dependent on external circumstances, this
 * number may not be known before the parameters are collected. Use is
 * identical to <code>Parameterized</code>, only internal execution is
 * different.
 * </p>
 *
 */
public class DependentParameterized extends Parameterized {

	private final ArrayList<Runner> runners= new ArrayList<Runner>();

	/**
	 * Only called reflectively. Do not use programmatically.
	 */
	public DependentParameterized(Class<?> klass) throws Throwable {
		super(klass, Collections.<Runner>emptyList());
	}

	@Override
	protected List<Runner> getChildren() {
		return runners;
	}

	/**
	 * Must be called to instantiate parameters.
	 * @throws Throwable
	 */
	public void initParameters() throws Throwable {
		List<Object[]> parametersList= getParametersList(getTestClass());
		for (int i= 0; i < parametersList.size(); i++)
			runners.add(new TestClassRunnerForParameters(getTestClass().getJavaClass(),
					parametersList, i));
	}

	/**
	 * Constructs a {@code Statement} to run all of the tests in the test class.
	 * Overridden to add parameters processing. See
	 * {@link ParentRunner#classBlock(RunNotifier)} for
	 * original logic, the additional implementation adds the following:
	 * <ul>
	 * <li>Call {@link #initParameters()} after all
	 * <code>&#064;BeforeClass</code> methods have been called.</li>
	 * </ul>
	 * @param notifier
	 * @return {@code Statement}
	 */
	@Override
	protected Statement classBlock(final RunNotifier notifier) {
		//order of execution is a bit confusing,
		//work out from center, so first is the tests (childrenInvoker)
		//then the parameters
		//then the BeforeClasses and the AfterClasses
		Statement statement= childrenInvoker(notifier);
		statement= withParameters(statement);
		statement= withBeforeClasses(statement);
		statement= withAfterClasses(statement);
		return statement;
	}

	/**
	 * Returns a {@link Statement}: run the first {@code @Parameters} method in this class
	 * before executing {@code statement}; if any throw an Exception, stop execution
	 * and pass the exception on.
	 */
	protected Statement withParameters(Statement statement) {
		statement= new RunDependentParameters(statement, this);
		return statement;
	}

	//
	// Implementation of Runner
	//

	@Override
	public Description getDescription() {
		Description description= super.getDescription();
		if(description.getChildren().size() == 0) {
			description= Description.createSuiteDescription(getTestClass().getJavaClass());
			Description child= Description.EMPTY.childlessCopy();
			for (FrameworkMethod method : getTestClass().getAnnotatedMethods(Test.class)) {
				child.addChild(Description.createTestDescription(getTestClass().getJavaClass(), method.getName()));
			}
			description.addChild(child);
			return description;
		}
		else {
			return description;
		}
	}

}
