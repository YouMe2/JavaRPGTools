package basic;

import java.text.ParseException;

public abstract class AbsParser<T> {

	private CharSequence chars;
	private int offset; // pointer to next to parse
	
	public AbsParser(CharSequence chars) {
		this.chars = chars;
		offset = 0;
	}
	
	public abstract T parse() throws ParseException ;
	
	public CharSequence getChars() {
		return chars;
	}
	
	public int getOffset() {
		return offset;
	}

	public CharSequence getRest() {
		return chars.subSequence(offset, chars.length());
	}
	
	protected int parseInteger() throws ParseException {
		if (!isNextDigit() && !isNextAnyOf('-', '+'))
			throw new ParseException("expecting digit", offset);
		StringBuilder builder = new StringBuilder();
		do {
			builder.append(next());
		} while (isNextDigit());
		return Integer.valueOf(builder.toString());
	}
	
	protected boolean isNextSeq(CharSequence seq) {
		for (int i = 0; i < seq.length(); i++) {
			if (offset+i >= chars.length() || chars.charAt(offset + i) != seq.charAt(i))
				return false;
		}
		return true;
	}

	protected boolean isNextDigit() {
		return hasNext() && Character.isDigit(chars.charAt(offset));
	}

	protected boolean isNext(char c) {
		return isNextAnyOf(c);
	}
	
	protected boolean isNextAnyOf(char... characters) {
		if (!hasNext())
			return false;
		for (char character : characters)
			if (chars.charAt(offset) == character)
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

	protected boolean hasNext() {
		return offset < chars.length();
	}

}
