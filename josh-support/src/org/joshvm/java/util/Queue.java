package org.joshvm.java.util;

import java.util.NoSuchElementException;

public interface Queue extends Collection {
	public boolean add(Object e) 
			throws IllegalStateException, ClassCastException, NullPointerException, IllegalArgumentException;
	public boolean offer(Object e)
			throws ClassCastException, NullPointerException, IllegalArgumentException;
	public Object poll();
	public Object element() throws NoSuchElementException;
	public Object peek();
}
