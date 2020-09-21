package org.joshvm.java.util;

import java.util.Timer;
import java.util.TimerTask;

public class ScheduledThreadPool {

	public ScheduledThreadPool(int numOfClientThreads) {
		// TODO Auto-generated constructor stub
	}

	public Timer schedule(TimerTask runnable, long delay) {
		Timer timer = new Timer();
		timer.schedule(runnable, delay);
		return timer;
	}

	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

	public Timer scheduleAtFixedRate(TimerTask runnable, long initialDelay, long period) {
		Timer timer = new Timer();
		timer.schedule(runnable, initialDelay, period);
		return timer;
	}

}
