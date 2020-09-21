package org.joshvm.java.util;

public class AtomicLong {
	private long value;
	
	public AtomicLong(long value) {
		this.value = value;
	}
	
	public synchronized void set(long value) {
		this.value = value;
	}
	
	public synchronized long get() {
		return value;
	}
	
	public synchronized final boolean compareAndSet(long expect, long update) {
		if (value == expect) {
			value = update;
			return true;
		} else {
			return false;
		}
	}
}
