package org.ubhave.anticipatorymiddleware.filters.operators;

import java.util.HashSet;
import java.util.Set;


public class LogicalOperator {

	public final static String AND = "&&";
	
	public final static String OR = "||";
	
	public final static String NOT = "!";

	public static Set<String> getAllValues(){
		Set<String> values = new HashSet<String>();
		values.add(AND);
		values.add(OR);
		values.add(NOT); 
		return values;
	}
	
	public static Boolean isValid(String value){
		return getAllValues().contains(value);
	}
	
}
