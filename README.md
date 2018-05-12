RegexParser
===========
[![Build Status](https://api.travis-ci.org/almondtools/regexparser.svg)](https://travis-ci.org/almondtools/regexparser)

RegexParser is a handwritten parser for (deterministic) regular expressions. Deterministic means, that the regular expression language can be compiled to a deterministic finite automaton (the default java regular expressions are more powerful).

Not supported features are:
* backreferences
* lookaheads, lookbehinds
* variations of the Kleene star

Syntax
======
The syntax of the recognized regular expressions could be characterized by following table:

| Syntax                  | Matches                                                              |
| ----------------------- |----------------------------------------------------------------------|
| Single Characters       |                                                                      |
| x                       | The character x, unless there exist special rules for this character |
| \x                      | The character x, if there exist special rules for this character     |
| .                       | any character (newlines only in DOTALL-mode)                         |
| \\                      | backslash character                                                  |
| \n                      | newline character                                                    |
| \t                      | tab character                                                        |
| \r                      | carriage return character                                            |
| \f                      | form feed character                                                  |
| \a                      | alert/bell character                                                 |
| \e                      | escape character                                                     |
| *\uhhhh*                | *unicode character, not yet supported*                               |
| Character classes       |                                                                      |
| [...]                   | any of the contained characters                                      |
| [^...]                  | none of the contained characters                                     |
| [a-z]                   | char range (all chars from a to z)                                   |
| [a-zA-Z]                | char range, union of multiple ranges                                 |
| \s                      | white space                                                          |
| \S                      | non white space                                                      |
| \w                      | word characters                                                      |
| \W                      | non word charachters                                                 |
| \d                      | digits                                                               |
| \D                      | non digits                                                           |
| *\p{name}*              | *posix character class, not yet supported*                           |
| Sequences, alternatives |                                                                      |
| xy                      | match x followed by y                                                |
| x|y                     | match x or y                                                         |
| (x)                     | match inner expression x (grouping is not supported)                 |
| Repetitions             |                                                                      |
| x?                      | match x or nothing                                                   |
| x*                      | match a sequence of x's or nothing                                   |
| x+                      | match a sequence of x's (minimum one)                                |
| x{2}                    | match a sequence of 2 x's                                            |
| x{2,4}                  | match a 2 to 4 x's                                                   |
| x{,4}                   | match a up to 4 x's                                                  |
| x{2,}                   | match a minimum of 2 x's                                             |
|                         |                                                                      |
| *Advanced Groups*       | *not supported*                                                      |
| *Lookaheads*            | *not supported*                                                      |
| *Lookbehinds*           | *not supported*                                                      |
| *References*            | *not supported*                                                      |
| *Anchors*               | *not supported*                                                      |
| *Flags*                 | *not supported*                                                      |

Maven Dependency
================

```xml
<dependency>
    <groupId>net.amygdalum</groupId>
    <artifactId>regexparser</artifactId>
    <version>0.1.0</version>
</dependency>
```