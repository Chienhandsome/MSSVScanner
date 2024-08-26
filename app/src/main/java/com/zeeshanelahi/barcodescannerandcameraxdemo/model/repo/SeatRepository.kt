package com.zeeshanelahi.barcodescannerandcameraxdemo.model.repo

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.zeeshanelahi.barcodescannerandcameraxdemo.model.ApiEndpoint
import com.zeeshanelahi.barcodescannerandcameraxdemo.model.ApiFactory
import com.zeeshanelahi.barcodescannerandcameraxdemo.model.SharedPreferencesHelper
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class SeatRepository(private val context: Context) {
    private val TAG = "SeatRepository";
    private val requestQueue: RequestQueue = Volley.newRequestQueue(context)
    private val sharedPreferencesHelper: SharedPreferencesHelper = SharedPreferencesHelper.getInstance(context)
    private val seatData = mutableListOf<Seat>()

    data class Seat(val mssv: String, val seat: String)

    fun loadSeats(callback: (List<Seat>) -> Unit) {
        val savedData = sharedPreferencesHelper.getString("seat_data", null)
        if (!savedData.isNullOrEmpty()) {
            updateMemoryFromPreferences(savedData)
            callback(seatData)
            Log.d(TAG, "loadSeats: savedData not null")
        } else {
            Log.d(TAG, "loadSeats: fetchSeatData called")
            fetchSeatData(callback)
        }
    }

    private fun fetchSeatData(callback: (List<Seat>) -> Unit) {

        val url = ApiFactory.createApi(ApiEndpoint.GET_SEATS, context)
        Log.d(TAG, "fetchSeatData: url: $url")
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                val newData = parseSeatData(response)
                if (newData != seatData) {
                    seatData.clear()
                    seatData.addAll(newData)
                    saveDataToPreferences()
                }
                callback(seatData)
            },
            { error ->
                    error.printStackTrace()
            }
        )
        requestQueue.add(jsonArrayRequest)
    }

    private fun parseSeatData(response: JSONArray): List<Seat> {
        val list = mutableListOf<Seat>()
        for (i in 0 until response.length()) {
            val item: JSONObject = response.getJSONObject(i)
            val mssv = item.getString("mssv")
            val seat = item.getString("seat")
            if (mssv.isNotEmpty() && seat.isNotEmpty()){
                list.add(Seat(mssv, seat))
                Log.d(TAG, "parseSeatData: mssv: $mssv, seat: $seat")
            }
            else{
                Log.e(TAG, "parseSeatData: NULL mssv or seat")
            }
        }
        return list
    }

    private fun saveDataToPreferences() {
        val jsonArray = JSONArray()
        for (seat in seatData) {
            val jsonObject = JSONObject()
            jsonObject.put("mssv", seat.mssv)
            jsonObject.put("seat", seat.seat)
            jsonArray.put(jsonObject)
            Log.d(TAG, "size of jsonArray: ${jsonArray.length()}")
        }
        sharedPreferencesHelper.saveString("seat_data", jsonArray.toString())
    }

    private fun updateMemoryFromPreferences(savedData: String) {
        val jsonArray = JSONArray(savedData)
        seatData.clear()
        for (i in 0 until jsonArray.length()) {
            val item: JSONObject = jsonArray.getJSONObject(i)
            val mssv = item.getString("mssv")
            val seat = item.getString("seat")
            seatData.add(Seat(mssv, seat))
        }
    }

    fun getTotalSeatsCount(): Int {
        if (seatData.isEmpty()) {
            val savedData = sharedPreferencesHelper.getString("seat_data", null)
            if (!savedData.isNullOrEmpty()) {
                val jsonArray = JSONArray(savedData)
                if (jsonArray.length() > 0) {
                    return jsonArray.length()
                }
            }
        }
        return seatData.size
    }

    fun getSeatInfo(mssv: String): String? {
        for (seat in seatData) {
            if (seat.mssv == mssv) {
                return seat.seat
            }
        }

        val savedData = sharedPreferencesHelper.getString("seat_data", null)
        if (savedData != null) {
            try {
                val jsonArray = JSONArray(savedData)
                for (i in 0 until jsonArray.length()) {
                    val item = jsonArray.getJSONObject(i)
                    if (item.getString("mssv") == mssv) {
                        return item.getString("seat")
                    }
                }
                return ""
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return null
    }
}
