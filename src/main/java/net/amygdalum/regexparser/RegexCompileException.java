package net.amygdalum.regexparser;

public class RegexCompileException extends RuntimeException {

	public RegexCompileException(String pattern, int pos, String missing) {
		super("Regular expression " + pattern + " fails to compile at position " + pos + ", missing: " + missing);
	}

}
