package com.linemanwongnai.app.model

import com.google.gson.annotations.SerializedName

class CoinModel(
    @SerializedName("name")
    var name: String?,
    @SerializedName("symbol")
    var symbol: String?,
    @SerializedName("price")
    var price: Double,
    @SerializedName("change")
    var change: String?,
    @SerializedName("rank")
    var rank: Int,
    @SerializedName("iconUrl")
    var iconUrl: String?
)