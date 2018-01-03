package com.edipo.uni7kotlin

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.support.annotation.RequiresPermission
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.edipo.uni7kotlin.remote.IOpenWeatherMap
import com.edipo.uni7kotlin.remote.WeatherRsp
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_weather.*
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class WeatherInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)
        text_username.text = intent.getStringExtra(EXTRA_USERNAME)
        checkForUserLocation()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (permissions.isNotEmpty() && permissions.first() == GPS_PERMISSION) {
            if (ContextCompat.checkSelfPermission(this, GPS_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
                requestUserLocation()
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, GPS_PERMISSION)) {
                    val rootView = findViewById<View>(android.R.id.content)
                    Snackbar.make(rootView, R.string.weather_gps_permission_rationale, Snackbar.LENGTH_INDEFINITE)
                            .setAction(android.R.string.ok, { requestGPSPermission() })
                            .show()
                } else {
                    Toast.makeText(this, R.string.weather_gps_permission_denied, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun checkForUserLocation() {
        if (ContextCompat.checkSelfPermission(this, GPS_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
            requestUserLocation()
        } else {
            requestGPSPermission()
        }
    }

    private fun requestGPSPermission() {
        val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        ActivityCompat.requestPermissions(this, permissions, RC_PERMISSION)
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    private fun requestUserLocation() {
        LocationServices.getFusedLocationProviderClient(this)
                .lastLocation
                .addOnSuccessListener(this, { location ->
                    if (location == null) {
                        showLocationError()
                        return@addOnSuccessListener
                    }
                    requestTemperatureFromLocation(location.latitude, location.longitude)
                    try {
                        val addresses = Geocoder(applicationContext)
                                .getFromLocation(location.latitude, location.longitude, 1)
                        if (addresses.isNotEmpty()) {
                            val address = addresses.first()
                            text_weather_city.text = "${address.locality} - ${address.adminArea}"
                        } else {
                            showLocationError()
                        }
                    } catch (e: Exception) {
                        showLocationError()
                    }
                })
    }

    private fun requestTemperatureFromLocation(lat: Double, lon: Double) {
        Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create())
                .baseUrl("https://api.openweathermap.org/")
                .build()
                .create(IOpenWeatherMap::class.java)
                .getWeather(lat, lon, "pt", "metric", BuildConfig.WEATHER_APPID)
                .enqueue(object : Callback<WeatherRsp> {

                    override fun onResponse(call: retrofit2.Call<WeatherRsp>?, response: Response<WeatherRsp>?) {
                        val body = response?.body()
                        if (response?.isSuccessful == false || body == null) {
                            showTemperatureError()
                            return
                        }
                        text_weather_temp.text = getString(R.string.weather_temperature, body.main.temp)
                        if (body.weather.isNotEmpty()) {
                            val weather = body.weather.first()
                            text_weather_desc.text = weather.description
                            Glide.with(this@WeatherInfoActivity)
                                    .load("http://openweathermap.org/img/w/${weather.icon}.png")
                                    .into(image_weather)
                        }
                    }

                    override fun onFailure(call: retrofit2.Call<WeatherRsp>?, t: Throwable?) {
                        showTemperatureError()
                        t?.printStackTrace()
                    }
                })
    }

    private fun showTemperatureError() {
        Snackbar.make(findViewById<View>(android.R.id.content), R.string.error_weather, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.try_again, { checkForUserLocation() })
                .show()
    }

    private fun showLocationError() {
        Snackbar.make(findViewById<View>(android.R.id.content), R.string.error_location, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.try_again, { checkForUserLocation() })
                .show()
    }

    companion object {

        private const val GPS_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION
        private const val EXTRA_USERNAME = "usernameExtra"
        private const val RC_PERMISSION = 234

        fun getIntent(context: Context, username: String): Intent {
            return Intent(context, WeatherInfoActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra(EXTRA_USERNAME, username)
            }
        }
    }

}
