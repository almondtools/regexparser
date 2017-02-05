package net.amygdalum.regexparser;

import java.util.List;

public abstract class AbstractCharClassNode extends CharNode {

	public abstract AbstractCharClassNode invert(List<DefinedCharNode> allnodes);

}
