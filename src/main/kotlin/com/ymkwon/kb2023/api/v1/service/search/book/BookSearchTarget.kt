package com.ymkwon.kb2023.api.v1.service.search.book

enum class BookSearchTarget(val code: Char)
{
    TITLE('T'),
    ISBN('I'),
    PUBLISHER('P'),
    PERSON('E')
}