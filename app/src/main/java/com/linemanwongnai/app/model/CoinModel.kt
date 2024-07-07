package com.linemanwongnai.app.model

import com.google.gson.annotations.SerializedName

class CoinModel(
    @SerializedName("uuid")
    var uuid: String,
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
    var iconUrl: String?,
    @SerializedName("description")
    var description: String?,
    @SerializedName("marketCap")
    var marketCap: String?,
    @SerializedName("websiteUrl")
    var websiteUrl: String?,
    @SerializedName("color")
    var textColor: String?
)