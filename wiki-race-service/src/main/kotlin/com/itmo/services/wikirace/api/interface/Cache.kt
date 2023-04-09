package com.itmo.services.wikirace.api.`interface`

interface Cache<K, T> {
    fun put(key: K, value: T): T?
    fun delete(key: K): Boolean
    fun reset()
    fun get(key: K): T?
    fun size(): Long
}