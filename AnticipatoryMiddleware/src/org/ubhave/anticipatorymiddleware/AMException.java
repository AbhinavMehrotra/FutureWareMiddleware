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
package org.ubhave.anticipatorymiddleware;

public class AMException extends Exception
{
	private static final long serialVersionUID = -6952859423645368705L;

	// error codes
	public static final int INVALID_PARAMETER = 1000;
	public static final int CONFIG_NOT_SUPPORTED = 1001;
	public static final int UNKNOWN_PREDICTOR_TYPE = 1002;
	public static final int INVALID_STATE = 1003;
	public static final int NULL_MODALITY = 1004;
	public static final int FILE_MISSING = 1005;
	public static final int CANNOT_READ_FILE = 1006;
	public static final int FILE_FORMAT_NOT_SUPPORTED = 1007;
	public static final int JSON_OBJECT_MISSING = 1008;
	public static final int PREDICTOR_MODEL_MISSING = 1009;
	public static final int ERROR_WITH_MARKOV_CHAIN = 1010;
	public static final int CANNOT_WRITE_TO_FILE = 1011;
	public static final int INTENT_EXTRA_MISSING = 1012;
	public static final int NULL_POINTER = 1013;
	public static final int TIME_TRAVEL_EXCEPTION = 1014;
	public static final int PREDICTION_FREQUENCY_GRANULARITY_EXCEPTION = 1015;
	

	public static final int INVALID_PREDICTOR = 1016;
	
	//testing from windows
	public static final int temp = 0;
	
	//testing from linux
	public static final int temp_linux = 0;
	

	private int errorCode;
	private String message;

	public AMException(int errorCode, String message)
	{
		super(message);
		this.errorCode = errorCode;
		this.message = message;
	}

	public int getErrorCode()
	{
		return errorCode;
	}

	public String getMessage()
	{
		return message;
	}

}
