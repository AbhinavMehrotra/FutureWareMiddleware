package org.ubhave.anticipatorymiddleware.filters.values;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class ActivityValue{

	public static final String WALKING = "WALKING";
	
	public static final String STILL = "STILL";
	
	public static final String OTHER = "OTHER";
	
	public static Set<String> getAllValues(){
		Set<String> values = new HashSet<String>();
		values.add(WALKING);
		values.add(STILL);
		values.add(OTHER);
		return values;
	}
	
	public static Boolean isValid(String value){
		return getAllValues().contains(value.toUpperCase(Locale.UK));
	}
	
}
