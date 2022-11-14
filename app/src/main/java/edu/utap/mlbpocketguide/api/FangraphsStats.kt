package edu.utap.mlbpocketguide.api

import kotlin.collections.Map.Entry

// So, I don't really have the OkHttp / Retrofit example from class working
// But I wanted to mirror the architecture, even if the implementation is different
// this class takes in a JSONObject from our FanGraphs response and turns it into a
// FangraphsStats object to use in our viewmodel

data class FangraphsStats (

    // Initialize our objects
    val comparisonStats: MutableMap<String, Long>,
    val profileCharacteristics: MutableMap<String, String>,
    val profileStatsCounting: MutableMap<String, Int>,
    val profileStatsAvg: MutableMap<String, ArrayList<Pair<Int, Long>>>

)



