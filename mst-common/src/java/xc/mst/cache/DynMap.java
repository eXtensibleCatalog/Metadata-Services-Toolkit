package xc.mst.cache;

import xc.mst.utils.TimingLogger;
import gnu.trove.TLongLongHashMap;
import gnu.trove.TObjectLongHashMap;

public class DynMap {
	
	protected TObjectLongHashMap stringKeyedMap = new TObjectLongHashMap();
	protected TLongLongHashMap longKeyedMap = new TLongLongHashMap();
	
	public void put(String k, Long value) {
		try {
			Long kl = Long.parseLong(k);
			longKeyedMap.put(kl, value);
			TimingLogger.add("dyncache.long", 0);
		} catch (NumberFormatException nfe) {
			TimingLogger.add("dyncache.string", 0);
			stringKeyedMap.put(k, value);
		}
	}
	
	public Long getLong(String k) {
		try {
			Long bibMarcId = Long.parseLong(k);
			long l = longKeyedMap.get(bibMarcId);
			if (l == 0) {
				return null;
			} else {
				return (Long)l;
			}
		} catch (NumberFormatException nfe) {
			return stringKeyedMap.get(k);
		}
	}
	
	public void clear() {
		stringKeyedMap.clear();
		longKeyedMap.clear();
	}

}
