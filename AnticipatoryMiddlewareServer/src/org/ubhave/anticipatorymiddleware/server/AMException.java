package org.ubhave.anticipatorymiddleware.server;

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
