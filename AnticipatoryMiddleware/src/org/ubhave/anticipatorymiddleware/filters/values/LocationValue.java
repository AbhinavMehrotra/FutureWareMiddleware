package org.ubhave.anticipatorymiddleware.filters.values;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class LocationValue {
	
	public static final String HOME = "HOME";
	
	public static final String WORK = "WORK";
	
	public static final String UNKNOWN = "UNKNOWN";
	
	
	public static Set<String> getAllValues(){
		Set<String> values = new HashSet<String>();
		values.add(HOME);
		values.add(WORK);
		values.add(UNKNOWN);
		return values;
	}
	
	public static Boolean isValid(String value){
		return getAllValues().contains(value.toUpperCase(Locale.UK));
	}
	
}
