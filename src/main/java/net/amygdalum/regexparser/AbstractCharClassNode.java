package net.amygdalum.regexparser;


public abstract class AbstractCharClassNode extends CharNode {

	public abstract AbstractCharClassNode invert(char min, char max);

}
