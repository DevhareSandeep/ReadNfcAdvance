package com.sandeep.readnfc

/**
 * The Helper extension functions to read the api response(not limited to) values
 * and set the defaults in case of nulls received from the api
 */

import android.text.TextUtils
import android.view.View
import java.text.NumberFormat
import java.text.ParseException
import java.util.Locale
import java.util.regex.Pattern

const val LOCALE_EN = "en"

fun String?.numberString(default: String = "0"): String {
    return if (this.isNullOrBlank()) default else this.trim()
}

fun String?.stringToNumber(default: String = "0"): String {
    return if (this.isNullOrBlank()) {
        default
    } else {
        if (this.trim().isEmpty()) {
            default
        } else {
            this.trim()
        }
    }
}

fun String?.string(default: String = ""): String {
    return if (this.isNullOrBlank()) default else this
}

fun Double?.default(default: Double = 0.0): Double {
    return this ?: default
}

fun Int?.default(default: Int = 0): Int {
    return this ?: default
}

fun Long?.default(default: Long = 0): Long {
    return this ?: default
}

fun Boolean?.default(default: Boolean = false): Boolean {
    return this ?: default
}

fun <T> List<T>?.default(): List<T> {
    return this ?: listOf()
}

fun <T> ArrayList<T>?.default(): ArrayList<T> {
    return this ?: arrayListOf()
}

// For Java to kotlin value null check
fun stringNumber(str: String?): String {
    return if (str.isNullOrBlank()) "0" else str.trim()
}

fun stringEmpty(str: String?): String {
    return if (str.isNullOrBlank()) "" else str
}

fun toDouble(value: Double?): Double {
    return value.default()
}

fun toInt(value: Int?): Int {
    return value.default()
}

fun Double?.isNullOrZero(): Boolean {
    return this == null || this == 0.0
}

fun Int?.isNullOrZero(): Boolean {
    return this == null || this == 0
}

fun Long?.isNullOrZero(): Boolean {
    return this == null || this == 0L
}

fun String?.int(): Int {
    return this.numberString("0").trim().toInt()
}

fun String?.toSafeDouble(): Double {
    val defaultDouble = 0.0
    if (isNullOrEmpty()) {
        return defaultDouble
    }

    return try {
        trim().toDouble()
    } catch (numberFormatException: NumberFormatException) {
        defaultDouble
    }
}

fun parseInt(str: String?): Int {
    if (str == null || TextUtils.isEmpty(str.trim())) {
        return 0
    }
    return if (TextUtils.isDigitsOnly(str)) {
        str.trim().toInt()
    } else 0
}

fun parseDouble(
    numericString: String?, locale: Locale = Locale.ENGLISH
): Double {
    var numericValue = numericString
    if (numericValue == null || TextUtils.isEmpty(numericValue.trim())) {
        return 0.0
    } else {
        // Crashlytics fix
        numericValue = numericValue.trim()
        if (numericValue.length == 1 && numericValue.contains(".")) {
            return 0.0
        } else if (numericValue.startsWith(".")) {
            numericValue = "0$numericValue"
        }
        if (isDouble(numericValue)) {
            return if (LOCALE_EN === locale.language) {
                numericValue.toDouble()
            } else {
                getEnNumber(numericValue, locale)
            }
        }
    }
    return 0.0
}

fun isDouble(doubleNumber: String): Boolean {
    val decimalPattern = "([0-9]){1,13}\\.([0-9]*)"
    if (TextUtils.isEmpty(doubleNumber)) {
        return false
    }
    return if (TextUtils.isDigitsOnly(doubleNumber)) {
        true
    } else Pattern.matches(decimalPattern, doubleNumber)
}

fun getEnNumber(
    numericString: String, locale: Locale
): Double {
    try {
        val format = NumberFormat.getInstance(locale)
        return format.parse(numericString.trim()).toDouble()
    } catch (e: ParseException) {
        e.printStackTrace()
        throw RuntimeException(e)
    }
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}
