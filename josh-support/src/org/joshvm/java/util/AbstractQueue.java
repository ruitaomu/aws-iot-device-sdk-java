package org.joshvm.java.util;

import java.util.NoSuchElementException;

public abstract class AbstractQueue extends AbstractCollection
		implements Queue {
	protected AbstractQueue() {}
	
	public boolean add(Object element)	throws 	IllegalStateException, 
												ClassCastException, 
												NullPointerException, 
												IllegalArgumentException {
		throw new RuntimeException();
	}
	
	public boolean addAll(Collection collection) throws IllegalStateException, 
													 ClassCastException, 
													 NullPointerException, 
													 IllegalArgumentException {
		Iterator it = collection.iterator();
		
		if (it == null || it == iterator()) {
			throw new IllegalArgumentException();
		}
		
		while (it.hasNext()) {
			add(it.next());
		}
		
		return true;
	}
	
	public void clear() {
		while (poll() != null);
	}
	
	public Object element() throws NoSuchElementException {
		Object o;
		o = peek();
		if (o == null) {
			throw new NoSuchElementException();
		}
		return o;
	}
	
	public Object remove() throws NoSuchElementException {
		Object o;
		o = poll();
		if (o == null) {
			throw new NoSuchElementException();
		}
		return o;
	}
	
	public boolean offer(Object e) throws ClassCastException, 
										NullPointerException, 
										IllegalArgumentException {
		try {
			return add(e);
		} catch (IllegalStateException ex) {
			return false;
		}
	}
}
