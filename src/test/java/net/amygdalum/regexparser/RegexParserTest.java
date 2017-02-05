package net.amygdalum.regexparser;

import static com.almondtools.conmatch.conventions.ReflectiveEqualsMatcher.reflectiveEqualTo;
import static java.lang.Character.MAX_VALUE;
import static java.lang.Character.MIN_VALUE;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

public class RegexParserTest {

	@Test
	public void testNothing() throws Exception {
		RegexParser parser = new RegexParser(null);
		
		assertThat(parser.parse(), nullValue());
	}

	@Test
	public void testEmpty() throws Exception {
		RegexParser parser = new RegexParser("");
		
		assertThat(parser.parse(), reflectiveEqualTo((RegexNode) new EmptyNode()));
	}

	@Test
	public void testSingleChar() throws Exception {
		RegexParser parser = new RegexParser("A");
		RegexNode node = parser.parse();

		assertThat(node, reflectiveEqualTo((RegexNode) new SingleCharNode('A')));
	}

	@Test
	public void testEscapedOrdinaryChar() throws Exception {
		RegexParser parser = new RegexParser("\\A");
		RegexNode node = parser.parse();

		assertThat(node, reflectiveEqualTo((RegexNode) new SingleCharNode('A')));
	}

	@Test
	public void testEscapedControlChar() throws Exception {
		RegexParser parser = new RegexParser("\\n\\r\\t\\f\\\\");
		RegexNode node = parser.parse();

		assertThat(node, reflectiveEqualTo((RegexNode) new StringNode("\n\r\t\f\\")));
	}

	@Test
	public void testString() throws Exception {
		RegexParser parser = new RegexParser("Ab");
		RegexNode node = parser.parse();

		assertThat(node, reflectiveEqualTo((RegexNode) new StringNode("Ab")));
	}

	@Test
	public void testSpecialCharClassWhitespace() throws Exception {
		RegexParser parser = new RegexParser("\\s");
		RegexNode node = parser.parse();

		assertThat(node, reflectiveEqualTo((RegexNode) new SpecialCharClassNode('s')).excluding("charNodes"));
		assertThat(((SpecialCharClassNode) node).toCharNodes(), hasItem(reflectiveEqualTo( new SingleCharNode(' '))));
		assertThat(((SpecialCharClassNode) node).toCharNodes(), hasItem(reflectiveEqualTo( new SingleCharNode('\r'))));
		assertThat(((SpecialCharClassNode) node).toCharNodes(), hasItem(reflectiveEqualTo( new SingleCharNode('\n'))));
		assertThat(((SpecialCharClassNode) node).toCharNodes(), hasItem(reflectiveEqualTo( new SingleCharNode('\t'))));
	}

	@Test
	public void testSpecialCharClassNonWhitespace() throws Exception {
		RegexParser parser = new RegexParser("\\S");
		RegexNode node = parser.parse();

		assertThat(node, reflectiveEqualTo((RegexNode) new SpecialCharClassNode('S')).excluding("charNodes"));
		assertThat(((SpecialCharClassNode) node).toCharNodes(), not(hasItem(reflectiveEqualTo( new SingleCharNode(' ')))));
		assertThat(((SpecialCharClassNode) node).toCharNodes(), not(hasItem(reflectiveEqualTo( new SingleCharNode('\r')))));
		assertThat(((SpecialCharClassNode) node).toCharNodes(), not(hasItem(reflectiveEqualTo( new SingleCharNode('\n')))));
		assertThat(((SpecialCharClassNode) node).toCharNodes(), not(hasItem(reflectiveEqualTo( new SingleCharNode('\t')))));
	}

	@Test
	public void testSpecialCharClassAlphanumeric() throws Exception {
		RegexParser parser = new RegexParser("\\w");
		RegexNode node = parser.parse();

		assertThat(node, reflectiveEqualTo((RegexNode) new SpecialCharClassNode('w')).excluding("charNodes"));
		assertThat(((SpecialCharClassNode) node).toCharNodes(), hasItem(reflectiveEqualTo( new RangeCharNode('a','z'))));
		assertThat(((SpecialCharClassNode) node).toCharNodes(), hasItem(reflectiveEqualTo( new RangeCharNode('A','Z'))));
		assertThat(((SpecialCharClassNode) node).toCharNodes(), hasItem(reflectiveEqualTo( new RangeCharNode('0','9'))));
	}

	@Test
	public void testSpecialCharClassNonAlphanumeric() throws Exception {
		RegexParser parser = new RegexParser("\\W");
		RegexNode node = parser.parse();

		assertThat(node, reflectiveEqualTo((RegexNode) new SpecialCharClassNode('W')).excluding("charNodes"));
		assertThat(((SpecialCharClassNode) node).invert(anyCharNodes()).toCharNodes(), hasItem(reflectiveEqualTo( new RangeCharNode('a','z'))));
		assertThat(((SpecialCharClassNode) node).invert(anyCharNodes()).toCharNodes(), hasItem(reflectiveEqualTo( new RangeCharNode('A','Z'))));
		assertThat(((SpecialCharClassNode) node).invert(anyCharNodes()).toCharNodes(), hasItem(reflectiveEqualTo( new RangeCharNode('0','9'))));
	}

	@Test
	public void testSpecialCharClassDigits() throws Exception {
		RegexParser parser = new RegexParser("\\d");
		RegexNode node = parser.parse();

		assertThat(node, reflectiveEqualTo((RegexNode) new SpecialCharClassNode('d')).excluding("charNodes"));
		assertThat(((SpecialCharClassNode) node).toCharNodes(), hasItem(reflectiveEqualTo( new RangeCharNode('0','9'))));
	}

	@Test
	public void testSpecialCharClassNonDigits() throws Exception {
		RegexParser parser = new RegexParser("\\D");
		RegexNode node = parser.parse();

		assertThat(node, reflectiveEqualTo((RegexNode) new SpecialCharClassNode('D')).excluding("charNodes"));
		assertThat(((SpecialCharClassNode) node).invert(anyCharNodes()).toCharNodes(), hasItem(reflectiveEqualTo( new RangeCharNode('0','9'))));
	}

	@Test
	public void testCharClassForChar() throws Exception {
		RegexParser parser = new RegexParser("[d]");
		RegexNode node = parser.parse();

		assertThat(node, reflectiveEqualTo((RegexNode) new CharClassNode(new SingleCharNode('d'))));
	}

	@Test
	public void testCharClassForNestedSpecialCharClass() throws Exception {
		RegexParser parser = new RegexParser("[\\d]");
		RegexNode node = parser.parse();

		assertThat(node, reflectiveEqualTo((RegexNode) new CharClassNode(new RangeCharNode('0','9'))));
	}

	@Test
	public void testCharClassForEscapedChar() throws Exception {
		RegexParser parser = new RegexParser("[\\o]");
		RegexNode node = parser.parse();

		assertThat(node, reflectiveEqualTo((RegexNode) new CharClassNode(new SingleCharNode('o'))));
	}

	@Test
	public void testCharClassForControlChar() throws Exception {
		RegexParser parser = new RegexParser("[\\t]");
		RegexNode node = parser.parse();

		assertThat(node, reflectiveEqualTo((RegexNode) new CharClassNode(new SingleCharNode('\t'))));
	}

	@Test
	public void testCharClassForOneContinuousRange() throws Exception {
		RegexParser parser = new RegexParser("[A-C]");
		RegexNode node = parser.parse();

		assertThat(node, reflectiveEqualTo((RegexNode) new CharClassNode(new RangeCharNode('A', 'C'))));
	}

	@Test
	public void testCharClassForSomeSingleChars() throws Exception {
		RegexParser parser = new RegexParser("[Ac]");
		RegexNode node = parser.parse();

		assertThat(node, reflectiveEqualTo((RegexNode) new CharClassNode(new SingleCharNode('A'), new SingleCharNode('c'))));
	}

	@Test
	public void testCharClassForInteruptedRange() throws Exception {
		RegexParser parser = new RegexParser("[A-Ca-c]");
		RegexNode node = parser.parse();

		assertThat(node, reflectiveEqualTo((RegexNode) new CharClassNode(new RangeCharNode('A', 'C'), new RangeCharNode('a', 'c'))));
	}

	@Test
	public void testCharClassForMixedContent() throws Exception {
		RegexParser parser = new RegexParser("[AC-D]");
		RegexNode node = parser.parse();

		assertThat(node, reflectiveEqualTo((RegexNode) new CharClassNode(new SingleCharNode('A'), new RangeCharNode('C', 'D'))));
	}

	@Test
	public void testCompCharClassForChar() throws Exception {
		RegexParser parser = new RegexParser("[^e]");
		RegexNode node = parser.parse();

		assertThat(node, reflectiveEqualTo((RegexNode) new CharClassNode(new SingleCharNode('e')).invert(AnyCharNode.dotDefault(MIN_VALUE, MAX_VALUE).toCharNodes())));
	}

	@Test
	public void testCompCharClassForCharDotAll() throws Exception {
		RegexParser parser = new RegexParser("[^e]", RegexParserOption.DOT_ALL);
		RegexNode node = parser.parse();

		assertThat(node, reflectiveEqualTo((RegexNode) new CharClassNode(new SingleCharNode('e')).invert(anyCharNodes())));
	}

	@Test
	public void testAnyCharDotAll() throws Exception {
		RegexParser parser = new RegexParser(".", RegexParserOption.DOT_ALL);
		RegexNode node = parser.parse();

		assertThat(node, reflectiveEqualTo((RegexNode) AnyCharNode.dotAll(Character.MIN_VALUE, Character.MAX_VALUE)));
	}

	@Test
	public void testAnyChar() throws Exception {
		RegexParser parser = new RegexParser(".");
		RegexNode node = parser.parse();

		assertThat(node, reflectiveEqualTo((RegexNode) AnyCharNode.dotDefault(Character.MIN_VALUE, Character.MAX_VALUE)));
	}

	@Test
	public void testAlternatives() throws Exception {
		RegexParser parser = new RegexParser("a|B");
		RegexNode node = parser.parse();

		assertThat(node, reflectiveEqualTo((RegexNode) AlternativesNode.anyOf(new SingleCharNode('a'), new SingleCharNode('B'))));
	}

	@Test
	public void testGroup() throws Exception {
		RegexParser parser = new RegexParser("(B)");
		RegexNode node = parser.parse();

		assertThat(node, reflectiveEqualTo((RegexNode) new GroupNode(new SingleCharNode('B'))));
	}

	@Test
	public void testConcat() throws Exception {
		RegexParser parser = new RegexParser("a(B)");
		RegexNode node = parser.parse();

		assertThat(node, reflectiveEqualTo((RegexNode) ConcatNode.inSequence(new SingleCharNode('a'), new GroupNode(new SingleCharNode('B')))));
	}

	@Test
	public void testOptional() throws Exception {
		RegexParser parser = new RegexParser("c?");
		RegexNode node = parser.parse();

		assertThat(node, reflectiveEqualTo((RegexNode) OptionalNode.optional(new SingleCharNode('c'))));
	}

	@Test
	public void testStarLoop() throws Exception {
		RegexParser parser = new RegexParser("d*");
		RegexNode node = parser.parse();

		assertThat(node, reflectiveEqualTo((RegexNode) UnboundedLoopNode.star(new SingleCharNode('d'))));
	}

	@Test
	public void testPlusLoop() throws Exception {
		RegexParser parser = new RegexParser("e+");
		RegexNode node = parser.parse();

		assertThat(node, reflectiveEqualTo((RegexNode) UnboundedLoopNode.plus(new SingleCharNode('e'))));
	}

	@Test
	public void testUnboundedLoop() throws Exception {
		RegexParser parser = new RegexParser("f{3,}");
		RegexNode node = parser.parse();

		assertThat(node, reflectiveEqualTo((RegexNode) UnboundedLoopNode.unbounded(new SingleCharNode('f'),3)));
	}

	@Test
	public void testFixedLoop() throws Exception {
		RegexParser parser = new RegexParser("g{4}");
		RegexNode node = parser.parse();

		assertThat(node, reflectiveEqualTo((RegexNode) BoundedLoopNode.bounded(new SingleCharNode('g'),4,4)));
	}

	@Test
	public void testBoundedLoop() throws Exception {
		RegexParser parser = new RegexParser("h{2,6}");
		RegexNode node = parser.parse();

		assertThat(node, reflectiveEqualTo((RegexNode) BoundedLoopNode.bounded(new SingleCharNode('h'),2,6)));
	}
	
	private List<DefinedCharNode> anyCharNodes() {
		return new RangeCharNode(MIN_VALUE, MAX_VALUE).toCharNodes();
	}
}
