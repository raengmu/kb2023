package com.ymkwon.kb2023.search.mapper

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class MapperTest: FunSpec({
    test("map 1") {
        val json = "{\n" +
                "            \"blogname\": \"value blogname\",\n" +
                "            \"contents\": \"value contents\",\n" +
                "            \"datetime\": \"value datetime\",\n" +
                "            \"thumbnail\": \"value thumbnail\",\n" +
                "            \"title\": \"value title\",\n" +
                "            \"intval\": 10\n" +
                "        }"

        val objectMapper = jacksonObjectMapper()
        val m: Map<String, Any> = objectMapper.readValue(json, object : TypeReference<Map<String, Any>>(){})
        m["blogname"] shouldBe "value blogname"
        m["contents"] shouldBe "value contents"
        m["datetime"] shouldBe "value datetime"
        m["thumbnail"] shouldBe "value thumbnail"
        m["intval"] shouldBe 10
    }
})