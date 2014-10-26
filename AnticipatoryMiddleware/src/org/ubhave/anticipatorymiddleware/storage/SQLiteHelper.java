package org.ubhave.anticipatorymiddleware.storage;

import org.ubhave.anticipatorymiddleware.predictor.PredictorCollection;
import org.ubhave.anticipatorymiddleware.utils.Constants;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

public class SQLiteHelper  extends SQLiteOpenHelper {

	private static final int db_version = 1;

	protected SQLiteHelper(Context context, String db_name){		
		super(context, db_name, null, db_version);	
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String DB_CREATE_1 = "create table ";
		SQLiteUtils utils = new SQLiteUtils();
		String DB_CREATE_2 = " (_id INTEGER primary key,_value text not null)";
		for(int sensor_id : Constants.ALL_SENSORS){
			db.execSQL(DB_CREATE_1+ utils.getStackedSensorDatatableName(sensor_id) + DB_CREATE_2);
			for(String predictor_name : PredictorCollection.PredictorNames)
				db.execSQL(DB_CREATE_1+ utils.getModelDataTableName(sensor_id, predictor_name) + DB_CREATE_2);
			db.execSQL(DB_CREATE_1+ utils.getLabelDataTableName(sensor_id) + DB_CREATE_2);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		SQLiteUtils utils = new SQLiteUtils();
		for(int sensor_id : Constants.ALL_SENSORS){
			db.execSQL("DROP TABLE IF EXISTS " + utils.getStackedSensorDatatableName(sensor_id));
			for(String predictor_name : PredictorCollection.PredictorNames)
				db.execSQL("DROP TABLE IF EXISTS " + utils.getModelDataTableName(sensor_id, predictor_name));
			db.execSQL("DROP TABLE IF EXISTS " + utils.getLabelDataTableName(sensor_id));
		}
		onCreate(db);
	}

}
