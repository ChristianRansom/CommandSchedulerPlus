package muttsworld.dev.team.CommandSchedulerPlus;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class RepeatTime implements Serializable {

	private static final long serialVersionUID = 1L;
	private int days;
	private int hours;
	private int minutes;
	private int seconds;
	private long millis;
	
	
	public RepeatTime(int theDays, int theHours, int theMinutes, int theSeconds) {
		super();
		this.days = theDays;
		this.hours = theHours;
		this.minutes = theMinutes; 
		this.seconds = theSeconds;
		updateMillis();
	}


	private void updateMillis() {
		millis = 0l;
        millis += TimeUnit.DAYS.toMillis(days);
        millis += TimeUnit.HOURS.toMillis(hours);
        millis += TimeUnit.MINUTES.toMillis(minutes);
        millis += TimeUnit.SECONDS.toMillis(seconds);
	}
	
	public int getMinutes() {
		return minutes;
	}


	public void setMinutes(int minutes) {
		updateMillis();
		this.minutes = minutes;
	}


	public long getMillis() {
		return this.getMiliseconds();
	}


	public void setMillis(long millis) {
		this.millis = millis;
		updateNonMillis();
	}
	
	public int getDays() {
		return days;
	}
	public void setDays(int days) {
		this.days = days;
		updateMillis();
	}
	public int getHours() {
		return hours;
	}
	public void setHours(int hours) {
		this.hours = hours;
		updateMillis();
	}
	public int getSeconds() {
		return seconds;
	}
	public void setSeconds(int seconds) {
		this.seconds = seconds;
		updateMillis();
	}
	public long getMiliseconds() {
		return millis;
	}
	public void setMiliseconds(long miliseconds) {
		this.millis = miliseconds;
		updateNonMillis();
	}


	private void updateNonMillis() {
		long millisCopy = millis;
		days = (int) TimeUnit.MILLISECONDS.toDays(millisCopy);
		millisCopy -= TimeUnit.DAYS.toMillis(days);
        hours = (int) TimeUnit.MILLISECONDS.toHours(millisCopy);
        millisCopy -= TimeUnit.HOURS.toMillis(hours);
        minutes = (int) TimeUnit.MILLISECONDS.toMinutes(millisCopy);
        millisCopy -= TimeUnit.MINUTES.toMillis(minutes);
        seconds = (int) TimeUnit.MILLISECONDS.toSeconds(millisCopy);
	}

	public boolean isZero() {
		System.out.println("Repeat millis = " + millis);
		return (millis == 0);
	}
	
	@Override
	public String toString() {
        StringBuilder sb = new StringBuilder(64);
        
        if(days != 0){
            sb.append(days);
            sb.append(" Days ");
        }
        if(hours != 0) {
            sb.append(hours);
            sb.append(" Hours ");	
        }
        if(minutes != 0){
            sb.append(minutes);
            sb.append(" Minutes ");	
        }
        if(seconds != 0){
        	sb.append(seconds);
        	sb.append(" Seconds ");
        }
		return sb.toString();
	}


	public int compareTo(RepeatTime repeat) {
		if(this.millis > repeat.millis)
			return 1;
		else if(this.millis > repeat.millis){
			return -1;
		}
		else {
			return 0;
		}
	}
	
}
