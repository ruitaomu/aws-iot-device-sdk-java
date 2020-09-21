package com.amazonaws.services.iot.client.logging;

public class Logger {
	
	private static Logger logger = null;

	public synchronized static Logger getLogger(String name) {
		if (logger == null) {
			logger = new Logger(name);
		}
		return logger;
	}

	private String name;

	protected Logger(String name) {
		this.name = name;
	}

	public void info(String message) {
		System.out.println("[INFO]"+message);
	}

	public void warning(String message) {
		System.out.println("[WARN]"+message);
	}

	public void fine(String message) {
		System.out.println("[FINE]"+message);
	}

	public void log(int level, String message, Throwable e) {
		System.out.println("[Level"+level+"]"+message);
		System.out.println("<Stack>:");
		e.printStackTrace();
	}

	public void error(String message) {
		System.out.println("[CRIT]"+message);
	}
}
