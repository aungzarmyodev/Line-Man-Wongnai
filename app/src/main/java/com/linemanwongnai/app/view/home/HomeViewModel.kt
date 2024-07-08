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

    private var coinDetailMutableLiveData = MutableLiveData<NetworkResult<CoinModel>>()
    val coinDetailLiveData: LiveData<NetworkResult<CoinModel>> = coinDetailMutableLiveData

    private var searchMutableLiveData = MutableLiveData<NetworkResult<List<CoinModel>>>()
    val searchLiveData: LiveData<NetworkResult<List<CoinModel>>> = searchMutableLiveData

    init {
        mutableLiveData.postValue(NetworkResult.Loading())
        getCoinList()
    }

    fun getCoinList() {
        viewModelScope.launch {
            try {
                val result = coinRepository.getCoinList()
                val coinList = result?.data?.coinList
                if (!coinList.isNullOrEmpty()) {
                    mutableLiveData.postValue(NetworkResult.Success(coinList))
                } else {
                    mutableLiveData.postValue(NetworkResult.Success(emptyList()))
                }
            } catch (e: Exception) {
                mutableLiveData.postValue(NetworkResult.Error(e))
            }
        }
    }

    fun getCoinDetail(uuid: String) {
        viewModelScope.launch {
            try {
                val result = coinRepository.getCoinDetail(uuid)
                val coinModel = result?.data?.coinModel
                if (coinModel != null) {
                    coinDetailMutableLiveData.postValue(NetworkResult.Success(coinModel))
                } else {
                    coinDetailMutableLiveData.postValue(NetworkResult.Success(null))
                }
            } catch (e: Exception) {
                coinDetailMutableLiveData.postValue(NetworkResult.Error(e))
            }
        }
    }

    fun search(keyword: String) {
        viewModelScope.launch {
            try {
                val result = coinRepository.searchCoin(keyword)
                val coinList = result?.data?.coinList
                if (!coinList.isNullOrEmpty()) {
                    searchMutableLiveData.postValue(NetworkResult.Success(coinList))
                } else {
                    searchMutableLiveData.postValue(NetworkResult.Success(emptyList()))
                }
            } catch (e: Exception) {
                searchMutableLiveData.postValue(NetworkResult.Error(e))
            }
        }
    }
}