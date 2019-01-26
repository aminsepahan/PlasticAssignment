package com.amin.plasticassignment.model

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

data class TimeResponseModel(val datetime: Date) {
    @SuppressLint("SimpleDateFormat")
    fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("mm:ss.SS")
        return sdf.format(datetime)
    }
}