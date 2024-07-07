package com.linemanwongnai.app.data.network
import com.linemanwongnai.app.model.ResponseData
import retrofit2.http.GET

interface CoinApi {

    @GET("/v2/coins")
    suspend fun getCoinList() : ResponseData
}