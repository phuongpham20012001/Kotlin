package com.example.firebase

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.launch

import retrofit2.Retrofit

import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import kotlin.math.log

class CountryViewModel : ViewModel() {
    var timezones = mutableStateOf("")
    var continents = mutableStateOf("")
    var population = mutableStateOf("")
    var area = mutableStateOf("")
    var capital = mutableStateOf("")

    val api : CountryApi by lazy {
        Retrofit.Builder().baseUrl("https://restcountries.com/v3.1/name/").
        addConverterFactory(GsonConverterFactory
            .create())
            .build()
            .create()
    }
    fun getCountry(search: String){
        viewModelScope.launch {
            val response = api.getCountry(search).awaitResponse()
            if(response.isSuccessful){
                val data  = response.body()
                capital.value = data!![0]["capital"].toString()
                area.value = data!![0]["area"].toString()
                population.value = data!![0]["population"].toString()
                continents.value = data!![0]["continents"].toString()
                timezones.value = data!![0]["timezones"].toString()





            } else {
                Log.d("***", "Not success")
            }
        }
    }
}