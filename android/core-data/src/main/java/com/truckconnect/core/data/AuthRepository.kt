package com.truckconnect.core.data

import retrofit2.Response
import com.truckconnect.core.network.NetworkResult
import com.truckconnect.core.network.api.AuthApiService
import com.truckconnect.core.network.model.AdminLoginRequest
import com.truckconnect.core.network.model.AuthResponse
import com.truckconnect.core.network.model.DriverLoginRequest
import com.truckconnect.core.network.model.DriverRegisterRequest
import com.truckconnect.core.network.model.RegisterResponse
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApiService: AuthApiService,
    private val tokenManager: TokenManager
) {
    suspend fun loginAdmin(email: String, pass: String): NetworkResult<AuthResponse> {
        return try {
            val response = authApiService.loginAdmin(
                AdminLoginRequest(email = email, password = pass),
                requestId = UUID.randomUUID().toString()
            )
            if (response.isSuccessful && response.body() != null) {
                val auth = response.body()!!
                tokenManager.saveTokens(auth.accessToken, auth.refreshToken)
                NetworkResult.Success(auth)
            } else {
                NetworkResult.Error(response.code(), response.message() ?: "Authentication failed")
            }
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    suspend fun loginDriver(phone: String, pin: String): NetworkResult<AuthResponse> {
        return try {
            val response = authApiService.loginDriver(
                DriverLoginRequest(phone = phone, pin = pin),
                requestId = UUID.randomUUID().toString()
            )
            if (response.isSuccessful && response.body() != null) {
                val auth = response.body()!!
                tokenManager.saveTokens(auth.accessToken, auth.refreshToken)
                NetworkResult.Success(auth)
            } else {
                NetworkResult.Error(response.code(), response.message() ?: "Driver login failed")
            }
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    suspend fun registerDriver(req: DriverRegisterRequest): NetworkResult<RegisterResponse> {
        return try {
            val response = authApiService.registerDriver(
                req,
                requestId = UUID.randomUUID().toString()
            )
            if (response.isSuccessful && response.body() != null) {
                NetworkResult.Success(response.body()!!)
            } else {
                NetworkResult.Error(response.code(), response.message() ?: "Registration failed")
            }
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    fun isLoggedIn(): Boolean = !tokenManager.getAccessToken().isNullOrBlank()

    fun logout() {
        tokenManager.clearTokens()
    }
}
