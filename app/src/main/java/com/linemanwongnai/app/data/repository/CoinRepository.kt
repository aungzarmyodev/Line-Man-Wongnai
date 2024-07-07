package com.linemanwongnai.app.data.repository

import com.linemanwongnai.app.data.network.CoinApi
import com.linemanwongnai.app.model.ResponseData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CoinRepository @Inject constructor(private val coinApi: CoinApi) {

    suspend fun getCoinList(): ResponseData? {
        return withContext(Dispatchers.IO) {
            coinApi.getCoinList()
        }
    }
}