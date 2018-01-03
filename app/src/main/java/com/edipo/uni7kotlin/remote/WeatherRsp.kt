package com.edipo.uni7kotlin.remote

class WeatherRsp(val main: Main, val weather: List<Weather>)

class Main(val temp: Float)

class Weather(val description: String, val icon: String)