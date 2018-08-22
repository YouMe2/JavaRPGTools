package util;

import java.text.ParseException;

import roll.Rollable;

public abstract class AbsParser<T> {

	//TODO skipWhileIsNExt(Fun)
	
	private String chars;
	private int offset; // pointer to next to parse
	
	public AbsParser(String chars) {
		this.chars = chars;
		offset = 0;
	}
	
	public abstract Rollable parse() throws ParseException ;

	
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
		if (!isNextDigit())
			throw new ParseException("expecting digits", getOffset());
		
		do {
			builder.append(next());
		} while (isNextDigit());
		return Integer.valueOf(builder.toString());
	}
	
 	protected String parseText() throws ParseException {
		StringBuilder builder = new StringBuilder();
		if (!isNextText())
			throw new ParseException("expecting text", getOffset());
		
		
		if (isNext('\"')) {
			skip(1);
			while (!isNext('\"') ) {
				builder.append(next());
			}
			skip(1);
			return builder.toString();
		} else if (isNextLetter()) {
			do {
				builder.append(next());
			} while (isNextLetter() && !isNextWhitespace());
			return builder.toString();
		} else
			throw new ParseException("expecting text", getOffset());
	
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

	protected boolean isNextDigit() {
		return hasNext() && Character.isDigit(chars.charAt(offset));
	}
	
	protected boolean isNextLetter() {
		
		return hasNext() && Character.isLetter(chars.charAt(offset));
		
	}
	
	protected boolean isNextText() {
		return isNextLetter() || isNext('\"');
	}
	
	protected boolean isNextInt() {
		return isNextDigit() || (isNextAnyOf('-', '+') && hasNext(2) && Character.isDigit(chars.charAt(offset+1)));
	}
	
	protected boolean isNextWhitespace() {
		return isNextAnyOf(' ', '\t');
		//return hasNext() && Character.isWhitespace(chars.charAt(offset));
	}

 	protected boolean isNext(char c) {
		return hasNext() && chars.charAt(offset) == c;
	}
	
	protected boolean isNextSeq(String seq) {
		for (int i = 0; i < seq.length(); i++) {
			if (offset+i >= chars.length() || chars.charAt(offset + i) != seq.charAt(i))
				return false;
		}
		return true;
	}
	
	protected boolean isNextAnyOf(char... characters) {
		if (!hasNext())
			return false;
		for (char character : characters)
			if (isNext(character))
				return true;
		return false;
	}
	
	protected boolean isNextAnySeqOf(String... seqs) {
		if (!hasNext())
			return false;
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

	protected void skip(int n) throws ParseException {
		if (n < 1)
			return;
		if (!hasNext())
			throw new ParseException("unexpected end", offset);
		offset++;
		skip(n - 1);
	}
	
	protected void skipNextWhitespaces(){
		while (hasNext() && isNextWhitespace()) {
			try {
				skip(1);
			} catch (ParseException e) {
				// this should never happen!
				e.printStackTrace();
			}		
		}
	}
	
	protected void skipNextSpaces(){
		while (isNextWhitespace() || isNextSeq(System.lineSeparator())) {
			try {
				if(isNextWhitespace())
					skip(1);
				else
					skip(System.lineSeparator().length());
			} catch (ParseException e) {
				// this should never happen!
				e.printStackTrace();
			}
		}
	}

	protected boolean hasNext() {
		return hasNext(1);
	}
	
	protected boolean hasNext(int n) {
		return offset + n - 1 < chars.length();
	}
}
