package com.jayceebee.budgie.parsers;

import com.jayceebee.budgie.Transaction;

public abstract class AbstractParser {

	public abstract Transaction process (String text);
	public abstract boolean isValidParser(String text);
	
}
