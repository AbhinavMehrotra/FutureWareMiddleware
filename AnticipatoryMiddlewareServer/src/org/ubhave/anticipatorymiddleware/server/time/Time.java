package org.ubhave.anticipatorymiddleware.server.time;

import java.io.Serializable;
import java.util.Calendar;

public class Time implements Serializable, Comparable<Time>{

	private static final long serialVersionUID = 231819815954473912L;
	public static int HOURS = Calendar.HOUR_OF_DAY;
	public static int MINUTES = Calendar.MINUTE;
	public static int SECONDS = Calendar.SECOND;
	public static int MILISECONDS = Calendar.MILLISECOND;
	
	private int hours = 0;
	private int minutes = 0;
	private int seconds = 0;
	private int milliseconds = 0;
	
	public Time(int hours, int minutes, int seconds, int milliseconds){
		this.hours = hours;
		this.minutes = minutes;
		this.seconds = seconds;
		this.milliseconds = milliseconds;
	}
	
	public Time(Calendar c){
		this.hours = c.get(Calendar.HOUR_OF_DAY);
		this.minutes = c.get(Calendar.MINUTE);
		this.seconds = c.get(Calendar.SECOND);
		this.milliseconds = c.get(Calendar.MILLISECOND);
	}
	
	public static Time getCurrentTime(){		
		return new Time(Calendar.getInstance());
	}

	public static Time add(Time t1, Time t2){
		return new Time(t1.get(Time.HOURS) + t2.get(Time.HOURS), 
				t1.get(Time.MINUTES) + t2.get(Time.MINUTES),
				t1.get(Time.SECONDS) + t2.get(Time.SECONDS),
				t1.get(Time.MILISECONDS) + t2.get(Time.MILISECONDS));
	}
	
	public static Time getTime(long minutes){		
		return new Time((int)minutes/60, (int)minutes % 60, 0, 0);
	}
	
	public static long getCurrentTimeInMilliseconds(){		
		return Calendar.getInstance().getTimeInMillis();
	}
	
	public int get(int unit){
		switch (unit){
		case (Calendar.HOUR_OF_DAY):
			return hours;
		case (Calendar.MINUTE):
			return minutes;
		case (Calendar.SECOND):
			return seconds;
		case (Calendar.MILLISECOND):
			return milliseconds;
		default:
			return -1;
		}
	}
	
	public String toString(){
		String string = this.hours + "," + this.minutes + "," + this.seconds + "," + this.milliseconds;
		return string;
	}
	
	public static Time stringToTime(String string){
		String[] elements = string.split(",");
		Time time = new Time(Integer.parseInt(elements[0]), 
				Integer.parseInt(elements[1]), Integer.parseInt(elements[2]), 
				Integer.parseInt(elements[3]));
		return time;
	}

	@Override
	public int compareTo(Time another) {
		if(this.hours > another.get(Time.HOURS)){
			return 1;
		}
		else if(this.hours < another.get(Time.HOURS)){
			return -1;
		}
		else{
			if(this.minutes > another.get(Time.MINUTES)){
				return 1;
			}
			else if(this.minutes < another.get(Time.MINUTES)){
				return -1;
			}
			else{

				if(this.seconds > another.get(Time.SECONDS)){
					return 1;
				}
				else if(this.seconds < another.get(Time.SECONDS)){
					return -1;
				}
				else{
					return 0;
				}
			}
		}
	}
	
}
