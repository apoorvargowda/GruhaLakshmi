package com.gov.gruhalakshmi.viewModel

import androidx.annotation.UiThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import com.gov.gruhalakshmi.ApplicationData
import com.gov.gruhalakshmi.SubmitData
import com.gov.gruhalakshmi.network.AuthRepository
import com.gov.gruhalakshmi.network.repositories.ApiFactory
import com.gov.gruhalakshmi.network.repositories.Resource
import com.gov.gruhalakshmi.shared.extension.Coroutines
import org.json.JSONArray

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository(ApiFactory.authApi)

    private val _result = MutableLiveData<Resource<String>>()

    val result: LiveData<Resource<String>> get() = _result

    @UiThread
    fun callAPI(apiCode:Int, data: JsonObject,) {
        // _loading.value = true
        Coroutines.ioThenMain({
            repository.callAPI(apiCode, data)
        }) {
            if(it == null){
                _result.value = Resource.error("Network Error", null)
            }
            it?.let {
                //_loading.value = false
                if (!it.contains("Incorrect LoginID or LoginID not found",true)) {
                    _result.value = Resource.success(it)
                } else {
                    _result.value = Resource.error(it, null)
                }
            }
        }
    }

    @UiThread
    fun callAPI(apiCode:Int, data: SubmitData,) {
        // _loading.value = true
        Coroutines.ioThenMain({
            repository.callAPI(apiCode, data)
        }) {
            if(it == null){
                _result.value = Resource.error("Network Error", null)
            }
            it?.let {
                //_loading.value = false
                if (!it.contains("Incorrect LoginID or LoginID not found",true)) {
                    _result.value = Resource.success(it)
                } else {
                    _result.value = Resource.error(it, null)
                }
            }
        }
    }
}