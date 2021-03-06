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

import java.io.File;

import org.ubhave.anticipatorymiddleware.sharedpreferences.SharedPreferences;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class FilePathGenerator {

	private final String TAG = "AnticipatoryManager";

/*
	public String generateLabelledDataFilePath(int sensor_type){
		String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + 
				"/AnticipatoryMiddleware/LabelledData/Predictor"+sensor_type+"/data.txt";
		String dir_path = file_path.substring(0, file_path.length()-9);
		File dir = new File (dir_path);
	    if(dir.mkdirs())
			Log.d(TAG, "Dir created at:"+dir_path);	    	
		return file_path;
	}

	public String generateTrainingDataForLabelsFilePath(int sensor_type){
		String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + 
				"/AnticipatoryMiddleware/TrainingDataForLabels/Predictor"+sensor_type+"/data.txt";
		String dir_path = file_path.substring(0, file_path.length()-9);
		File dir = new File (dir_path);
	    if(dir.mkdirs())
			Log.d(TAG, "Dir created at:"+dir_path);	 
		return file_path;
	}

	public String generateStackedPredictorDataFilePath(int predictor_type){
		String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + 
				"/AnticipatoryMiddleware/StackedPredictorData/Predictor"+predictor_type+"/data.txt";
		String dir_path = file_path.substring(0, file_path.length()-9);
		File dir = new File (dir_path);
	    if(dir.mkdirs())
			Log.d(TAG, "Dir created at:"+dir_path);	
		Log.d(TAG, "Checking the file at: "+file_path);	
	    try {
		    File file = new File(file_path);
		    if(file.exists()){
		    	Log.i(TAG, "File already exists.");	
		    }
		    else{
		    	Log.i(TAG, "File did not exists and a new file is created.");	
			FileOutputStream f = new FileOutputStream(file);
			f.close();
		    }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file_path;
	}

	public String generatePredictorModelDataFilePath(int predictor_type){
		String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + 
				"/AnticipatoryMiddleware/PreddictorModelData/Predictor"+predictor_type+"/data.txt";
		String dir_path = file_path.substring(0, file_path.length()-9);
		File dir = new File (dir_path);
	    if(dir.mkdirs())
			Log.d(TAG, "Dir created at:"+dir_path);	
		return file_path;
	}
*/
	public String generateSubscriptionDataFilePath(Context context){
		SharedPreferences sp  = new SharedPreferences(context);
		String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + 
				"/AnticipatoryMiddleware/" + sp.getString(sp.USER_ID) + "/SubscriptionData/data.txt";
		String dir_path = file_path.substring(0, file_path.length()-9);
		File dir = new File (dir_path);
	    if(dir.mkdirs())
			Log.d(TAG, "Dir created at:"+dir_path);	
		return file_path;
	}
	

	public String generatePrivacyDataFilePath(Context context){
		SharedPreferences sp  = new SharedPreferences(context);
		String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + 
				"/AnticipatoryMiddleware/" + sp.getString(sp.USER_ID) + "/PrivacyData/data.txt";
		String dir_path = file_path.substring(0, file_path.length()-9);
		Log.d(TAG, "Trying to create a dir at:"+dir_path);
		File dir = new File (dir_path);
	    if(dir.mkdirs())
			Log.d(TAG, "Dir created at:"+dir_path);	
		return file_path;
	}
	

	public String generateGroupPredictionResultDataFilePath(int subscription_id){
		String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + 
				"/AnticipatoryMiddleware/GroupPredictionResultData/Subscription"+subscription_id+"/data.txt";
		String dir_path = file_path.substring(0, file_path.length()-9);
		Log.d(TAG, "Trying to create a dir at:"+dir_path);
		File dir = new File (dir_path);
	    if(dir.mkdirs())
			Log.d(TAG, "Dir created at:"+dir_path);	
		return file_path;
	}
	
	
	public String generateDBFilePath(Context context){
		SharedPreferences sp  = new SharedPreferences(context);
		String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + 
				"/AnticipatoryMiddleware/" + sp.getString(sp.USER_ID) + "/DB/data.db";
		String dir_path = file_path.substring(0, file_path.length()-8);
		File dir = new File (dir_path);
	    if(dir.mkdirs())
			Log.d(TAG, "Dir created at:"+dir_path);	
		return file_path;
	}
}
