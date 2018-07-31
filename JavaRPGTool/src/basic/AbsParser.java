package basic;

import java.text.ParseException;

public abstract class AbsParser<T> {

	private String chars;
	private int offset; // pointer to next to parse
	
	public AbsParser(String chars) {
		this.chars = chars;
		offset = 0;
	}
	
	public abstract Pair<T,String> parse() throws ParseException ;
	
	public String getChars() {
		return chars;
	}
	
	public int getOffset() {
		return offset;
	}

	public String getRest() {
		return chars.substring(offset, chars.length());
	}
	
	protected Pair<Integer, String> parseInteger() throws ParseException {
		StringBuilder builder = new StringBuilder();
		
		if (isNextAnyOf('-', '+')) {
			builder.append(next()); // +-
		}
		if (!isNextDigit())
			return new Pair<Integer, String>(null, getRest());
		
		do {
			builder.append(next());
		} while (isNextDigit());
		return new Pair<Integer, String>(Integer.valueOf(builder.toString()), getRest());
	}
	
	protected boolean isNextSeq(String seq) {
		for (int i = 0; i < seq.length(); i++) {
			if (offset+i >= chars.length() || chars.charAt(offset + i) != seq.charAt(i))
				return false;
		}
		return true;
	}

	protected boolean isNextDigit() {
		return hasNext() && Character.isDigit(chars.charAt(offset));
	}
	
	protected boolean isNextInt() {
		return isNextDigit() || (isNextAnyOf('-', '+') && hasNext(2) && Character.isDigit(chars.charAt(offset+1)));
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
		return hasNext(1);
	}
	
	protected boolean hasNext(int n) {
		return offset + n - 1 < chars.length();
	}
}
