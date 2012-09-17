import static org.junit.Assert.*;

import org.junit.Test;


public class TestParenthesesSanitiser {

	@Test
	public void testGivenStringWithNoParentheses_thenSameStringIsReturned() {
		assertEquals("Hello World!", new ParenthesesSanitiser("Hello World!").sanitised());
	}

	@Test
	public void testGivenEmptyString_thenEmptyStringIsReturned() {
		assertEquals("", new ParenthesesSanitiser("").sanitised());
	}

	@Test
	public void testGivenStringWithOneValidParentheses_thenSameStringIsReturned() {
		assertEquals("abc(def)abc", new ParenthesesSanitiser("abc(def)abc").sanitised());
	}

	@Test
	public void testGivenStringBookendedWithValidParentheses_thenSameStringIsReturned() {
		assertEquals("()abcdefabc()", new ParenthesesSanitiser("()abcdefabc()").sanitised());
	}

	@Test
	public void testGivenStringStartingWithOpenParentheses_thenValidStringIsReturned() {
		assertEquals("abc", new ParenthesesSanitiser("(abc").sanitised());
	}

	@Test
	public void testGivenStringEndingWithOpenParentheses_thenValidStringIsReturned() {
		assertEquals("abc", new ParenthesesSanitiser("abc(").sanitised());
	}

	@Test
	public void testGivenStringStartingWithCloseParentheses_thenSanitisedStringIsValid() {
		assertEquals("abc", new ParenthesesSanitiser(")abc").sanitised());
	}

	@Test
	public void testGivenStringEndingWithCloseParentheses_thenSanitisedStringIsValid() {
		assertEquals("abc", new ParenthesesSanitiser("abc)").sanitised());
	}

	@Test
	public void testGivenStringWithInvalidParentheses_thenSanitisedStringIsValid() {
		assertEquals("abc(def)ghi", new ParenthesesSanitiser("abc((def)ghi").sanitised());
	}

	@Test
	public void testGivenStringWithManyInvalidParentheses_thenSanitisedStringIsValid() {
		assertEquals("abc(def)ghi", new ParenthesesSanitiser("abc((def)gh(i").sanitised());
	}

	@Test
	public void testGivenStringWithOnlyInvalidParentheses_thenSanitisedStringIsValid() {
		assertEquals("()()((()())())", new ParenthesesSanitiser(")))()()((()())())(((").sanitised());
	}

}
