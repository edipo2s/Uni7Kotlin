package com.edipo.uni7kotlin.remote

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface IOpenWeatherMap {

    @GET("data/2.5/weather")
    fun getWeather(@Query("lat") latitude: Double,
                   @Query("lon") longitude: Double,
                   @Query("lang") language: String,
                   @Query("units") units: String,
                   @Query("appid") appId: String): Call<WeatherRsp>

}
