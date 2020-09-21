package org.joshvm.java.util.concurrent;

import java.util.Vector;

import org.joshvm.java.util.AbstractSet;
import org.joshvm.java.util.HashMap;
import org.joshvm.java.util.Iterator;
import org.joshvm.java.util.Set;

public class ConcurrentHashMap extends HashMap implements ConcurrentMap {
	//////////////////////////////////////////////////////////////
	///// innere Klasse innerSet ////////////////////////////////////
	//////////////////////////////////////////////////////////////

	class ISyncSet extends AbstractSet implements org.joshvm.java.util.Set
	{

		Vector vec = null;

		public ISyncSet()
		{

			vec = new Vector();

		}

		public synchronized boolean add(Object o)
		{
			vec.addElement(o);
			return true;
		}

		public int size()
		{
			return vec.size();
		}

		public Iterator iterator()
		{
			return new ISyncIterator(vec);
		}
	}

	//////////////////////////////////////////////////////////////
	///// innere Klasse Iterator ////////////////////////////////////
	//////////////////////////////////////////////////////////////
	class ISyncIterator implements org.joshvm.java.util.Iterator
	{
		int index = 0;
		Vector vec = null;
		public ISyncIterator(Vector ve)
		{
			vec = ve;
		}

		public synchronized boolean hasNext()
		{
			if (vec.size() > index) return true;
			return false;
		}

		public synchronized Object next()
		{
			Object o = vec.elementAt(index);
			if (o==Nullobject) o=null;
			index++;
			return o;

		}

		public synchronized void remove()
		{
			index--;
			vec.removeElementAt(index);
		}

	}
	
	public Set entrySet()
	{
		ISyncSet s = new ISyncSet();
		return entrySet(s);
	}
	
	public synchronized Object putIfAbsent(Object key, Object value) {
		if (!m_HashTable.containsKey(key))
			return m_HashTable.put(key, value);
		else
			return m_HashTable.get(key);
	}

}
