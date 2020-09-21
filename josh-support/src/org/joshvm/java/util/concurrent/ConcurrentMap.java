package org.joshvm.java.util.concurrent;

import org.joshvm.java.util.Map;

public interface ConcurrentMap extends Map {
	public Object putIfAbsent(Object key, Object value);
}
