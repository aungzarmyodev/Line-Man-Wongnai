package com.linemanwongnai.app.data.network

import com.linemanwongnai.app.model.CoinDetailResponseModel
import com.linemanwongnai.app.model.CoinListResponseModel
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CoinApi {

    @GET("/v2/coins")
    suspend fun getCoinList(): CoinListResponseModel?

    @GET("/v2/coin/{uuid}")
    suspend fun getCoinDetail(@Path("uuid") uuid: String): CoinDetailResponseModel?

    @GET("/v2/coins")
    suspend fun searchCoin(@Query("search") keyword: String): CoinListResponseModel?

}