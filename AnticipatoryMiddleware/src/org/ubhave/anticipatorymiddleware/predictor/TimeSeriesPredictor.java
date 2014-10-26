package org.ubhave.anticipatorymiddleware.predictor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ubhave.anticipatorymiddleware.predictordata.PredictionResult;
import org.ubhave.anticipatorymiddleware.predictordata.PredictorData;
import org.ubhave.anticipatorymiddleware.sensors.SensorManager;
import org.ubhave.anticipatorymiddleware.time.Time;

import android.content.Context;
import android.util.Log;

public class TimeSeriesPredictor implements PredictorModule{

	private static final String TAG = "AnticipatoryManager";

	@Override
	public ArrayList<Integer> idsOfRequiredData(int predictor_type) {
		ArrayList<Integer> predictor_types = new ArrayList<Integer>();
		predictor_types.add(predictor_type);
		return predictor_types;
	}

	@Override
	public PredictorData predictionRequest(int predictor_id,
			int context_sampling_rate, int context_life_cycle,
			ArrayList<JSONObject> predictor_models, String current_state,
			String state_to_be_predicted) {
		PredictorData predictor_data = PredictorData.getInstance(predictor_id);
		ArrayList<PredictionResult> result = new ArrayList<PredictionResult>();

		try{
			JSONArray data = predictor_models.get(0).getJSONArray("DATA");
			JSONArray data_array;
			for(int i=0; i<data.length(); i++){
				data_array = data.getJSONArray(i);
				int probability = getProbabilityOfElement(state_to_be_predicted, data_array);
				if(probability > 0){
					PredictionResult p_result = new PredictionResult();	
					p_result.setPredictedState(state_to_be_predicted);	
					p_result.setPredictedTime(getTime(i, context_sampling_rate));
					p_result.setPredictionProbability(probability);
					p_result.setPredictionConfidenceLevel(95); //TODO: find confidence level
					result.add(p_result);
				}
			}
			predictor_data.setResult(result);
			return predictor_data;
		}
		catch(JSONException e){
			System.out.println("Error!! "+ e.toString());
		} 
		return predictor_data;
	}

	@Override
	public PredictorData predictionRequest(int predictor_id,
			int context_sampling_rate, int context_life_cycle,
			ArrayList<JSONObject> predictor_models, String current_state,
			long notification_period) {
		try{
			PredictorData predictor_data = PredictorData.getInstance(predictor_id);
			ArrayList<PredictionResult> result = new ArrayList<PredictionResult>();
			Time current_time  = Time.getCurrentTime();
			int current_time_in_minutes = current_time.get(Time.HOURS)*60 + current_time.get(Time.MINUTES);
			int current_position = current_time_in_minutes / context_sampling_rate;
			int prediction_time_in_minutes = current_time_in_minutes + (int) notification_period;	
			int prediction_position = prediction_time_in_minutes / context_sampling_rate;
			JSONArray data = predictor_models.get(0).getJSONArray("DATA");

			Log.v(TAG, "Model: "+data);
			ArrayList<JSONArray> position_data_array = new ArrayList<JSONArray>();
			//get data from current_position+1 to prediction_position
			for(int i=current_position+1; i<=prediction_position; i++)
			{
				JSONArray element = new JSONArray();
				element = data.getJSONArray(i);
				position_data_array.add(element);
			}

			JSONArray complete_data_set = new JSONArray();
			Set<String> states = new HashSet<String>();
			for(JSONArray position_data : position_data_array)
			{
				for(int i=0; i<position_data.length(); i++)
				{
					String state = position_data.getString(i);
					complete_data_set.put(state);
					states.add(state);
				}
			}
			for(String state : states){
				int probability = getProbabilityOfElement(state, complete_data_set);
				if(probability > 0){
					PredictionResult p_result = new PredictionResult();	
					p_result.setPredictedState(state);	
					p_result.setPredictedTime(getTime(prediction_position, context_sampling_rate));
					p_result.setPredictionProbability(probability);
					p_result.setPredictionConfidenceLevel(95); //TODO: find confidence level
					result.add(p_result);
				}	
			}
			//Log.d(TAG, "result: "+result);
			predictor_data.setResult(result);
			//Log.d(TAG, "predictor_data: "+predictor_data);
			return predictor_data;
		}
		catch(JSONException e){
			Log.e(TAG, e.toString());
			return null;
		}
	}

	@Override
	public JSONObject generateDataModel(Context app_context, JSONObject data,
			ArrayList<String> labels) {
		try{
			JSONObject model = new JSONObject();
			JSONArray model_array = new JSONArray();
			SensorManager sm = new SensorManager(app_context);
			int context_sampling_rate = sm.getContextSamplingRate();
			int context_life_cycle = sm.getContextLifeCycleTimePeriod();
			int number_of_samples_in_a_day = context_life_cycle / context_sampling_rate;

			//create empty array
			for(int i=0; i<number_of_samples_in_a_day; i++){
				JSONArray json_array = new JSONArray();
				model_array.put(json_array);	
			}

			if(data!=null){
				JSONArray data_array = data.getJSONArray("DATA");
				JSONArray json_array;
				JSONObject json_object;
				Time t;
				int minutes, position;
				for(int i=0; i<data_array.length(); i++)
				{
					json_object = data_array.getJSONObject(i);
					t = Time.stringToTime(json_object.getString("TIME"));
					minutes = t.get(Time.HOURS)*60 + t.get(Time.MINUTES);
					position = minutes / context_sampling_rate;
					json_array = model_array.getJSONArray(position);
					json_array.put(json_object.getString("STATE"));
					model_array.put(position, json_array);
				}
//				Log.d(TAG,"Time series model: "+ model_array);	
			}
			else{
				Log.i(TAG,"Data is null, so default model generated.");
			}
			model.put("DATA", model_array);	
			return model;
		}
		catch(JSONException e){
			Log.e(TAG, e.toString());
			return null;
		}
	}

	private int getProbabilityOfElement(String element, JSONArray array) throws JSONException{
		int counter =0;
		for(int i=0; i<array.length(); i++){
			if(array.getString(i).equalsIgnoreCase(element)){
				counter++;
			}
		}
		return (counter * 100) / array.length();
	}

	private Time getTime(int position, int sampling_rate){
		int minutes = position * sampling_rate;
		int hours = minutes / 60;
		minutes = minutes - (hours * 60);
		Time time = new Time(hours, minutes, 0, 0);
		return time;
	}

	@Override
	public String getNameOrPath() {
		return this.getClass().getName();
	}

}