package com.linemanwongnai.app.model

import com.google.gson.annotations.SerializedName

data class CoinListResponseModel(
    @SerializedName("status") var status: String,
    @SerializedName("data") var data: DataModel
)

data class DataModel(@SerializedName("coins") var coinList: List<CoinModel>)


data class CoinDetailResponseModel(
    @SerializedName("status") var status: String,
    @SerializedName("data") var data: CoinDetailModel
)

data class CoinDetailModel(@SerializedName("coin") var coinModel: CoinModel)
