package com.itmo.services.wikirace.impl.cache

class LruCache<K, T>(val maxSize: Int) {

    private val internalCache: MutableMap<K, T> = object : LinkedHashMap<K, T>(0, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, T>?): Boolean {
            return size > maxSize
        }
    }

     fun put(key: K, value: T) = internalCache.put(key, value)

     fun delete(key: K): Boolean = internalCache.remove(key) != null

     fun reset() = internalCache.clear()

     fun get(key: K): T? = internalCache[key]

     fun size(): Long {
        return synchronized(this) {
            val snapshot = LinkedHashMap(internalCache)
            snapshot.size.toLong()
        }
    }
}