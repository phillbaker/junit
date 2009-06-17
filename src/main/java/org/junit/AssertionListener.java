package org.junit;

public interface AssertionListener {
	//convenience method
	public void asserted(String message, Object expected);//or maybe asserted(String type, String message, Object expected)
	//specific listeners
	public void assertedArrayEquals(String message, Object expected);
	public void assertedEquals(String message, Object expected);
	public void assertedTrue(String message);//expected true
	public void assertedFalse(String message);//expected false
	public void assertedNotNull(String message);//expected not null
	public void assertedNotSame(String message, Object expected);
	public void assertedNull(String message);//expected null
	public void assertedSame(String message, Object expected);
	//public void assertedThat(String message, Matcher<T> matcher);//from Hamcrest? Dunno...
}
