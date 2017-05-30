package net.amygdalum.regexparser;

import static com.almondtools.conmatch.conventions.ReflectiveEqualsMatcher.reflectiveEqualTo;
import static net.amygdalum.regexparser.ConcatNode.inSequence;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import net.amygdalum.regexparser.ConcatNode;
import net.amygdalum.regexparser.EmptyNode;
import net.amygdalum.regexparser.RangeCharNode;
import net.amygdalum.regexparser.RegexNode;
import net.amygdalum.regexparser.RegexNodeVisitor;
import net.amygdalum.regexparser.SingleCharNode;
import net.amygdalum.regexparser.StringNode;


@RunWith(MockitoJUnitRunner.class)
public class ConcatNodeTest {

	@Mock
	private RegexNodeVisitor<String> visitor;

	@Test
	public void testInSequence() throws Exception {
		assertThat(inSequence(new SingleCharNode('a'), new SingleCharNode('b')).toString(), equalTo("ab"));
	}

	@Test
	public void testInSequenceEmpty() throws Exception {
		assertThat(inSequence().toString(), equalTo(""));
	}

	@Test
	public void testInSequenceAlternativesNodesAreInlined() throws Exception {
		assertThat(inSequence(new SingleCharNode('a'), inSequence(new SingleCharNode('b'), new SingleCharNode('c'))).toString(), equalTo("abc"));
		assertThat(inSequence(inSequence(new SingleCharNode('a'), new SingleCharNode('b')), new SingleCharNode('c')).toString(), equalTo("abc"));
	}

	@Test
	public void testAccept() throws Exception {
		when(visitor.visitConcat(any(ConcatNode.class))).thenReturn("success");
		
		assertThat(inSequence(new SingleCharNode('a'), new SingleCharNode('b')).accept(visitor), equalTo("success"));
	}

	@Test
	public void testCloneIsNotOriginal() throws Exception {
		ConcatNode original = inSequence(new SingleCharNode('a'), new SingleCharNode('b'));
		ConcatNode cloned = original.clone();
		
		assertThat(cloned, not(sameInstance(original)));
	}

	@Test
	public void testCloneIsDeep() throws Exception {
		ConcatNode original = inSequence(new SingleCharNode('a'), new SingleCharNode('b'));
		ConcatNode cloned = original.clone();
		
		assertThat(cloned.toString(), equalTo(original.toString()));
		assertThat(cloned.getSubNodes(), not(equalTo(original.getSubNodes())));
		
		original.getSubNodes().clear();
		
		assertThat(cloned.getSubNodes(), hasSize(2));
	}

	@Test
	public void testSimplify() throws Exception {
		ConcatNode notSimplifiable = inSequence(new SingleCharNode('a'), new RangeCharNode('b','c'));
		assertThat(notSimplifiable.simplify(), equalTo((RegexNode) notSimplifiable));
		assertThat(inSequence(new SingleCharNode('a'), new SingleCharNode('b'), new RangeCharNode('c', 'd')).simplify(), reflectiveEqualTo((RegexNode) inSequence(new StringNode("ab"), new RangeCharNode('c', 'd'))));
		assertThat(inSequence(new SingleCharNode('a'), new SingleCharNode('b')).simplify(), reflectiveEqualTo((RegexNode) new StringNode("ab")));
		assertThat(inSequence(new RangeCharNode('c', 'd'), new SingleCharNode('a'), new SingleCharNode('b')).simplify(), reflectiveEqualTo((RegexNode) inSequence(new RangeCharNode('c', 'd'), new StringNode("ab"))));
		assertThat(inSequence(new SingleCharNode('a'), new EmptyNode()).simplify(), reflectiveEqualTo((RegexNode) new StringNode("a")));
		assertThat(inSequence(new SingleCharNode('c')).simplify(), reflectiveEqualTo((RegexNode) new SingleCharNode('c')));
		assertThat(inSequence().simplify(), reflectiveEqualTo((RegexNode) new EmptyNode()));
	}

}
