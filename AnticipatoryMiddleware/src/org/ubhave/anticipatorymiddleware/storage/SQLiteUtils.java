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

public class SQLiteUtils {
	
	
	private final String DB_NAME = "FUTUREWARE_SQLite_V11.DB";
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
