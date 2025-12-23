package com.gorogoro.notification.exception

import org.springframework.http.HttpStatus

class BusinessException private constructor(
    val httpStatus: HttpStatus,
    override val message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause) {

    companion object {
        fun builder(errorCode: ErrorCode): Builder {
            return Builder(errorCode)
        }
    }

    class Builder(private val errorCode: ErrorCode) {
        private val params = mutableListOf<Any>()
        private var cause: Throwable? = null

        fun withId(vararg ids: Long): Builder {
            params.addAll(ids.toList())
            return this
        }

        fun withField(vararg fields: String): Builder {
            params.addAll(fields.toList())
            return this
        }

        fun withCount(count: Int): Builder {
            params.add(count)
            return this
        }

        fun withCause(cause: Throwable): Builder {
            this.cause = cause
            return this
        }

        fun build(): BusinessException {
            val finalMessage = if (params.isEmpty()) {
                errorCode.message
            } else {
                String.format(errorCode.message, *params.toTypedArray())
            }

            return BusinessException(
                httpStatus = errorCode.status,
                message = finalMessage,
                cause = cause
            )
        }
    }
}
