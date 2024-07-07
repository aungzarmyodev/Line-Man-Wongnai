package com.linemanwongnai.app.model

import com.google.gson.annotations.SerializedName

data class ResponseData(
    @SerializedName("status") var status: String,
    @SerializedName("data") var data: DataModel
)

data class DataModel(@SerializedName("coins") var coinList: List<CoinModel>)
