package edu.utap.mlbpocketguide.api

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.IOException

class FangraphsAPI {

    // FanGraphs returns a different body for pitchers vs position players, so RetroFit isn't a very good use-case
    // Because I couldn't really figure out the dynamic api response in a way that wouldn't cause my app to crash
    // https://stackoverflow.com/questions/71198581/using-okhttp-for-simple-get-request-in-kotlin-helper-class
    fun getStats(playerId: String, position: String) : FangraphsStats {
        val newUrl = url + "&playerId=%s&position=%s".format(playerId,position)
        Log.d("Tracing", "In getStats")
        Log.d("Tracing", "Our URL is %s".format(newUrl))
        val request = Request.Builder().url(newUrl).build()

        try {
            Log.d("Tracing", "Trying to get stats")
            client.newCall(request).execute()
                .use {
                        response -> return toPlayerStats(JSONObject(response.body?.string()), position)
                }
        } catch (e: IOException) {
            Log.d("FangraphsAPI", "Caught an exception trying to get the stats from FanGraphs")
            // return an empty stats object to satisfy compiler, we will check if its populated before setting later
            return FangraphsStats(
                    mutableMapOf<String, Long>(),
                    mutableMapOf<String, String>(),
                    mutableMapOf<String, Int>(),
                    mutableMapOf<String, String>()
                )
        }

    }

    // Map our JSON response to the inputs we actually want in our data structure
    fun toPlayerStats(player: JSONObject, position: String): FangraphsStats {

        // Initialize our objects
        val comparisonStats = mutableMapOf<String, Long>()
        val profileCharacteristics = mutableMapOf<String, String>()
        val profileStatsCounting = mutableMapOf<String, Int>()
        val profileStatsAvg = mutableMapOf<String, String>()

        // Profile Characteristics: Age, Position, Hit, Throw
        val playerInfoObject = player.getJSONObject("playerInfo")
        profileCharacteristics["playerAge"] = playerInfoObject.getString("Age")
        profileCharacteristics["playerPosition"] = playerInfoObject.getString("Position")
        profileCharacteristics["playerHit"] = playerInfoObject.getString("Bats")
        profileCharacteristics["playerThrow"] = playerInfoObject.getString("Throws")

        // Profile Stats: PieChart (Hitter) - PA, HR, Non-HR Hits, BB, SO, Non-SO Outs
        // Profile Stats: PieChart (Pitcher) - TBF, HR, Non-HR Hits, BB, SO, Non-SO Outs
        val dataObject = player.getJSONArray("data")
        val hr = dataObject.getJSONObject(0).getInt("HR")
        val h = dataObject.getJSONObject(0).getInt("H")
        val bb = dataObject.getJSONObject(0).getInt("BB")
        val ibb = dataObject.getJSONObject(0).getInt("IBB")
        val hbp = dataObject.getJSONObject(0).getInt("HBP")
        val so = dataObject.getJSONObject(0).getInt("SO")
        val total = when (position) {
            "P" -> dataObject.getJSONObject(0).getInt("TBF")
            else -> dataObject.getJSONObject(0).getInt("PA")
        }

        profileStatsCounting["TOTAL"] = total
        profileStatsCounting["HR"] = hr
        profileStatsCounting["RemHITS"] = h - hr
        profileStatsCounting["BB"] = bb + ibb + hbp
        profileStatsCounting["SO"] = so
        profileStatsCounting["OUTS"] = total - so - h - bb - ibb - hbp

        // Comparison Stats: BA, K%, BB%, GB%, Pull%, Hard%, wFB/c, wOther/c, Contact%
        val avg = dataObject.getJSONObject(0).getLong("AVG")
        val kp = dataObject.getJSONObject(0).getLong("K%")
        val bbp = dataObject.getJSONObject(0).getLong("BB%")
        val gbp = dataObject.getJSONObject(0).getLong("GB%")
        val pp = dataObject.getJSONObject(0).getLong("Pull%")
        val hp = dataObject.getJSONObject(0).getLong("Hard%")
        val valFB = dataObject.getJSONObject(0).getLong("wFB/C")
        val valOther = dataObject.getJSONObject(0).getLong("wSL/C")
                        + dataObject.getJSONObject(0).getLong("wCT/C")
                        + dataObject.getJSONObject(0).getLong("wCB/C")
                        + dataObject.getJSONObject(0).getLong("wCH/C")
                        + dataObject.getJSONObject(0).getLong("wSF/C")
        val contactP = dataObject.getJSONObject(0).getLong("Contact%")

        comparisonStats["AVG"] = avg
        comparisonStats["KP"] = kp
        comparisonStats["BBP"] = bbp
        comparisonStats["GBP"] = gbp
        comparisonStats["PullP"] = pp
        comparisonStats["HardP"] = hp
        comparisonStats["valFB"] = valFB
        comparisonStats["valOTHER"] = valOther
        comparisonStats["ContactP"] = contactP

        // Profile Stats: Graph - Batting Average,


        return FangraphsStats(comparisonStats, profileCharacteristics, profileStatsCounting, profileStatsAvg)
    }

    companion object {
        private val client = OkHttpClient()
        private val url = "https://www.fangraphs.com/api/players/stats/common?season=2022"
    }
}
