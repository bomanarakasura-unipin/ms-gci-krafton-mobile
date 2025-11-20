package com.unipin.gci-parent-name.exception

import com.unipin.gci-parent-name.model.response.DataResponse
import com.unipin.gci-parent-name.configuration.variable.ErrorCode
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {
    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(GameProviderException::class)
    fun handleGameProviderError(ex: GameProviderException): ResponseEntity<*> {
        log.error("Game provider error: {}", ex.message)
        return ResponseEntity.ok(
            DataResponse(
                0, mapOf(
                    "errorCode" to ex.errorCode, "errorMsg" to ex.message
                )
            )
        )
    }

    @ExceptionHandler(GenericException::class)
    fun handleGenericException(ex: GenericException): ResponseEntity<*> {
        log.error("{} Exception occurred: {}", ex.ctx, ex.message)
        return ResponseEntity.ok(
            DataResponse(
                0, mapOf(
                    "errorCode" to ex.errorCode, "errorMsg" to (ex.message ?: "An unexpected error occurred")
                )
            )
        )
    }

    @ExceptionHandler(Exception::class)
    fun unknownExceptionHandler(ex: RuntimeException): ResponseEntity<*> {
        log.error("Unhandled System Error: {}", ex.message, ex)
        return ResponseEntity.ok(
            DataResponse(
                status = 0, data = mapOf(
                    "errorCode" to ErrorCode.UNKNOWN_ERROR, "errorMsg" to "An unexpected error occurred"
                )
            )
        )
    }

}

class GameProviderException(override val message: String, val errorCode: ErrorCode) : RuntimeException(message)
class GenericException(val ctx: String, val errorCode: ErrorCode, override val message: String?) :
    RuntimeException(message)
