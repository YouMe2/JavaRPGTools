package util;

import java.text.ParseException;
import java.util.function.Function;

import roll.Rollable;

public abstract class AbsParser<T> {


	private String chars;
	private int offset; // pointer to next to parse

	public AbsParser(String chars) {
		this.chars = chars;
		offset = 0;
	}

	public abstract Rollable parse() throws ParseException;

	public int getOffset() {
		return offset;
	}

	public String getRest() {
		return chars.substring(offset, chars.length());
	}

	public void setRest(String chars) {
		this.chars = chars;
		offset = 0;
	}

	protected int parseInteger() throws ParseException {

		if (isNext('-')) {
			skip(1);
			return -parseNatural();
		}
		if (isNext('+')) {
			skip(1);
			return parseNatural();
		}
		return parseNatural();

	}

	protected int parseNatural() throws ParseException {
		StringBuilder builder = new StringBuilder();
		if (!isNextDigit() && !isNext('+'))
			throw new ParseException("expecting digits or +", getOffset());

		do {
			builder.append(next());
		} while (isNextDigit());
		return Integer.valueOf(builder.toString());
	}

	/**
	 * @return the next text that is enclosed by "" or just the next word broken by a space
	 * @throws ParseException
	 */
	protected String parseText() throws ParseException {
		StringBuilder builder = new StringBuilder();
		if (!isNextText())
			throw new ParseException("expecting text", getOffset());

		if (isNext('\"')) {
			skip(1);
//			while (hasNext() && !isNext('\"')) {
//				builder.append(next());
//			}
			builder.append(nextUntilIsNextAnySeqOf("\""));
			skip(1);
			return builder.toString();
		} else if (isNextLetter()) {	//buchstabe am anfang
			return parseWord();
		} else
			throw new ParseException("expecting text", getOffset());

	}

	protected String parseWord() throws ParseException {
		StringBuilder builder = new StringBuilder();
		if (isNextLetter()) {	//buchstabe am anfang
			do {
				builder.append(next());
			} while (isNextDigit() || isNextLetter()); //dann bis was anderes
			return builder.toString();
		} else
			throw new ParseException("expecting word, starting with a letter (the word may contain digits)", getOffset());
	}
	
	protected String parseLetters() throws ParseException {
		StringBuilder builder = new StringBuilder();

		if (isNextLetter()) {
			do {
				builder.append(next());
			} while (isNextLetter() || isNextWhitespace());
			return builder.toString();
		} else
			throw new ParseException("expecting letters", getOffset());
	}

	protected String nextUntilIsNextAnySeqOf(String... seqs) throws ParseException {
		StringBuilder builder = new StringBuilder();
		while (hasNext() && !isNextAnySeqOf(seqs)) {
			builder.append(next());
		}
		return builder.toString();
	}

	protected boolean isNextDigit() throws ParseException {
		return hasNext() && Character.isDigit(peek());
	}

	protected boolean isNextLetter() {

		return hasNext() && Character.isLetter(chars.charAt(offset));

	}

	protected boolean isNextText() throws ParseException {
		return isNextLetter() || isNext('\"');
	}

	protected boolean isNextNatural() throws ParseException {
		return isNextDigit() || isNext('+');
	}
	
	protected boolean isNextInt() throws ParseException {
		return isNextDigit() || isNextAnyOf('-', '+');
//				&& hasNext(2) && Character.isDigit(chars.charAt(offset + 1));
	}

	protected boolean isNextWhitespace() throws ParseException {
		return isNextAnyOf(' ', '\t');
		// return hasNext() && Character.isWhitespace(chars.charAt(offset));
	}

	protected boolean isNext(char c) throws ParseException {
		return hasNext() && peek() == c;		
	}

	protected boolean isNextSeq(String seq) throws ParseException {
		if(!hasNext(seq.length()))
			return false;
		for (int i = 0; i < seq.length(); i++) {
			if (offset + i >= chars.length() || peek(i) != seq.charAt(i))
				return false;
		}
		return true;
	}

	protected boolean isNextAnyOf(char... characters) throws ParseException {
		if (!hasNext())
			return false;
		for (char character : characters)
			if (isNext(character))
				return true;
		return false;
	}

	protected boolean isNextAnySeqOf(String... seqs) throws ParseException {
		for (String seq : seqs)
			if (isNextSeq(seq))
				return true;
		return false;
	}

	protected char next() throws ParseException {
		if (!hasNext())
			throw new ParseException("unexpected end", offset);
		return chars.charAt(offset++);
	}

	protected char peek() throws ParseException {
		if (!hasNext())
			throw new ParseException("unexpected end", offset);
		return chars.charAt(offset);
	}

	protected char peek(int n) throws ParseException {
		if (!hasNext(n))
			throw new ParseException("unexpected end", offset);
		return chars.charAt(offset + n);
	}	
	
	protected void skip(int n) throws ParseException {
		if (n < 1)
			return;
		if (!hasNext())
			throw new ParseException("unexpected end", offset);
		offset++;
//		System.out.println(getOffset());
		skip(n - 1);
	}

	protected void skipNextWhitespaces() throws ParseException {
//		skipWhileNext(c -> c.isWhitespace(c));		
		while (isNextWhitespace()) {
			skip(1);		
		}
	}

	protected void skipNextSpaces() throws ParseException {
		while (isNextWhitespace() || isNextSeq(System.lineSeparator())) {
			try {
				if (isNextWhitespace())
					skip(1);
				else
					skip(System.lineSeparator().length());
			} catch (ParseException e) {
				// this should never happen!
				e.printStackTrace();
			}
		}
	}

	protected void skipWhileNext(Function<Character, Boolean> condition) {
		try {
			
			while (hasNext() && condition.apply(peek())) {
				skip(1);
			}
			
			assert (!hasNext()) || (!condition.apply(peek()));
			
		} catch (ParseException e) {
			// this should never happen!
			e.printStackTrace();
		}
		
	}
	
	protected void skipUntilNextIsAnyOfSeq(String... seqs) {
		try {
			while (!isNextAnySeqOf(seqs)) {
				skip(1);
			}
		} catch (ParseException e) {
			// this should never happen!
			e.printStackTrace();
		}	
	}

	protected void skipUntilNextIsSeq(String seq) {
		skipUntilNextIsSeq(seq);
	}
	
	protected void skipAnyOf(char... cs) {
		skipWhileNext(new Function<Character, Boolean>() {			
			@Override
			public Boolean apply(Character t) {				
				for (char c : cs) {
					if ( c == t)
						return true;
				}
				return false;
			}
		});
	}
	
	protected void skipText() throws ParseException {
		if (!isNextText())
			return;
		parseText();
	}

	protected boolean hasNext() {
		return hasNext(1);
	}

	protected boolean hasNext(int n) {
		return offset + n - 1 < chars.length();
	}
}
