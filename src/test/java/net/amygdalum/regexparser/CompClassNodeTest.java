package net.amygdalum.regexparser;

import static net.amygdalum.extensions.hamcrest.objects.ReflectiveEqualsMatcher.reflectiveEqualTo;
import static java.util.Arrays.asList;
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

import net.amygdalum.regexparser.CompClassNode;
import net.amygdalum.regexparser.DefinedCharNode;
import net.amygdalum.regexparser.RangeCharNode;
import net.amygdalum.regexparser.RegexNodeVisitor;

@RunWith(MockitoJUnitRunner.class)
public class CompClassNodeTest {

	@Mock
	private RegexNodeVisitor<String> visitor;

	@Test
	public void testToCharNodes() throws Exception {
		assertThat(new CompClassNode(
			asList((DefinedCharNode) new RangeCharNode((char) 0, 'd')),
			asList((DefinedCharNode) new RangeCharNode('e', (char) 255)))
				.toCharNodes(),
			contains(reflectiveEqualTo((DefinedCharNode) new RangeCharNode('e', (char) 255))));
	}

	@Test
	public void testInvert() throws Exception {
		assertThat(new CompClassNode(
			asList((DefinedCharNode) new RangeCharNode((char) 0, 'd')),
			asList((DefinedCharNode) new RangeCharNode('e', (char) 255)))
				.invert(rangeCharNodes((char) 0, (char) 255)).toCharNodes(),
			contains(reflectiveEqualTo((DefinedCharNode) new RangeCharNode((char) 0, 'd'))));
	}

	@Test
	public void testAccept() throws Exception {
		when(visitor.visitCompClass(any(CompClassNode.class))).thenReturn("success");

		assertThat(new CompClassNode(
			asList((DefinedCharNode) new RangeCharNode((char) 0, 'd')),
			asList((DefinedCharNode) new RangeCharNode('e', (char) 255)))
				.accept(visitor),
			equalTo("success"));
	}

	@Test
	public void testCloneIsNotOriginal() throws Exception {
		CompClassNode original = new CompClassNode(
			asList((DefinedCharNode) new RangeCharNode((char) 0, 'd')),
			asList((DefinedCharNode) new RangeCharNode('e', (char) 255)));
		CompClassNode cloned = original.clone();

		assertThat(cloned, not(sameInstance(original)));
	}

	@Test
	public void testCloneIsSimilar() throws Exception {
		CompClassNode original = new CompClassNode(
			asList((DefinedCharNode) new RangeCharNode((char) 0, 'd')),
			asList((DefinedCharNode) new RangeCharNode('e', (char) 255)));
		CompClassNode cloned = original.clone();

		assertThat(cloned.toString(), equalTo(original.toString()));
	}

	@Test
	public void testToString() throws Exception {
		assertThat(new CompClassNode(
			asList((DefinedCharNode) new RangeCharNode((char) 0, 'd')),
			asList((DefinedCharNode) new RangeCharNode('e', (char) 255))).toString(), equalTo("[^\\u0000-d]"));
	}

	private List<DefinedCharNode> rangeCharNodes(char from, char to) {
		return new RangeCharNode(from, to).toCharNodes();
	}

}
