package org.ubhave.anticipatorymiddleware.filter;

public class Operators {

	public static String AND = "&&";

	public static String OR = "||";

	public static String EQUAL_TO = "==";

	public static String NOT_EQUAL_TO = "!=";

	public static String GREATER_THAN = ">";
	
	public static String GREATER_THAN_EQUAL_TO = ">=";
	
	public static String SMALLER_THAN = "<";
	
	public static String SMALLER_THAN_EQUAL_TO = "<=";
	
	public static Boolean isComparisonOperator(String operator){
		if(operator == EQUAL_TO || operator == NOT_EQUAL_TO || operator == GREATER_THAN || 
				operator == GREATER_THAN_EQUAL_TO || operator == SMALLER_THAN || 
				operator == SMALLER_THAN_EQUAL_TO){
			return true;
		}
		return false;
	}
	
	public static Boolean isLogicalOperator(String operator){
		if(operator == AND || operator == OR){
			return true;
		}
		return false;
	}
	
}
