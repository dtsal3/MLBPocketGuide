package edu.utap.mlbpocketguide.api

import com.google.gson.annotations.SerializedName
import org.json.JSONArray
import org.json.JSONObject

data class FangraphsStats (
    @SerializedName("playerInfo")
    val playerInfo: JSONObject,
    @SerializedName("teamInfo")
    val teamInfo: JSONObject,
    @SerializedName("data")
    val data: String,
    @SerializedName("fielding")
    val fielding: String,
    @SerializedName("positionProfile")
    val positionProfile: JSONObject,
    @SerializedName("prospect")
    val prospect: String,
    @SerializedName("news")
    val news: String,
)
