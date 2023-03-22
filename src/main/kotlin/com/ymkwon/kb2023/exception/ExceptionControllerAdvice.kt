package com.ymkwon.kb2023.exception

import com.ymkwon.kb2023.common.CustomHttpResponse
import com.ymkwon.kb2023.search.exception.SearchException
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MissingRequestHeaderException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestController

private val logger = KotlinLogging.logger {}

@ControllerAdvice(annotations = [RestController::class])
class ExceptionControllerAdvice {
    private fun responseEntity(
        exCode: CommonExceptionCode
    ): ResponseEntity<CustomHttpResponse> =
        responseEntity(exCode.status, exCode.code, exCode.message)

    private fun responseEntity(
        status: HttpStatus,
        code: Int,
        message: String
    ): ResponseEntity<CustomHttpResponse> {
        val httpResponse = CustomHttpResponse(
            status = status.value(),
            code = code,
            message = message
        )
        logger.info { "Exception: status(${httpResponse.status}) code(${httpResponse.code}) message(${httpResponse.message})" }
        return ResponseEntity(httpResponse, status)
    }

    // invalid parameters
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(ex: HttpMessageNotReadableException): ResponseEntity<CustomHttpResponse> {
        logger.error { "Exception: HttpMessageNotReadableException" }
        return responseEntity(CommonExceptionCode.INVALID_PARAMETER)
    }

    @ExceptionHandler(MissingRequestHeaderException::class)
    fun handleBadRequestException(ex: MissingRequestHeaderException): ResponseEntity<CustomHttpResponse> {
        logger.error { "Exception: MissingRequestHeaderException" }
        return responseEntity(CommonExceptionCode.MISSING_HEADER)
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleBadRequestException(ex: MissingServletRequestParameterException): ResponseEntity<CustomHttpResponse> {
        logger.error { "Exception: MissingServletRequestParameterException : ${ex.parameterName} missing" }
        return responseEntity(CommonExceptionCode.INVALID_PARAMETER)
    }

    @ExceptionHandler(InternalError::class)
    fun handleInternalErrorException(ex: InternalError): ResponseEntity<CustomHttpResponse> {
        logger.error { "Exception: InternalError" }
        return responseEntity(CommonExceptionCode.INTERNAL_ERROR)
    }

    @ExceptionHandler(CommonException::class)
    fun handleCommonException(ex: CommonException): ResponseEntity<CustomHttpResponse> {
        logger.error { "Exception: SearchException - "+
                "${ex.code}:${ex.message} - ${ex.context}" }
        return responseEntity(ex.status, ex.code, ex.message)
    }

    @ExceptionHandler(SearchException::class)
    fun handleSearchException(ex: SearchException): ResponseEntity<CustomHttpResponse> {
        val convertedCode = CommonExceptionCode.getSearchExceptionCode(ex.code)
        logger.error { "Exception: SearchException - "+
                       "${convertedCode}(${ex.code}):${ex.message} - ${ex.context}" }
        return responseEntity(ex.status, convertedCode, ex.message)
    }

}