package com.sandeep.readnfc

import android.content.ContentValues
import android.util.Log
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.experimental.and

private const val TAG = "Utils"

class Utils {

    //region Tags Information Methods
    fun getDateTimeNow(): String {
        val format: DateFormat = SimpleDateFormat.getDateTimeInstance()
        Log.d(ContentValues.TAG, "getDateTimeNow() Return ${format.format(Date())}")
        return format.format(Date())
    }

    fun getHex(bytes: ByteArray): String {
        val sb = StringBuilder()
        for (i in bytes.indices.reversed()) {
            val b: Int = bytes[i].and(0xff.toByte()).toInt()
            if (b < 0x10) sb.append('0')
            sb.append(Integer.toHexString(b))
            if (i > 0) sb.append(" ")
        }
        Log.d(TAG, "getHex() $sb")
        return sb.toString()
    }

    fun getDec(bytes: ByteArray): Long {
        var result: Long = 0
        var factor: Long = 1
        for (i in bytes.indices) {
            val value: Long = bytes[i].and(0xffL.toByte()).toLong()
            result += value * factor
            factor *= 256L
        }
        Log.d(TAG, "getDec()")
        return result
    }

    fun getReversed(bytes: ByteArray): Long {
        var result: Long = 0
        var factor: Long = 1
        for (i in bytes.indices.reversed()) {
            val value = bytes[i].and(0xffL.toByte()).toLong()
            result += value * factor
            factor *= 256L
        }
        Log.d(TAG, "getReversed()")
        return result
    }

    //endregion
}
