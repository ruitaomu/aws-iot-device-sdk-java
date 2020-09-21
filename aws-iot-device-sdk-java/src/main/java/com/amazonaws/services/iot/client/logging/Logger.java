package com.amazonaws.services.iot.client.logging;

import java.util.Hashtable;

public class Logger {
	
	private final static Hashtable loggers = new Hashtable();
	
	private final static int DEFAULT_LOGGING_LEVEL = Level.WARNING;

	public synchronized static Logger getLogger(String name) {
		if (loggers.containsKey(name)) {
			return (Logger)loggers.get(name);
		}
		
		Logger logger = new Logger(name);
		logger.level = DEFAULT_LOGGING_LEVEL;
		loggers.put(name, logger);
		
		return logger;
	}

	private String name;
	
	private int level;

	protected Logger(String name) {
		this.name = name;
	}
	
	public void setLevel(int newLevel) {
		level = newLevel;
	}

	private void severe(String message, Throwable e) {
		if (level <= Level.SEVERE) {
			System.out.println("[CRIT]<"+name+">"+message);
			if (e != null) {
				e.printStackTrace();
			}
		}
	}

	private void info(String message, Throwable e) {
		if (level <= Level.INFO) {
			System.out.println("[INFO]<"+name+">"+message);
			if (e != null) {
				e.printStackTrace();
			}
		}
	}

	private void warning(String message, Throwable e) {
		if (level <= Level.WARNING) {
			System.out.println("[WARN]<"+name+">"+message);
			if (e != null) {
				e.printStackTrace();
			}
		}
	}
	
	private void config(String message, Throwable e) {
		if (level <= Level.CONFIG) {
			System.out.println("[CONF]<"+name+">"+message);
			if (e != null) {
				e.printStackTrace();
			}
		}
	}

	private void fine(String message, Throwable e) {
		if (level <= Level.FINE) {
			System.out.println("[FINE]<"+name+">"+message);
			if (e != null) {
				e.printStackTrace();
			}
		}
	}
	
	private void finer(String message, Throwable e) {
		if (level <= Level.FINER) {
			System.out.println("[FINER]<"+name+">"+message);
			if (e != null) {
				e.printStackTrace();
			}
		}
	}
	
	private void finest(String message, Throwable e) {
		if (level <= Level.FINEST) {
			System.out.println("[FINEST]<"+name+">"+message);
			if (e != null) {
				e.printStackTrace();
			}
		}
	}
	
	public void severe(String message) {
		severe(message, null);
	}

	public void info(String message) {
		info(message, null);
	}

	public void warning(String message) {
		warning(message, null);
	}
	
	public void config(String message) {
		config(message, null);
	}

	public void fine(String message) {
		fine(message, null);
	}
	
	public void finer(String message) {
		finer(message, null);
	}
	
	public void finest(String message) {
		finest(message, null);
	}

	
	public void log(int level, String message, Throwable e) {
		switch (level) {
		case Level.SEVERE:
			severe(message, e);
			break;
		case Level.WARNING:
			warning(message, e);
			break;
		case Level.INFO:
			info(message, e);
			break;
		case Level.CONFIG:
			config(message, e);
			break;
		case Level.FINE:
			fine(message, e);
			break;
		case Level.FINER:
			finer(message, e);
			break;
		case Level.FINEST:
			finest(message, e);
			break;
		}		
	}
}
