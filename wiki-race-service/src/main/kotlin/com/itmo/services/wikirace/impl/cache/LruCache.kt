package com.itmo.services.wikirace.impl.cache

import com.itmo.services.wikirace.api.`interface`.Cache

class LruCache<K, T>(val maxSize: Int): Cache<K, T> {

    private val internalCache: MutableMap<K, T> = object : LinkedHashMap<K, T>(0, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, T>?): Boolean {
            return size > maxSize
        }
    }

    override fun put(key: K, value: T) = internalCache.put(key, value)

    override fun delete(key: K): Boolean = internalCache.remove(key) != null

    override fun reset() = internalCache.clear()

    override fun get(key: K): T? = internalCache[key]

    override fun size(): Long {
        return synchronized(this) {
            val snapshot = LinkedHashMap(internalCache)
            snapshot.size.toLong()
        }
    }
}