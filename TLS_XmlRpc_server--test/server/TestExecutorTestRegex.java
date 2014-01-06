package server;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Contains all regex related test.
 * @author Adrien Droguet
 * @see TestExecutorTest
 */
public class TestExecutorTestRegex {
	
	/**
	 * OK
	 */
	@Test
	public void testIsScriptValid_regex_match() {
		assertTrue(TestExecutor.isScriptValid(
				"FakeTestCase",
				"public class FakeTestCase {"));
	}

	/**
	 * Same as testIsScriptValid_regex_match(), but with something after the
	 * bracket.
	 */
	@Test
	public void testIsScriptValid_regex_notAnEmptyClass() {
		assertTrue(TestExecutor.isScriptValid(
				"FakeTestCase",
				"public class FakeTestCase { something; }"));
	}
	
	/**
	 * Same as testIsScriptValid_regex_match(), but with imported package before
	 * class declaration.
	 */
	@Test
	public void testIsScriptValid_regex_import() {
		assertTrue(TestExecutor.isScriptValid(
				"FakeTestCase",
				"import something;\npublic class FakeTestCase {"));
	}
	
	/**
	 * It's OK if your class extends something.
	 */
	@Test
	public void testIsScriptValid_regex_valid_extends_OK() {
		assertTrue(TestExecutor.isScriptValid(
				"FakeTestCase",
				"public class FakeTestCase extends something {"));
	}
	
	/**
	 * But not nothing.
	 */
	@Test
	public void testIsScriptValid_regex_valid_extends_nothing() {
		assertFalse(TestExecutor.isScriptValid(
				"FakeTestCase",
				"public class FakeTestCase extends  {"));
	}
	
	/**
	 * It's also OK to implement something. Note that users should extend before
	 * implementing.
	 */
	@Test
	public void testIsScriptValid_regex_valid_implements_single() {
		assertTrue(TestExecutor.isScriptValid(
				"FakeTestCase",
				"public class FakeTestCase implements something {"));
	}
	
	/**
	 * But not nothing.
	 */
	@Test
	public void testIsScriptValid_regex_valid_implements_single_nothing() {
		assertFalse(TestExecutor.isScriptValid(
				"FakeTestCase",
				"public class FakeTestCase implements {"));
	}
	
	/**
	 * It's also OK to implement multiple things.
	 */
	@Test
	public void testIsScriptValid_regex_valid_implements_multiple() {
		assertTrue(TestExecutor.isScriptValid(
				"FakeTestCase",
				"public class FakeTestCase implements something, thing {"));
	}
	
	/**
	 * Even if you don't put space after your commas.
	 */
	@Test
	public void testIsScriptValid_regex_valid_implements_multipleC() {
		assertTrue(TestExecutor.isScriptValid(
				"FakeTestCase",
				"public class FakeTestCase implements something,thing {"));
	}
	/**
	 * But not if you don't put anything after your commas.
	 */
	@Test
	public void testIsScriptValid_regex_valid_implements_multiple_nothing() {
		assertFalse(TestExecutor.isScriptValid(
				"FakeTestCase",
				"public class FakeTestCase implements something, {"));
	}
	
	/**
	 * Mismatching test and test class names are now allowed.<br>
	 * Well, not anymore but the compiler will complain anyway.
	 */
	@Test
	public void testIsScriptValid_regex_mismatch() {
		assertTrue(TestExecutor.isScriptValid(
				"Mismatch",
				"public class FakeTestCase {"));
	}
	
	/**
	 * Invalid class structure.
	 */
	@Test
	public void testIsScriptValid_regex_invalidStructure_missingClass() {
		assertFalse(TestExecutor.isScriptValid(
				"FakeTestCase",
				"public FakeTestCase{"));
	}
	
	/**
	 * Another invalid class structure, there is no'{'.
	 */
	@Test
	public void testIsScriptValid_regex_invalidStructure_bracket() {
		assertFalse(TestExecutor.isScriptValid(
				"FakeTestCase",
				"public class FakeTestCase"));
	}
}
