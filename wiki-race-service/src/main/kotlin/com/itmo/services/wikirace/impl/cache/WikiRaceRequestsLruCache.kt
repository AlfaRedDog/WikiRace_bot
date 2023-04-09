package com.itmo.services.wikirace.impl.cache

class WikiRaceRequestsLruCache(
    private val maxSize: Int,
    private val cache: LruCache<String, HashMap<String, List<String>>> =
        LruCache(maxSize)
) {


    fun put(titles: List<String>) {
        var tempTitles = titles
        while (tempTitles.size != 1) {
            val keyTitle = tempTitles.first()
            tempTitles = tempTitles.drop(1)
            val paths = HashMap<String, List<String>>()
            for (title in tempTitles)
                paths[title] = listOf(keyTitle) + tempTitles.dropLastWhile { it != title }

            cache.put(keyTitle, paths)
        }
    }

    fun delete(key: String): Boolean = cache.delete(key)

    fun reset() = cache.reset()

    fun size(): Long = cache.size()

    fun get(start: String, stop: String): List<String>? {
        val startValues = cache.get(start)
        if (startValues != null)
            return startValues[stop]

        return null
    }
}