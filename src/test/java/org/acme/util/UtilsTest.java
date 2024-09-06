package org.acme.util;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class UtilsTest {

	private static final String ALPHA_TEXT = "TEst";
	private static final String ALPHANUMERIC_TEXT = "Test1234";
	private static final String EMPTY_TEXT = "";
	private static final String NULL_TEXT = null;

	@Test
	public void testNotNullString_WhenTextIsNull() {
		String input = null;
		String result = Utils.notNullString(input);
		assertEquals("", result);
	}

	@Test
	public void testNotNullString_WhenTextIsNotNull() {
		String input = "Hello, World!";
		String result = Utils.notNullString(input);
		assertEquals("Hello, World!", result);
	}

	@Test
	public void testIsAlpha_WhenTextIsAlpha() {
		assertTrue(Utils.isAlpha(ALPHA_TEXT));
	}

	@Test
	public void testIsAlpha_WhenTextIsNotAlpha() {
		assertFalse(Utils.isAlpha(ALPHANUMERIC_TEXT));
	}

	@Test
	public void testIsAlpha_WhenTextIsEmpty() {
		assertFalse(Utils.isAlpha(EMPTY_TEXT));
	}

	@Test
	public void testCapitalize() {
		String result = Utils.capitalize(ALPHA_TEXT);
		assertEquals("Test", result);
	}

	@Test
	public void testText2IntArray() {
		assertEquals(Utils.text2IntArray("1,4,5"), Arrays.asList(1,4,5));
		assertTrue(Utils.text2IntArray(null).isEmpty());
	}

	@Test
	public void testCapitalize_WhenTextIsEmpty() {
		assertEquals(EMPTY_TEXT, Utils.capitalize(EMPTY_TEXT));
	}

	@Test
	public void testCapitalize_WhenTextIsNull() {
		assertEquals(EMPTY_TEXT, Utils.capitalize(NULL_TEXT));
	}


	@Test
	public void testIsValidNumber() {
		assertTrue(Utils.isValidNumber("9"));
		assertTrue(Utils.isValidNumber("-9"));
		assertTrue(Utils.isValidNumber("-0"));
		assertFalse(Utils.isValidNumber("aa"));
		assertFalse(Utils.isValidNumber("9a"));
		assertFalse(Utils.isValidNumber(""));
		assertFalse(Utils.isValidNumber(null));
	}
}
