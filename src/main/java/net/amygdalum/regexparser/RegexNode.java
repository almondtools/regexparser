package net.amygdalum.regexparser;

public interface RegexNode extends Cloneable {

	<T> T accept(RegexNodeVisitor<T> visitor);

	RegexNode clone();
	
}
