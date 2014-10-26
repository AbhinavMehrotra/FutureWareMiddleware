package org.ubhave.anticipatorymiddleware.filters.operators;

import java.util.HashSet;
import java.util.Set;

public class ComparisonOperator {

	public final static String EQUAL_TO = "==";
	
	public final static String NOT_EQUAL_TO = "!=";
	
	public final static String GREATER_THAN = ">";

	public final static String GREATER_THAN_OR_EQUAL_TO = ">=";
	
	public final static String SMALLER_THAN = "<";
	
	public final static String SMALLER_THAN_OR_EQUAL_TO = "<=";

	public static Set<String> getAllValues(){
		Set<String> values = new HashSet<String>();
		values.add(EQUAL_TO);
		values.add(NOT_EQUAL_TO);
		values.add(GREATER_THAN);
		values.add(GREATER_THAN_OR_EQUAL_TO);
		values.add(SMALLER_THAN);
		values.add(SMALLER_THAN_OR_EQUAL_TO);
		return values;
	}
	
	public static Boolean isValid(String value){
		return getAllValues().contains(value);
	}
	
}
