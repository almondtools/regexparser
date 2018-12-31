package net.amygdalum.regexparser;

import static net.amygdalum.extensions.hamcrest.objects.ReflectiveEqualsMatcher.reflectiveEqualTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import net.amygdalum.regexparser.GroupNode;
import net.amygdalum.regexparser.RegexNode;
import net.amygdalum.regexparser.RegexNodeVisitor;
import net.amygdalum.regexparser.SingleCharNode;

@RunWith(MockitoJUnitRunner.class)
public class GroupNodeTest {

	@Mock
	private RegexNodeVisitor<String> visitor;

	@Test
	public void testGetSubNode() throws Exception {
		assertThat(new GroupNode(new SingleCharNode('a')).getSubNode(), reflectiveEqualTo((RegexNode) new SingleCharNode('a')));
	}

	@Test
	public void testAccept() throws Exception {
		when(visitor.visitGroup(any(GroupNode.class))).thenReturn("success");
		
		assertThat(new GroupNode(new SingleCharNode('a')).accept(visitor), equalTo("success"));
	}

	@Test
	public void testCloneIsNotOriginal() throws Exception {
		GroupNode original = new GroupNode(new SingleCharNode('a'));
		GroupNode cloned = original.clone();
		
		assertThat(cloned, not(sameInstance(original)));
	}

	@Test
	public void testCloneIsSimilar() throws Exception {
		GroupNode original = new GroupNode(new SingleCharNode('a'));
		GroupNode cloned = original.clone();
		
		assertThat(cloned.toString(), equalTo(original.toString()));
	}

	@Test
	public void testToString() throws Exception {
		assertThat(new GroupNode(new SingleCharNode('a')).toString(), equalTo("(a)"));
	}

}
