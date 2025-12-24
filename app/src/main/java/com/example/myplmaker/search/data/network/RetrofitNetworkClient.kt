package com.example.myplmaker.search.data.network


import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.myplmaker.search.data.dto.Response
import com.example.myplmaker.search.data.dto.TrackSearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitNetworkClient(private val iTunesApi: ITunesApi, private val context: Context) :
    NetworkClient {

    override suspend fun doRequest(dto: Any): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = -1 }
        }

        if (dto !is TrackSearchRequest) {
            return Response().apply { resultCode = 400 }
        }
        return try {
            val resp = iTunesApi.searchTracks(dto.text)
            resp.apply { resultCode = 200 }
        } catch (e: Exception) {
            Response().apply { resultCode = 500 }
        }
    }

    private fun isConnected(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }
}