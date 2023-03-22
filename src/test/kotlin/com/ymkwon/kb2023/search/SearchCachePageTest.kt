package com.ymkwon.kb2023.search

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class SearchCachePageTest: FunSpec({
    test("comparator") {
        val cp1 = SearchCachePage(1, "1")
        val cp2 = SearchCachePage(2, "1")
        val cp3 = SearchCachePage(1, "1")

        (cp1 < cp2) shouldBe true
        (cp1 > cp2) shouldBe false

        (cp1 < cp3) shouldBe false
        (cp1 > cp3) shouldBe false
    }
})