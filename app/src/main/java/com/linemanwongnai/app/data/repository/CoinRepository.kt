package com.linemanwongnai.app.data.repository

interface CoinRepository {

    suspend fun getCoinList(): List<String>
}