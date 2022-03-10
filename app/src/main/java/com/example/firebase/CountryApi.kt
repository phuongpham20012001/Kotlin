package com.example.firebase

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonSerializer
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path


interface CountryApi {
    @GET("{x}")
    fun getCountry(
        @Path("x") search : String,
    ) : Call<Array<JsonObject>>


}

