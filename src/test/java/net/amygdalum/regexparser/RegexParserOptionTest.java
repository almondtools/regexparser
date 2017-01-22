package net.amygdalum.regexparser;

import static com.almondtools.conmatch.conventions.EnumMatcher.isEnum;
import static net.amygdalum.regexparser.RegexParserOption.DOT_ALL;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import net.amygdalum.regexparser.RegexParserOption;

public class RegexParserOptionTest {

	@Test
	public void testRegexParserOption() throws Exception {
		assertThat(RegexParserOption.class, isEnum().withElements(1));
	}

	@Test
	public void testIn() throws Exception {
		assertThat(DOT_ALL.in(new RegexParserOption[] { DOT_ALL }), is(true));
		assertThat(DOT_ALL.in(new RegexParserOption[0]), is(false));
	}

}
