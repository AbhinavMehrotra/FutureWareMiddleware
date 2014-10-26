package org.ubhave.anticipatorymiddleware.classifiers;

import java.util.ArrayList;

import org.json.JSONArray;

import com.ubhave.sensormanager.data.SensorData;

public interface Classifier {

	public void trainClassifier(JSONArray array);

	public ArrayList<String> getAllLabels();

	public String classifySensorData(SensorData sensorData);

}