package net.amygdalum.regexparser;

import static com.almondtools.conmatch.conventions.ReflectiveEqualsMatcher.reflectiveEqualTo;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matcher;
import org.junit.Test;

public class CharNodeTest {

	@SuppressWarnings("unchecked")
	@Test
	public void testComputeComplement() {
		List<DefinedCharNode> set = charNodes(new SingleCharNode(':'));
		List<DefinedCharNode> all = charNodes(
			new RangeCharNode('\u0000', '\u0009'),
			new RangeCharNode('\u000b', '\u000c'),
			new RangeCharNode('\u000e', '\u0084'),
			new RangeCharNode('\u0086', '\u2027'),
			new RangeCharNode('\u202a', '\uffff'));
		List<DefinedCharNode> complement = CharNode.computeComplement(set, all);

		assertThat(complement, contains(
			match('\u0000', '\u0009'),
			match('\u000b', '\u000c'),
			match('\u000e', '9'),
			match(';', '\u0084'),
			match('\u0086', '\u2027'),
			match('\u202a', '\uffff')));
	}

	@SuppressWarnings("unchecked")
	private Matcher<DefinedCharNode> match(char from, char to) {
		return (Matcher<DefinedCharNode>) reflectiveEqualTo(new RangeCharNode(from, to));
	}

	private List<DefinedCharNode> charNodes(DefinedCharNode... nodes) {
		return new ArrayList<>(asList(nodes));
	}
}
