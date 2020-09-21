package org.joshvm.java.util.concurrent;

import org.joshvm.java.util.*;

public class ConcurrentLinkedQueue extends AbstractQueue implements Collection, Queue {
	private ArrayList m_arlst;
	
	public ConcurrentLinkedQueue() {
		m_arlst = new ArrayList();
	}
	
	public ConcurrentLinkedQueue(Collection c) {
		m_arlst = new ArrayList(c);
	}

	public boolean add(Object element)	throws 	IllegalStateException, 
												ClassCastException, 
												NullPointerException, 
												IllegalArgumentException {
		return m_arlst.add(element);
	}
	
	public synchronized Object poll() {
		Object element;
		
		if (m_arlst.isEmpty()) {
			return null;
		} else {
			element = m_arlst.remove(0);
			return element;
		}
	}

	public synchronized Object peek() {
		Object element;
		
		if (m_arlst.isEmpty()) {
			return null;
		} else {
			element = m_arlst.get(0);
			return element;
		}
	}

	public Iterator iterator() {
		return m_arlst.iterator();
	}

	public int size() {
		return m_arlst.size();
	}

}
