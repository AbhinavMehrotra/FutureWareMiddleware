package org.ubhave.anticipatorymiddleware.filter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.ubhave.anticipatorymiddleware.filters.variables.ActivityVariable;
import org.ubhave.anticipatorymiddleware.filters.variables.LocationVariable;
import org.ubhave.anticipatorymiddleware.filters.variables.SocialVariable;
import org.ubhave.anticipatorymiddleware.filters.variables.UserContext;
import org.ubhave.anticipatorymiddleware.predictordata.ActivityData;
import org.ubhave.anticipatorymiddleware.predictordata.LocationData;
import org.ubhave.anticipatorymiddleware.predictordata.PredictionResult;
import org.ubhave.anticipatorymiddleware.predictordata.PredictorData;
import org.ubhave.anticipatorymiddleware.predictordata.SocialData;
import org.ubhave.anticipatorymiddleware.utils.Constants;


public class FilterParser implements Serializable{


	private static final long serialVersionUID = -2131900519810459169L;
	private String expression_string;
	private Set<PredictorData> predictor_data;

	protected FilterParser(String expression_string){
		this.expression_string = expression_string;		
	}

	public Set<Integer> getSetOfRequiredPredictors(){
		//TODO: find the variable that are equal to predictor names
		Set<Integer> predictors = new HashSet<Integer>();
		for(Condition c : this.getSetOfConditions()){
			System.out.println("condition: "+ c.getVariable());
			if(UserContext.isValidContextType(c.getVariable()))
				predictors.add(UserContext.getPredictorIdByConditionVariable(c.getVariable()));
		}		
		return predictors;
	}

	public Set<Condition> getSetOfConditions(){
		//TODO: return the set of conditions
		Set<Condition> conditions = new HashSet<Condition>();
		String temp_string = expression_string;
		for(String condition_string : getConditions(this.expression_string)){
			//System.out.println(this.expression_string);
			int e1_start = indexOfInnerStartingBracketForCondition(condition_string, 0);
			int e1_end = indexOfInnerEndingBracketForCondition(condition_string);
			//System.out.println("Start and End: "+ e1_start +" & "+e1_end);
			Condition c;
			if(e1_start == -1){
				c = Condition.createCondition(condition_string);
				temp_string = temp_string.replace(condition_string, String.valueOf(c.getId()));
			}
			//			else if()
			else{
				c = Condition.createCondition(condition_string.substring(e1_start, e1_end+1));
				temp_string = temp_string.replace(condition_string.substring(e1_start, e1_end+1), String.valueOf(c.getId()));
			}
			conditions.add(c);
		}		

		return conditions;
	}





	public Boolean validateFilter(Set<PredictorData> predictor_data){
		/**TODO: 
		 * 1. Gets Set<PredictorData> prediction_data 
		 * 2. Create tree for the expression_string
		 * 3. Insert the predicted data values in the tree to get the result
		 * 
		 */

		this.predictor_data = predictor_data;
		String boolean_string = replaceConditionWithBooleanValues();
		Boolean result = computeBooleanString(boolean_string);
		return result;
	}


	//replaces condition with a boolean
	private String replaceConditionWithBooleanValues(){
		String string = this.expression_string;
		ArrayList<String> conditions = new ArrayList<String>();
		conditions = getConditions(string);
		for(String condition : conditions){
			int e1_start = indexOfInnerStartingBracketForCondition(condition, 0);
			int e1_end = indexOfInnerEndingBracketForCondition(condition);
			Condition c;
			if(e1_start == -1){
				c = Condition.createCondition(condition);
				string = string.replace(condition, String.valueOf(computeCondition(c)));
			}
			else{
				c = Condition.createCondition(condition.substring(e1_start, e1_end+1));
				string = string.replace(condition.substring(e1_start, e1_end+1), String.valueOf(computeCondition(c)));
			}			
		}
		return string;
	}


	private Boolean computeCondition(Condition condition){		
		LocationData location_data = null;
		ActivityData activity_data = null;
		SocialData social_data = null;
		for(PredictorData data : predictor_data){
			if(data.getPredictorType() == Constants.PREDICTOR_TYPE_ACTIVITY){
				activity_data = (ActivityData) data;
			}
			else if(data.getPredictorType() == Constants.PREDICTOR_TYPE_LOCATION){
				location_data = (LocationData) data;
			}
			else if(data.getPredictorType() == Constants.PREDICTOR_TYPE_SOCIAL){
				social_data = (SocialData) data;
			}
		}
		String condition_variable = condition.getVariable().toUpperCase(Locale.ENGLISH);
		String condition_operator = condition.getOperator();
		String condition_value = condition.getValue().toUpperCase(Locale.ENGLISH);
		String predicted_value = "";
		if(condition_variable.contains(ActivityVariable.Activity)){
			int i = 0;
			boolean[] result = new boolean[activity_data.getResult().size()];
			for(PredictionResult pr : activity_data.getResult()){
				if(condition_variable.equalsIgnoreCase(ActivityVariable.Activity)){
					predicted_value = pr.getPredictedState(); 
				}
				else if(condition_variable.equalsIgnoreCase(ActivityVariable.PredictionProbability)){
					predicted_value = String.valueOf(pr.getPredictionProbability());
				}
				else if(condition_variable.equalsIgnoreCase(ActivityVariable.PredictionConfidenceLevel)){
					predicted_value = String.valueOf(pr.getPredictionConfidenceLevel());
				}
				else if(condition_variable.equalsIgnoreCase(ActivityVariable.PredictedTime)){
					predicted_value = String.valueOf(pr.getPredictedTime());
				}
				result[i++] = computeCondition(condition_value, condition_operator, predicted_value);
			}
			for(boolean b : result)
				if(b == true)
					return true;
		}
		else if(condition_variable.contains(LocationVariable.Location)){
			int i = 0;
			boolean[] result = new boolean[location_data.getResult().size()];
			for(PredictionResult pr : location_data.getResult()){
				if(condition_variable.equalsIgnoreCase(LocationVariable.Location)){
					predicted_value = pr.getPredictedState(); 
				}
				else if(condition_variable.equalsIgnoreCase(LocationVariable.PredictionProbability)){
					predicted_value = String.valueOf(pr.getPredictionProbability());
				}
				else if(condition_variable.equalsIgnoreCase(LocationVariable.PredictionConfidenceLevel)){
					predicted_value = String.valueOf(pr.getPredictionConfidenceLevel());
				}
				else if(condition_variable.equalsIgnoreCase(LocationVariable.PredictedTime)){
					predicted_value = String.valueOf(pr.getPredictedTime());
				}
				result[i++] = computeCondition(condition_value, condition_operator, predicted_value);
			}
			for(boolean b : result)
				if(b == true)
					return true;
		}
		else if(condition_variable.contains("friend_ids_")){
			int i = 0;
			boolean[] result = new boolean[social_data.getResult().size()];
			for(PredictionResult pr : social_data.getResult()){
				if(condition_variable.equalsIgnoreCase("friend_ids_")){
					predicted_value = pr.getPredictedState(); 
				}
				else if(condition_variable.equalsIgnoreCase(SocialVariable.PredictionProbability)){
					predicted_value = String.valueOf(pr.getPredictionProbability());
				}
				else if(condition_variable.equalsIgnoreCase(SocialVariable.PredictionConfidenceLevel)){
					predicted_value = String.valueOf(pr.getPredictionConfidenceLevel());
				}
				else if(condition_variable.equalsIgnoreCase(SocialVariable.PredictedTime)){
					predicted_value = String.valueOf(pr.getPredictedTime());
				}
				result[i++] = computeCondition(condition_value, condition_operator, predicted_value);
			}
			for(boolean b : result)
				if(b == true)
					return true;
		}
		return false;
	}

	//**********************************************//
	//*****WORKING WITH MOST PROBABLE RESULT********//
	//**********************************************//	
	//	private Boolean computeCondition(Condition condition){		
	//		LocationData location_data = null;
	//		ActivityData activity_data = null;
	//		SocialData social_data = null;
	//		for(PredictorData data : predictor_data){
	//			if(data.getPredictorType() == Constants.PREDICTOR_TYPE_ACTIVITY){
	//				activity_data = (ActivityData) data;
	//			}
	//			else if(data.getPredictorType() == Constants.PREDICTOR_TYPE_LOCATION){
	//				location_data = (LocationData) data;
	//			}
	//			else if(data.getPredictorType() == Constants.PREDICTOR_TYPE_SOCIAL){
	//				social_data = (SocialData) data;
	//			}
	//		}
	//		/*
	//		 * TODO: currently this method assume that the filter requires the most probable predictions, 
	//		 * but it should consider all. Need to fix this!!
	//		 */
	//		String condition_variable = condition.getVariable().toUpperCase(Locale.ENGLISH);
	//		String condition_operator = condition.getOperator();
	//		String condition_value = condition.getValue().toUpperCase(Locale.ENGLISH);
	//		String predicted_value = "";
	//		if(condition_variable.contains(ActivityVariable.Activity)){
	//			if(condition_variable.equalsIgnoreCase(ActivityVariable.Activity)){
	//				predicted_value = activity_data.getMostProbableResult().getPredictedState(); 
	//			}
	//			else if(condition_variable.equalsIgnoreCase(ActivityVariable.PredictionProbability)){
	//				predicted_value = String.valueOf(activity_data.getMostProbableResult().getPredictionProbability());
	//			}
	//			else if(condition_variable.equalsIgnoreCase(ActivityVariable.PredictionConfidenceLevel)){
	//				predicted_value = String.valueOf(activity_data.getMostProbableResult().getPredictionConfidenceLevel());
	//			}
	//			else if(condition_variable.equalsIgnoreCase(ActivityVariable.PredictedTime)){
	//				predicted_value = String.valueOf(activity_data.getMostProbableResult().getPredictedTime());
	//			}			
	//		}
	//		else if(condition_variable.contains(LocationVariable.Location)){
	//			if(condition_variable.equalsIgnoreCase(LocationVariable.Location)){
	//				predicted_value = location_data.getMostProbableResult().getPredictedState(); 
	//			}
	//			else if(condition_variable.equalsIgnoreCase(LocationVariable.PredictionProbability)){
	//				predicted_value = String.valueOf(location_data.getMostProbableResult().getPredictionProbability());
	//			}
	//			else if(condition_variable.equalsIgnoreCase(LocationVariable.PredictionConfidenceLevel)){
	//				predicted_value = String.valueOf(location_data.getMostProbableResult().getPredictionConfidenceLevel());
	//			}
	//			else if(condition_variable.equalsIgnoreCase(LocationVariable.PredictedTime)){
	//				predicted_value = String.valueOf(location_data.getMostProbableResult().getPredictedTime());
	//			}
	//		}
	//		else if(condition_variable.contains("friend_ids_")){
	//			if(condition_variable.equalsIgnoreCase("friend_ids_")){
	//				predicted_value = social_data.getMostProbableResult().getPredictedState(); 
	//			}
	//			else if(condition_variable.equalsIgnoreCase(SocialVariable.PredictionProbability)){
	//				predicted_value = String.valueOf(social_data.getMostProbableResult().getPredictionProbability());
	//			}
	//			else if(condition_variable.equalsIgnoreCase(SocialVariable.PredictionConfidenceLevel)){
	//				predicted_value = String.valueOf(social_data.getMostProbableResult().getPredictionConfidenceLevel());
	//			}
	//			else if(condition_variable.equalsIgnoreCase(SocialVariable.PredictedTime)){
	//				predicted_value = String.valueOf(social_data.getMostProbableResult().getPredictedTime());
	//			}
	//		}
	//		return computeCondition(condition_value, condition_operator, predicted_value);
	//	}


	private Boolean computeCondition(String condition_value, String condition_operator, String predicted_value){
		if(condition_operator.equalsIgnoreCase(Operators.EQUAL_TO)){
			return condition_value.equalsIgnoreCase(predicted_value);
		}
		else if(condition_operator.equalsIgnoreCase(Operators.NOT_EQUAL_TO)){
			return !condition_value.equalsIgnoreCase(predicted_value);
		}
		else if(condition_operator.equalsIgnoreCase(Operators.GREATER_THAN)){
			return Double.parseDouble(condition_value) < Double.parseDouble(predicted_value);
		}
		else if(condition_operator.equalsIgnoreCase(Operators.GREATER_THAN_EQUAL_TO)){
			return Double.parseDouble(condition_value) <= Double.parseDouble(predicted_value);
		}
		else if(condition_operator.equalsIgnoreCase(Operators.SMALLER_THAN)){
			return Double.parseDouble(condition_value) > Double.parseDouble(predicted_value);
		}
		else if(condition_operator.equalsIgnoreCase(Operators.SMALLER_THAN_EQUAL_TO)){
			return Double.parseDouble(condition_value) >= Double.parseDouble(predicted_value);
		}
		System.out.println("NO MATCH");	
		return false;
	}

	private Boolean computeBooleanString(String boolean_string){		
		while(true){

			int start = indexOfFirstInnerStartingBracketForExpression(boolean_string, 0);
			int end = indexOfFirstInnerEndingBracketForExpression(boolean_string, start);
			if(start == -1){
				boolean_string = boolean_string.replace(boolean_string, String.valueOf(computeOneBracketOfBooleans(boolean_string)));
			}
			else{
				boolean_string = boolean_string.replace(boolean_string.substring(start, end+1), String.valueOf(computeOneBracketOfBooleans(boolean_string.substring(start, end+1))));
			}
			if(!boolean_string.contains("&&") && !boolean_string.contains("||"))
				break;	
		}
		return Boolean.parseBoolean(boolean_string.trim());
	}

	private Boolean computeOneBracketOfBooleans(String string){
		if(string.contains("("))
			string = string.substring(1);
		if(string.contains(")"))
			string = string.substring(0, string.length()-1);
		System.out.println(string);
		String[] values = string.split(" ");
		Boolean result= Boolean.parseBoolean(values[0].trim());
		for(int i=1; i<values.length-1; i=i+2){
			if(values[i].equalsIgnoreCase("&&"))
				result = result && Boolean.parseBoolean(values[i+1]);
			else
				result = result || Boolean.parseBoolean(values[i+1]);
		}
		return result;
	}


	//
	//	private static String createStringWithExpressionTreeWithConditions(String expression_string){		
	//		while(true){
	//
	//			int start = indexOfFirstInnerStartingBracketForExpression(expression_string, 0);
	//			int end = indexOfFirstInnerEndingBracketForExpression(expression_string, start);
	//
	//			ExpressionNode e;
	//			if(start == -1){
	//				e = ExpressionNode.createExpressionTree(expression_string);
	//				expression_string = expression_string.replace(expression_string, String.valueOf(e.getId()));
	//			}
	//			else{
	//				e = ExpressionNode.createExpressionTree(expression_string.substring(start, end+1));
	//				expression_string = expression_string.replace(expression_string.substring(start, end+1), String.valueOf(e.getId()));
	//			}
	//			if(!expression_string.contains("&&") && !expression_string.contains("||"))
	//				break;			
	//		}
	//		return expression_string;
	//	}


	//replaces conditions with its id.
	//	private static String replaceConditionWithConditionIds(String string){
	//		ArrayList<String> conditions = new ArrayList<String>();
	//		conditions = getConditions(string);
	//		for(String condition : conditions){
	//			int e1_start = indexOfInnerStartingBracketForCondition(condition, 0);
	//			int e1_end = indexOfInnerEndingBracketForCondition(condition);
	//			Condition c;
	//			if(e1_start == -1){
	//				c = Condition.createCondition(condition);
	//				string = string.replace(condition, String.valueOf(c.getId()));
	//			}
	//			else{
	//				c = Condition.createCondition(condition.substring(e1_start, e1_end+1));
	//				string = string.replace(condition.substring(e1_start, e1_end+1), String.valueOf(c.getId()));
	//			}			
	//		}
	//		return string;
	//	}


	private static ArrayList<String> getConditions(String expression){
		//System.out.println(expression);
		ArrayList<Integer> indeces = new ArrayList<Integer>();
		ArrayList<String> conditions = new ArrayList<String>();
		indeces = getIndecesOfLogicalOperator(expression);
		indeces.add(0);
		indeces.add(expression.length());
		Collections.sort(indeces);

		//System.out.println(indeces);


		int start, end, counter = 0;

		while(counter < indeces.size()-1){
			start=indeces.get(counter);
			end=indeces.get(counter+1);
			counter++;

			if(start > 0)
				start = start+3;
			if(end < expression.length()-1)
				end = end-1;
			conditions.add(expression.substring(start, end));
		}
		return conditions;
	}
	//
	//	private static ArrayList<String> getLogicalOperators(String expression){
	//		ArrayList<Integer> indeces = new ArrayList<Integer>();
	//		ArrayList<String> operators = new ArrayList<String>();
	//		indeces = getIndecesOfLogicalOperator(expression);
	//		for(int i=0; i<indeces.size();i++){
	//			operators.add(expression.substring(indeces.get(i), indeces.get(i)+2));
	//		}
	//		return operators;
	//	}

	private  static ArrayList<Integer> getIndecesOfLogicalOperator(String string){
		ArrayList<Integer> indeces = new ArrayList<Integer>();
		int and_index, or_index, smallest_index;
		int index_counter = -1;
		while (true){
			and_index = string.indexOf("&&", index_counter+1);
			or_index=string.indexOf("||", index_counter+1);

			if(and_index == -1 && or_index == -1)
				break;

			if(and_index == -1)
				smallest_index=or_index;
			else if(or_index == -1)
				smallest_index=and_index;
			else if(and_index<or_index)
				smallest_index=and_index;
			else
				smallest_index=or_index;

			indeces.add(smallest_index);
			index_counter=smallest_index;
		}
		return indeces;
	}

	private static int indexOfInnerStartingBracketForCondition(String string, int starting_index){
		if(string.charAt(0) != '('){
			return starting_index-1;
		}
		else{
			return indexOfInnerStartingBracketForCondition(string.substring(1), starting_index+1);
		}
	}

	private static int indexOfInnerEndingBracketForCondition(String string){
		if(string.charAt(0) != '(')
			return -1;
		else
			return string.indexOf(')');
	}


	private static int indexOfFirstInnerStartingBracketForExpression(String string, int starting_index){
		if(!string.contains("(")){
			return starting_index-1;
		}
		else
			return indexOfFirstInnerStartingBracketForExpression(string.substring(1), starting_index+1);
	}

	private static int indexOfFirstInnerEndingBracketForExpression(String string, int starting_index){
		if(!string.contains("("))
			return -1;
		else
			return string.indexOf(')', starting_index);
	}



}
