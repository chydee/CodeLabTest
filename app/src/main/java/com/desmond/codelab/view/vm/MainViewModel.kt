package com.desmond.codelab.view.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.desmond.codelab.core.SettingsContext
import com.desmond.codelab.data.repository.MainRepository
import com.desmond.codelab.domain.body.LoginBody
import com.desmond.codelab.domain.response.LoginResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: MainRepository, private val settingsContext: SettingsContext) : ViewModel() {
    private val viewModelJob = Job()
    private val coroutineScope: CoroutineScope = CoroutineScope(viewModelJob + Dispatchers.IO)

    private val _error: MutableLiveData<String> = MutableLiveData()
    val errorLiveData: LiveData<String>
        get() = _error

    private val _loginResponse: MutableLiveData<LoginResponse?> = MutableLiveData()
    val userLiveData: LiveData<LoginResponse?>
        get() = _loginResponse

    fun authenticateUser(body: LoginBody) {
        coroutineScope.launch {
            repository.authenticateUser(body).catch { handleError(it) }.collectLatest {
                if (it.isSuccessful) {
                    _loginResponse.postValue(it.body())
                } else {
                    _loginResponse.postValue(null)
                    _error.postValue(it.errorBody()?.string())
                }
            }
        }
    }

    private fun handleError(x: Throwable) {
        x.printStackTrace()
        when (x) {
            is HttpException -> {
                _error.postValue(x.message())
            }

            is SocketTimeoutException -> {
                _error.postValue("Server timeout! Try Again")
            }

            else -> _error.postValue(x.message ?: "Unknown error")
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}