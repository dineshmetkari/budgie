package com.jayceebee.budgie.parsers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

import com.jayceebee.budgie.BudgieApplication;
import com.jayceebee.budgie.Transaction;


public class InvestecParser extends AbstractParser {
	
	private String investecRegex = "(^Purchase authorised on )(\\d+),.*for R(\\d+,?\\d+\\.\\d+).*at (.*)\\. Avail.*";
	
	@Override
	public Transaction process(String text) {
		
		Transaction t = null;
		
		Pattern regexp = Pattern.compile(investecRegex);
        Matcher matcher = regexp.matcher("");
        
        matcher.reset( text );
        if ( matcher.find() ) {
    	
    		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    		Date date;
			try {
				Log.i(BudgieApplication.TAG, "Trying to parse date " + matcher.group(2));
				date = dateFormat.parse(matcher.group(2));
				Log.i(BudgieApplication.TAG, "Parsed DATE is: " + date.toString());
			} catch (ParseException e) {
				return t;
			}
    	            
        	t = new Transaction(date, Double.valueOf(matcher.group(3).replace(",","")), matcher.group(4));
        } 
        
		return t;
	}

	@Override
	public boolean isValidParser(String text) {
		Pattern regexp = Pattern.compile(investecRegex);
        Matcher matcher = regexp.matcher("");
        
        matcher.reset( text );
        if ( matcher.find() ) {
        	return true;
        } else {
        	return false;        	
        }
        
	}

}
