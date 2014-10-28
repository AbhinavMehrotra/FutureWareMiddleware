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
