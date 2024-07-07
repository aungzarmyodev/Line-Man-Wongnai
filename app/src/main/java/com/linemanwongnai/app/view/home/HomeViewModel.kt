package com.linemanwongnai.app.view.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linemanwongnai.app.data.repository.CoinRepository
import com.linemanwongnai.app.model.NetworkResult
import com.linemanwongnai.app.model.ResponseData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val coinRepository: CoinRepository) :
    ViewModel() {

    private var mutableLiveData = MutableLiveData<NetworkResult<ResponseData>>()
    val liveData: LiveData<NetworkResult<ResponseData>> = mutableLiveData

    init {
        getCoinList()
    }

    fun getCoinList() {
        viewModelScope.launch {
            try {
                val result = coinRepository.getCoinList()
                mutableLiveData.postValue(NetworkResult.Success(result))
            } catch (e: Exception) {
                mutableLiveData.postValue(NetworkResult.Error(e))
            }
        }
    }
}