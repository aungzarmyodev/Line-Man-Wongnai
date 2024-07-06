package com.linemanwongnai.app.data.repository

import com.linemanwongnai.app.data.network.CoinApi
import javax.inject.Inject

class CoinRepositoryImpl @Inject constructor(private val coinApi: CoinApi) : CoinRepository {

    override suspend fun getCoinList(): List<String> {
        return coinApi.getCoinList()
    }
}