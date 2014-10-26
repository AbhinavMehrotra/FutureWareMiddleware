package org.ubhave.anticipatorymiddleware.filter;
import java.io.Serializable;
import java.util.Random;



public class Condition implements Serializable{

	private static final long serialVersionUID = -2195052682691850484L;
	//private static final String TAG = "AnticipatoryManager";
	private String variable, operator, value;
	private Random keyGenerator;
	private int id;
		
	private Condition(String variable, String operator, String value){
		keyGenerator = new Random();
		id = keyGenerator.nextInt();
		if(id<0)
			id *= -1;
		this.variable = variable;
		this.operator = operator;
		this.value = value;
	}
	
	public static Condition createCondition(String string){
		if(string.charAt(0) == '('){
			string = string.substring(1, string.length()-1);
		}		
		String[] tokens = string.split(" ");
		return new Condition(removeBracket(tokens[0]), tokens[1], removeBracket(tokens[2]));
	}

	
	private static String removeBracket(String string){
		int start = string.indexOf("(");
		int close = string.indexOf(")");
		if(start != -1){
			string = string.substring(1);
		}
		if(close != -1){
			string = string.substring(0, string.length()-2);
		}
		return string;
	}

	public String toString(){
//		return variable+" "+operator+" "+value;
		return "("+variable+" "+operator+" "+value+")";
	}
	
	public int getId(){
		return this.id;
	}
	
	public String getVariable(){
		return this.variable;
	}
	
	public String getOperator(){
		return this.operator;
	}
	
	public String getValue(){
		return this.value;
	}
}

