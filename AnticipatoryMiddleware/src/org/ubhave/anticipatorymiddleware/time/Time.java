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
package org.ubhave.anticipatorymiddleware.time;

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
		Time t = new Time(t1.get(Time.HOURS) + t2.get(Time.HOURS), 
				t1.get(Time.MINUTES) + t2.get(Time.MINUTES),
				t1.get(Time.SECONDS) + t2.get(Time.SECONDS),
				t1.get(Time.MILISECONDS) + t2.get(Time.MILISECONDS));
		if(t.get(Time.MILISECONDS) > 1000)
		{
			int s = t.get(Time.SECONDS);
			int ms = t.get(Time.MILISECONDS);
			s = s + ms/1000;
			ms = ms - (ms/1000 * 1000);
			t.seconds = s;
			t.milliseconds = ms;
		}
		if(t.get(Time.SECONDS) > 60)
		{
			int s = t.get(Time.SECONDS);
			int mins = t.get(Time.MINUTES);
			mins = mins + s/60;
			s = s - (s/60 * 60);
			t.seconds = s;
			t.minutes = mins;
		}
		if(t.get(Time.MINUTES) > 60)
		{
			int h = t.get(Time.HOURS);
			int mins = t.get(Time.MINUTES);
			h = h + mins/60;
			mins = mins - (mins/60 * 60);
			t.minutes = mins;
			t.hours = h;
		}
		return t;
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
