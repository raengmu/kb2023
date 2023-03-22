package com.ymkwon.kb2023.api.v1.service.search.jpa

import com.ymkwon.kb2023.search.jpa.JpaSearchQueryCountAccumulator
import mu.KotlinLogging
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class JpaAsyncSearchQueryCountAccumulator(
    private val queryCountAccumulator: JpaSearchQueryCountAccumulator
) {
    @Async("taskExecutor")
    fun searchQueryAccumulate(query: String) {
        queryCountAccumulator.update(query, 1)
    }
}