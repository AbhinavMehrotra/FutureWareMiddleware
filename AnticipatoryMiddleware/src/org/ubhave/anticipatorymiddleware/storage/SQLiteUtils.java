package org.ubhave.anticipatorymiddleware.storage;

public class SQLiteUtils {
	
	
	private final String DB_NAME = "FUTUREWARE_SQLite_V9.DB";
	private String TABLE_STACKED_DATA = "TABLE_STACKED_DATA_";
	private String TABLE_LABELLED_DATA = "TABLE_LABELLED_DATA_";
	private String TABLE_MODEL_DATA = "TABLE_MODEL_DATA_";

	/**
	 * Returns the DB name
	 * @return DB name as  a String
	 */
	public String getDbName(){
		return DB_NAME;
	}

	/**
	 * Returns table name for the stacked data.
	 * @param sensor Sensor id
	 * @return Table name as String
	 */
	public String getStackedSensorDatatableName(int sensor){
		return TABLE_STACKED_DATA + sensor;
	}

	/**
	 * Returns table name for model data
	 * @param sensor Sensor id
	 * @return Table name as String
	 */
	public String getModelDataTableName(int sensor, String predictor_type){
		return TABLE_MODEL_DATA + sensor + predictor_type;
	}
	
	/**
	 * Returns table name for label data
	 * @param sensor Sensor id
	 * @return Table name as String
	 */
	public String getLabelDataTableName(int sensor){
		return TABLE_LABELLED_DATA + sensor;
	}
	
}
