package com.ymkwon.kb2023.api.v1.service.search

import java.util.function.Consumer

class SearchQueryCountAggregator {
    companion object {
        private val counts = HashMap<String, MutableMap<String, Long>>()

        @Synchronized
        fun increment(category: String, query: String) {
            val queryCounts = counts.getOrDefault(category, HashMap())
            if (queryCounts.isEmpty())
                counts[category] = queryCounts
            queryCounts[query] = 1 + queryCounts.getOrDefault(query, 0)
        }

        fun commit(consumer: Consumer<Map<String, Map<String, Long>>>) {
            val countsCloned = getCountsClone()
            if (countsCloned.isNotEmpty())
                consumer.accept(countsCloned)
        }

        @Synchronized
        private fun getCountsClone(): Map<String, Map<String, Long>> {
            val countsClone = counts.toMap()
            counts.clear()
            return countsClone
        }
    }
}