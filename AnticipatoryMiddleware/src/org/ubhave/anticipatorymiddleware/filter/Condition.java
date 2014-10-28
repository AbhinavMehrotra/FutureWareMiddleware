/*******************************************************************************
 *
 * FutureWare Middleware
 *
 * Copyright (c) ${2014}, University of Birmingham
 * Abhinav Mehrotra, a.mehrotra@cs.bham.ac.uk
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the University of Birmingham 
 *       nor the names of its contributors may be used to endorse or
 *       promote products derived from this software without specific prior
 *       written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE ABOVE COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *******************************************************************************/
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

