package com.gov.gruhalakshmi.network

import com.gov.gruhalakshmi.ApplicationData
import com.gov.gruhalakshmi.SubmitData
import com.gov.gruhalakshmi.shared.K
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.*

interface AuthApi {

    @POST("api/GetOTPForGruhaLaksmiMobileOne")
    fun loginAsync(@Body value: Any): Deferred<Response<String>>

    @FormUrlEncoded
    @POST("api_login_check.php")
    fun townDetailsAsync(@Field("id") value: Any): Deferred<Response<String>>


    @POST("api/GetHSMURL")
    fun getHSMURL(@Body value: Any): Deferred<Response<String>>

    @POST("api/FetchCompiled")
    fun fetchCompiled(@Body value: Any): Deferred<Response<String>>

    @POST("api/GetOTP")
    fun validateOTP(@Body value: Any): Deferred<Response<String>>

    @POST("api/FetchRCData")
    fun fetchRcData(@Body value: Any): Deferred<Response<String>>

    @Headers("Accept: application/json")
    @POST("api/PostData")
    fun submitApplication(@Body value: SubmitData): Deferred<Response<String>>

}


class AuthRepository(private val api: AuthApi) : BaseRepository() {

    suspend fun callAPI(apicode: Int, data: Any): String? {
        return safeApiCall(
            call = {
                when (apicode) {
                    K.CALL_SEND_OTP -> api.loginAsync(data).await()
                    K.CALL_HSMURL -> api.getHSMURL(data).await()
                    K.CALL_FETCHCOMPLED -> api.fetchCompiled(data).await()
                    K.CALL_VALIDATEOTP -> api.validateOTP(data).await()
                    K.CALL_RCCHECK -> api.fetchRcData(data).await()

                    else -> api.townDetailsAsync(data).await()
                }
            },
            errorMessage = "Error Fetching Data"
        )
    }

    suspend fun callAPI(apicode: Int, data: SubmitData): String? {
        return safeApiCall(
            call = {
                when (apicode) {
                    K.CALL_SUBMIT -> api.submitApplication(data).await()


                    else -> api.townDetailsAsync(data).await()
                }
            },
            errorMessage = "Error Fetching Data"
        )
    }
}