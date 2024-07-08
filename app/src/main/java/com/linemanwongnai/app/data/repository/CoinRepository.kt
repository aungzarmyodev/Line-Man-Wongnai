package com.linemanwongnai.app.data.repository

import com.linemanwongnai.app.data.network.CoinApi
import com.linemanwongnai.app.model.CoinDetailResponseModel
import com.linemanwongnai.app.model.CoinListResponseModel
import com.linemanwongnai.app.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CoinRepository @Inject constructor(private val coinApi: CoinApi) {

    suspend fun getCoinList(): CoinListResponseModel? {
        return withContext(Dispatchers.IO) {
            coinApi.getCoinList(Utils.LIMIT)
        }
    }

    suspend fun getCoinDetail(uuid: String): CoinDetailResponseModel? {
        return withContext(Dispatchers.IO) {
            coinApi.getCoinDetail(uuid)
        }
    }

    suspend fun searchCoin(keyword: String): CoinListResponseModel? {
        return withContext(Dispatchers.IO) {
            coinApi.searchCoin(keyword, Utils.SEARCH_LIMIT)
        }
    }
}