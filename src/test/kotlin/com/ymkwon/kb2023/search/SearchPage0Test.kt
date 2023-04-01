package com.ymkwon.kb2023.search

import com.ymkwon.kb2023.search.exception.SearchException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class SearchPage0Test: FunSpec({
    test("invalid parameters") {
        shouldThrow<SearchException> {
            SearchPage0(
                -1, 200, 30)
        }
        shouldThrow<SearchException> {
            SearchPage0(
                0, 0, 30)
        }
        shouldThrow<SearchException> {
            SearchPage0(
                0, 200, 0)
        }
    }

    test("valid parameters 0 44 21") {
        val p1 = SearchPage0(
            0, 44, 21
        )
        p1.page shouldBe 0
        p1.pageSize shouldBe 44
        p1.rowBegin shouldBe 0
        p1.rowEnd shouldBe 44
        p1.cacheRowBeginOffset shouldBe 0
        p1.cacheRowEndOffset shouldBe 2
        p1.cachePageBegin shouldBe 0
        p1.cachePageEnd shouldBe 3
        p1.cachePageCount shouldBe 3
    }

    test("valid parameters 57 17 23") {
        val p1 = SearchPage0(
            57, 17, 23
        )
        p1.page shouldBe 57
        p1.pageSize shouldBe 17
        p1.rowBegin shouldBe 57*17
        p1.rowEnd shouldBe 57*17+17
        p1.cacheRowBeginOffset shouldBe 3
        p1.cacheRowEndOffset shouldBe 20
        p1.cachePageBegin shouldBe 42
        p1.cachePageEnd shouldBe 43
        p1.cachePageCount shouldBe 1
    }

    test("valid parameters 2 23 17") {
        val p1 = SearchPage0(
            2, 23, 17
        )
        p1.page shouldBe 2
        p1.pageSize shouldBe 23
        p1.rowBegin shouldBe 46
        p1.rowEnd shouldBe 46+23
        p1.cacheRowBeginOffset shouldBe 12
        p1.cacheRowEndOffset shouldBe 1
        p1.cachePageBegin shouldBe 2
        p1.cachePageEnd shouldBe 5
        p1.cachePageCount shouldBe 3
    }

    test("valid parameters 3 30 60") {
        val p1 = SearchPage0(
            3, 30, 60
        )
        p1.page shouldBe 3
        p1.pageSize shouldBe 30
        p1.rowBegin shouldBe 90
        p1.rowEnd shouldBe 90+30
        p1.cacheRowBeginOffset shouldBe 30
        p1.cacheRowEndOffset shouldBe 60
        p1.cachePageBegin shouldBe 1
        p1.cachePageEnd shouldBe 2
        p1.cachePageCount shouldBe 1
    }
})