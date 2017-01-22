package net.amygdalum.regexparser;

import static com.almondtools.conmatch.exceptions.ExceptionMatcher.matchesException;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import net.amygdalum.regexparser.RegexCompileException;

public class RegexCompileExceptionTest {

	@Test
	public void testIORuntimeException() throws Exception {
		assertThat(new RegexCompileException("a(b", 3, ")"), matchesException(RegexCompileException.class)
			.withMessage("Regular expression a(b fails to compile at position 3, missing: )"));
	}

}
