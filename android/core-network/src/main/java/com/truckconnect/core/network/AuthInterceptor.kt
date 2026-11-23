package com.truckconnect.core.network

import okhttp3.Interceptor
import okhttp3.Response
import java.util.UUID

class AuthInterceptor(
    private val tokenProvider: () -> String?
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val builder = original.newBuilder()

        // Attach JWT Bearer Token if present
        tokenProvider()?.let { token ->
            if (token.isNotBlank()) {
                builder.header("Authorization", "Bearer $token")
            }
        }

        // Attach unique Client Request ID for write idempotency if not present
        if (original.header("client-request-id") == null && original.method != "GET") {
            builder.header("client-request-id", UUID.randomUUID().toString())
        }

        return chain.proceed(builder.build())
    }
}
