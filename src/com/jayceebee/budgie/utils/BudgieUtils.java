package com.jayceebee.budgie.utils;

public class BudgieUtils {
	

	public static String formatString (String theString)
	{
		//set to all lower case
		String ret = theString.toLowerCase();
		//Capitalise first letter
		ret = ret.substring(0,1).toUpperCase() + ret.substring(1);
		return ret;
	}
}
