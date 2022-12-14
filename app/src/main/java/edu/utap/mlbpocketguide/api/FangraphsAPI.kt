package edu.utap.mlbpocketguide.api

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

class FangraphsAPI {

    // FanGraphs returns a different body for pitchers vs position players, so RetroFit isn't a very good use-case
    // Because I couldn't really figure out the dynamic api response in a way that wouldn't cause my app to crash
    // https://stackoverflow.com/questions/71198581/using-okhttp-for-simple-get-request-in-kotlin-helper-class
    fun getStats(playerId: String, position: String) : FangraphsStats {
        val newUrl = url + "?playerId=%s&position=%s".format(playerId,position)
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
                    mutableMapOf(),
                    mutableMapOf(),
                    mutableMapOf(),
                    mutableMapOf()
                )
        }

    }

    // Map our JSON response to the inputs we actually want in our data structure
    private fun toPlayerStats(player: JSONObject, position: String): FangraphsStats {

        // Initialize our objects
        val comparisonStats = mutableMapOf<String, Double>()
        val profileCharacteristics = mutableMapOf<String, String>()
        val profileStatsCounting = mutableMapOf<String, Int>()
        val profileStatsAvg = mutableMapOf<String, ArrayList<Pair<Int, Double>>>()
        val dataObject = player.getJSONArray("data")
        var dataCurrentSeason = JSONObject()

        // build our season-chart data entries while we find the career totals
        // Profile Stats: Graph - Batting Average, Slugging, ERA, FIP
        val eraList = ArrayList<Pair<Int, Double>>()
        val fipList = ArrayList<Pair<Int, Double>>()
        val avgList = ArrayList<Pair<Int, Double>>()
        val slgList = ArrayList<Pair<Int, Double>>()

        for (i in 0 until dataObject.length()) {
            val currentObject = dataObject.getJSONObject(i)
            if (currentObject["aseason"] != 0 && currentObject["type"] == 0 ) {
                Log.d("TracingStats", "Do entry logic for charts")
                if (position == "P") {
                    Log.d("TracingStats", "We are adding to our ERA lists for pitchers")
                    eraList.add(Pair(currentObject.getInt("aseason"), currentObject.getDouble("ERA")))
                    fipList.add(Pair(currentObject.getInt("aseason"), currentObject.getDouble("FIP")))
                } else {
                    avgList.add(Pair(currentObject.getInt("aseason"), currentObject.getDouble("AVG")))
                    slgList.add(Pair(currentObject.getInt("aseason"), currentObject.getDouble("SLG")))
                }
            }
            if (currentObject["aseason"] == 0 && currentObject["type"] == -1) {
                dataCurrentSeason = currentObject
            }
        }
        profileStatsAvg["ERA"] = eraList
        profileStatsAvg["FIP"] = fipList
        profileStatsAvg["AVG"] = avgList
        profileStatsAvg["SLG"] = slgList

        // Profile Characteristics: Age, Position, Hit, Throw
        val playerInfoObject = player.getJSONObject("playerInfo")
        profileCharacteristics["playerAge"] = playerInfoObject.getString("Age")
        profileCharacteristics["playerPosition"] = playerInfoObject.getString("Position")
        profileCharacteristics["playerHit"] = playerInfoObject.getString("Bats")
        profileCharacteristics["playerThrow"] = playerInfoObject.getString("Throws")

        // Profile Stats: PieChart (Hitter) - PA, HR, Non-HR Hits, BB, SO, Non-SO Outs
        // Profile Stats: PieChart (Pitcher) - TBF, HR, Non-HR Hits, BB, SO, Non-SO Outs
        val hr = dataCurrentSeason.getInt("HR")
        val h = dataCurrentSeason.getInt("H")
        val bb = dataCurrentSeason.getInt("BB")
        val ibb = dataCurrentSeason.getInt("IBB")
        val hbp = dataCurrentSeason.getInt("HBP")
        val so = dataCurrentSeason.getInt("SO")
        val total = when (position) {
            "P" -> dataCurrentSeason.getInt("TBF")
            else -> dataCurrentSeason.getInt("PA")
        }

        profileStatsCounting["TOTAL"] = total
        profileStatsCounting["HR"] = hr
        profileStatsCounting["RemHITS"] = h - hr
        profileStatsCounting["BB"] = bb + ibb + hbp
        profileStatsCounting["SO"] = so
        profileStatsCounting["OUTS"] = total - so - h - bb - ibb - hbp

        // Comparison Stats: BA, K%, BB%, GB%, Pull%, Hard%, wFB/c, wOther/c, Contact%
        val avg = dataCurrentSeason.getDouble("AVG")
        val kp = dataCurrentSeason.getDouble("K%")
        val bbp = dataCurrentSeason.getDouble("BB%")
        val gbp = dataCurrentSeason.getDouble("GB%")
        val pp = dataCurrentSeason.getDouble("Pull%")
        val hp = dataCurrentSeason.getDouble("Hard%")
        val valFB = dataCurrentSeason.optDouble("wFB/C")
        val valOther = dataCurrentSeason.optDouble("wSL/C")
                        + dataCurrentSeason.optDouble("wCT/C")
                        + dataCurrentSeason.optDouble("wCB/C")
                        + dataCurrentSeason.optDouble("wCH/C")
                        + dataCurrentSeason.optDouble("wSF/C")
        val contactP = dataCurrentSeason.getDouble("Contact%")

        comparisonStats["AVG"] = avg
        comparisonStats["KP"] = kp
        comparisonStats["BBP"] = bbp
        comparisonStats["GBP"] = gbp
        comparisonStats["PullP"] = pp
        comparisonStats["HardP"] = hp
        comparisonStats["valFB"] = valFB
        comparisonStats["valOTHER"] = valOther
        comparisonStats["ContactP"] = contactP

        return FangraphsStats(comparisonStats, profileCharacteristics, profileStatsCounting, profileStatsAvg)
    }

    companion object {
        private val client = OkHttpClient()
        private const val url = "https://www.fangraphs.com/api/players/stats"
    }
}
