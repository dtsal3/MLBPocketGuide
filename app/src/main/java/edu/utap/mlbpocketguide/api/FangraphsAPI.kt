package edu.utap.mlbpocketguide.api

import android.text.SpannableString
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.lang.reflect.Type

interface FangraphsAPI {

    data class FangraphsResponse(val data: Response)

    @GET("?season=2022")
    suspend fun getStats(@Query("playerId") playerId: String, @Query("position") position: String) : FangraphsResponse

    companion object {

        // Keep the base URL simple
        private const val BASE_URL = "www.fangraphs.com"
        var httpurl = HttpUrl.Builder()
            .scheme("https")
            .host(BASE_URL)
            .addPathSegments("api/players/stats/common/")
            .build()
        fun create(): FangraphsAPI = create(httpurl)
        private fun create(httpUrl: HttpUrl): FangraphsAPI {
            val client = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    // Enable basic HTTP logging to help with debugging.
                    this.level = HttpLoggingInterceptor.Level.BASIC
                })
                .build()
            return Retrofit.Builder()
                .baseUrl(httpUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(FangraphsAPI::class.java)
        }
    }
}
