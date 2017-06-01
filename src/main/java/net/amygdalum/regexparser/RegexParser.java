package net.amygdalum.regexparser;

import static java.lang.Character.MAX_VALUE;
import static java.lang.Character.MIN_VALUE;
import static net.amygdalum.regexparser.RegexParserOption.DOT_ALL;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RegexParser {

	private static final char OR = '|';
	private static final char AND = '&';
	private static final char OBRK = '[';
	private static final char NOT = '^';
	private static final char DASH = '-';
	private static final char CBRK = ']';
	private static final char OBRC = '{';
	private static final char COMMA = ',';
	private static final char CBRC = '}';
	private static final char OPT = '?';
	private static final char STAR = '*';
	private static final char PLUS = '+';
	private static final char OPAR = '(';
	private static final char CPAR = ')';
	private static final char DOT = '.';
	private static final char ESCAPE = '\\';

	private static final char[] DIGIT = "0123456789".toCharArray();
	private static final char[] BREAK_CONCAT = new char[] { OR, AND, CPAR };
	private static final char[] OPEN_LOOP = new char[] { OPT, STAR, PLUS, OBRC };
	private static final char[] CLOSE_CHAR_CLASS = new char[] { CBRK };
	private static final char[] NOT_CAPTURING_GROUP = new char[] { '?', ':' };

	private static final char DEFAULT_MIN_CHAR = MIN_VALUE;
	private static final char DEFAULT_MAX_CHAR = MAX_VALUE;

	private Map<Character, CharNode> characterClasses;

	private String pattern;
	private int pos;
	private int groupNumber;

	private char min;
	private char max;
	private RegexParserOption[] options;

	public RegexParser(String pattern, RegexParserOption... options) {
		this(pattern, DEFAULT_MIN_CHAR, DEFAULT_MAX_CHAR, options);
	}

	public RegexParser(String pattern, char min, char max, RegexParserOption... options) {
		this.pattern = pattern;
		this.min = min;
		this.max = max;
		this.options = options;
		this.pos = 0;
		this.groupNumber = 0;
		this.characterClasses = new CharClassBuilder(min, max)
			.add('t', new SingleCharNode('\t'))
			.add('n', new SingleCharNode('\n'))
			.add('r', new SingleCharNode('\r'))
			.add('f', new SingleCharNode('\f'))
			.add('\\', new SingleCharNode('\\'))
			.add(createWhiteSpaceEscapes())
			.add(createAlphaNumericEscapes())
			.add(createDigitEscapes())
			.build();
	}

	private static SpecialCharClassNode createWhiteSpaceEscapes() {
		return new SpecialCharClassNode('s',
			new SingleCharNode(' '),
			new SingleCharNode('\t'),
			new SingleCharNode('\r'),
			new SingleCharNode('\n'));
	}

	private static SpecialCharClassNode createAlphaNumericEscapes() {
		return new SpecialCharClassNode('w',
			new RangeCharNode('0', '9'),
			new RangeCharNode('a', 'z'),
			new RangeCharNode('A', 'Z'));
	}

	private static SpecialCharClassNode createDigitEscapes() {
		return new SpecialCharClassNode('d',
			new RangeCharNode('0', '9'));
	}

	public RegexNode parse() {
		if (pattern == null) {
			return null;
		} else if (pattern.isEmpty()) {
			return new EmptyNode();
		} else {
			return parseAlternatives();
		}
	}

	private char next() {
		char ch = pattern.charAt(pos);
		pos++;
		return ch;
	}

	private boolean done() {
		return pos >= pattern.length();
	}

	private boolean match(char c) {
		if (done()) {
			return false;
		} else if (pattern.charAt(pos) == c) {
			pos++;
			return true;
		} else {
			return false;
		}
	}

	private boolean match(char[] chars) {
		int finalPos = pos + chars.length; 
		if (finalPos >= pattern.length()) {
			return false;
		} else {			
			for (int i = 0; i < chars.length; i++) {
				char current = pattern.charAt(pos + i);
				if(current != chars[i])return false;
			}

			pos = finalPos;
			return true;
		}
	}

	private boolean lookahead(char[] chars) {
		if (done()) {
			return false;
		} else {
			char current = pattern.charAt(pos);
			for (char c : chars) {
				if (c == current) {
					return true;
				}
			}
			return false;
		}
	}

	private boolean lookaheadIsDigit() {
		return lookahead(DIGIT);
	}

	private boolean lookaheadIsBreakConcat() {
		return lookahead(BREAK_CONCAT)
			|| done();
	}

	private boolean lookaheadIsOpenLoop() {
		return lookahead(OPEN_LOOP);
	}

	private boolean lookaheadIsCloseCharClass() {
		return lookahead(CLOSE_CHAR_CLASS);
	}

	public RegexNode parseAlternatives() {
		RegexNode node = parseConcat();
		while (match(OR)) {
			node = AlternativesNode.anyOf(node, parseConcat());
		}
		return node;
	}

	private RegexNode parseConcat() {
		List<RegexNode> nodes = new LinkedList<>();
		nodes.add(parseLoop());
		while (!lookaheadIsBreakConcat()) {
			nodes.add(parseLoop());
		}
		return ConcatNode.inSequence(nodes.toArray(new RegexNode[0])).simplify();
	}

	private RegexNode parseLoop() {
		RegexNode node = parseCharOrCharClass();
		while (lookaheadIsOpenLoop()) {
			if (match(OPT)) {
				node = OptionalNode.optional(node);
			} else if (match(STAR)) {
				node = UnboundedLoopNode.star(node);
			} else if (match(PLUS)) {
				node = UnboundedLoopNode.plus(node);
			} else if (match(OBRC)) {
				int from = parseInt(0);
				int to = from;
				if (match(COMMA)) {
					to = parseInt(Integer.MIN_VALUE);
				}
				if (!match(CBRC)) {
					throw new RegexCompileException(pattern, pos, "}");
				}
				if (to < 0) {
					node = UnboundedLoopNode.unbounded(node, from);
				} else {
					node = BoundedLoopNode.bounded(node, from, to);
				}
			}
		}
		return node;
	}

	private RegexNode parseCharOrCharClass() {
		if (match(OBRK)) {
			boolean complement = match(NOT);
			List<CharNode> subNodes = new LinkedList<CharNode>();
			subNodes.add(parseCharOrRange());
			while (!lookaheadIsCloseCharClass()) {
				subNodes.add(parseCharOrRange());
			}
			if (!match(CBRK)) {
				throw new RegexCompileException(pattern, pos, "]");
			}
			AbstractCharClassNode charClassNode = new CharClassNode(toCharNodes(subNodes));
			if (complement) {
				charClassNode = charClassNode.invert(createDot().toCharNodes());
			}
			return charClassNode;
		} else if (match(ESCAPE)) {
			return parseEscapedChar();
		} else {
			return parseLeaf();
		}
	}

	private CharNode parseCharOrRange() {
		if (match(ESCAPE)) {
			return parseEscapedChar();
		} else {
			char ch = parseChar();
			if (match(DASH)) {
				if (lookaheadIsCloseCharClass()) {
					return new CharClassNode(new SingleCharNode(ch), new SingleCharNode(DASH));
				} else {
					char from = ch;
					char to = parseChar();
					return new RangeCharNode(from, to).simplify();
				}
			} else {
				return new SingleCharNode(ch);
			}
		}
	}

	private CharNode parseEscapedChar() {
		char ch = next();
		if (characterClasses.containsKey(ch)) {
			return characterClasses.get(ch);
		} else {
			return new SingleCharNode(ch);
		}
	}

	private RegexNode parseLeaf() {
		if (match(DOT)) {
			return createDot();
		} else if (match(OPAR)) {
			boolean notCapturing = match(NOT_CAPTURING_GROUP);
			if(!notCapturing)this.groupNumber++;
			int gn = this.groupNumber;

			RegexNode node = parseAlternatives();
			if (!match(CPAR)) {
				throw new RegexCompileException(pattern, pos, ")");
			}
			if(notCapturing){
				return new GroupNode(node);
			}else{
				return new GroupNode(node, gn);				
			}
		} else {
			return new SingleCharNode(parseChar());
		}
	}

	private AnyCharNode createDot() {
		if (DOT_ALL.in(options)) {
			return AnyCharNode.dotAll(min, max);
		} else {
			return AnyCharNode.dotDefault(min, max);
		}
	}

	private int parseInt(int defaultValue) {
		int start = pos;
		while (lookaheadIsDigit()) {
			next();
		}
		if (start != pos) {
			return Integer.parseInt(pattern.substring(start, pos));
		} else {
			return defaultValue;
		}
	}

	private char parseChar() {
		if (done()) {
			throw new RegexCompileException(pattern, pos, ".");
		} else {
			return next();
		}
	}

	public static List<DefinedCharNode> toCharNodes(List<CharNode> nodes) {
		List<DefinedCharNode> charNodes = new ArrayList<DefinedCharNode>();
		for (CharNode node : nodes) {
			charNodes.addAll(node.toCharNodes());
		}
		return charNodes;
	}

}
