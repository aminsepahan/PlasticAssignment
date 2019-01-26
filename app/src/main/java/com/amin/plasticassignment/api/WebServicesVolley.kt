package com.amin.plasticassignment.api

import android.util.Log
import com.amin.plasticassignment.model.TimeResponseModel
import com.amin.plasticassignment.utils.App
import com.amin.plasticassignment.utils.Constants.BASE_URL
import com.amin.plasticassignment.utils.Constants.LOG_TAG
import com.amin.plasticassignment.utils.Constants.VOLLEY_TIME_OUT
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.google.gson.GsonBuilder
import org.json.JSONException
import org.json.JSONObject


class WebServicesVolley {
    var gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS").create()

    fun getCurrentTime(
        url: String = BASE_URL,
        listener: NetworkListeners
    ): StringRequest {

        val stringRequest = object : StringRequest(
            Request.Method.GET,
            BASE_URL, Response.Listener<String> { response ->
                val gson = GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS").create()

                listener.onResponse(gson.fromJson(response, TimeResponseModel::class.java))
            },
            Response.ErrorListener { error ->
                parseError(error, url, listener)
            }) {

        }

        stringRequest.retryPolicy = DefaultRetryPolicy(
            VOLLEY_TIME_OUT,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        App.app.addToRequestQueue(stringRequest)
        return stringRequest

    }


    private fun parseError(error: VolleyError, url: String, listener: NetworkListeners) {
        var message = ""
        if (error.networkResponse != null) {
            if (error.networkResponse.data != null) {
                message = String(error.networkResponse.data)
                var jsonObject: JSONObject? = null
                try {
                    jsonObject = JSONObject(message)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

                if (jsonObject != null && jsonObject.has("message")) {
                    try {
                        message = jsonObject.getString("message")
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                }
            }
            message += error.networkResponse.statusCode
            if (message.contains("html")) {
                Log.d(LOG_TAG, " error for url $url == $message")
                message = "Connection Error"
            }
        } else {
            message = "Connection Error"
        }
        Log.d(LOG_TAG, " error for url $url == $message")
        listener.onError(message)
    }

    interface NetworkListeners {
        fun onResponse(model: TimeResponseModel)
        fun onError(message: String)
    }
}

