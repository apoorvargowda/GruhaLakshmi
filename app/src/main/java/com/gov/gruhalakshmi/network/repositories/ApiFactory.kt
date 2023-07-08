package com.gov.gruhalakshmi.network.repositories

import com.gov.gruhalakshmi.network.AuthApi
import com.gov.gruhalakshmi.shared.KCredential


object ApiFactory {

    val authApi = RetrofitFactory.retrofit(KCredential.Url).create(AuthApi::class.java)

}