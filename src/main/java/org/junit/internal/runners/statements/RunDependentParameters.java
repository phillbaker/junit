/**
 *
 */
package org.junit.internal.runners.statements;

import org.junit.runners.model.Statement;
import org.junit.runners.DependentParameterized;

public class RunDependentParameters extends Statement {
	private final Statement fNext;

	private final DependentParameterized fTarget;

	public RunDependentParameters(Statement next, DependentParameterized target) {
		fNext= next;
		fTarget= target;
	}

	@Override
	public void evaluate() throws Throwable {
		fTarget.initParameters();
		fNext.evaluate();
	}
}