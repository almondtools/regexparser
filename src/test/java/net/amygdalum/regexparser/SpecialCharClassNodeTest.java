package net.amygdalum.regexparser;

import static net.amygdalum.extensions.hamcrest.objects.ReflectiveEqualsMatcher.reflectiveEqualTo;
import static net.amygdalum.util.text.CharUtils.after;
import static net.amygdalum.util.text.CharUtils.before;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import net.amygdalum.regexparser.DefinedCharNode;
import net.amygdalum.regexparser.RangeCharNode;
import net.amygdalum.regexparser.RegexNodeVisitor;
import net.amygdalum.regexparser.SingleCharNode;
import net.amygdalum.regexparser.SpecialCharClassNode;

@RunWith(MockitoJUnitRunner.class)
public class SpecialCharClassNodeTest {

	@Mock
	private RegexNodeVisitor<String> visitor;

	@Test
	public void testToCharNodes() throws Exception {
		assertThat(new SpecialCharClassNode('x', new SingleCharNode('d')).toCharNodes(), contains(reflectiveEqualTo((DefinedCharNode) new SingleCharNode('d'))));
	}

	@Test
	public void testGetSymbol() throws Exception {
		assertThat(new SpecialCharClassNode('x', new SingleCharNode('d')).getSymbol(), equalTo('x'));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testInvert() throws Exception {
		assertThat(new SpecialCharClassNode('x', new SingleCharNode('e'), new SingleCharNode('f')).invert(rangeCharNodes((char) 0, (char) 255)).toCharNodes(), contains(
			reflectiveEqualTo((DefinedCharNode) new RangeCharNode((char) 0, before('e'))), 
			reflectiveEqualTo((DefinedCharNode) new RangeCharNode(after('f'), (char) 255))));
	}

	@Test
	public void testAccept() throws Exception {
		when(visitor.visitSpecialCharClass(any(SpecialCharClassNode.class))).thenReturn("success");

		assertThat(new SpecialCharClassNode('x', new SingleCharNode('d')).accept(visitor), equalTo("success"));
	}

	@Test
	public void testCloneIsNotOriginal() throws Exception {
		SpecialCharClassNode original = new SpecialCharClassNode('x', new SingleCharNode('d'));
		SpecialCharClassNode cloned = original.clone();

		assertThat(cloned, not(sameInstance(original)));
	}

	@Test
	public void testCloneIsSimilar() throws Exception {
		SpecialCharClassNode original = new SpecialCharClassNode('x', new SingleCharNode('d'));
		SpecialCharClassNode cloned = original.clone();

		assertThat(cloned.toString(), equalTo(original.toString()));
	}

	@Test
	public void testToString() throws Exception {
		assertThat(new SpecialCharClassNode('x', new SingleCharNode('d')).toString(), equalTo("\\x"));
	}

	private List<DefinedCharNode> rangeCharNodes(char from, char to) {
		return new RangeCharNode(from, to).toCharNodes();
	}

}
