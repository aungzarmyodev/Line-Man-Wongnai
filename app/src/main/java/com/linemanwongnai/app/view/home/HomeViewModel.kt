package com.linemanwongnai.app.view.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linemanwongnai.app.data.repository.CoinRepository
import com.linemanwongnai.app.model.CoinModel
import com.linemanwongnai.app.model.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val coinRepository: CoinRepository) :
    ViewModel() {

    private var mutableLiveData = MutableLiveData<NetworkResult<List<CoinModel>>>()
    val liveData: LiveData<NetworkResult<List<CoinModel>>> = mutableLiveData

    init {
        mutableLiveData.postValue(NetworkResult.Loading())
        getCoinList()
    }

    fun getCoinList() {
        viewModelScope.launch {
            try {
                val result = coinRepository.getCoinList()
                if (!result?.data?.coinList.isNullOrEmpty()) {
                    mutableLiveData.postValue(NetworkResult.Success(result!!.data.coinList))
                } else {
                    mutableLiveData.postValue(NetworkResult.Success(emptyList()))
                }
            } catch (e: Exception) {
                mutableLiveData.postValue(NetworkResult.Error(e))
            }
        }
    }
}