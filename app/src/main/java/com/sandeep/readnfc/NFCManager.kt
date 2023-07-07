package com.sandeep.readnfc

import android.app.Activity
import android.content.Context
import android.nfc.NfcAdapter
import android.os.Bundle
import android.util.Log

private const val TAG = "NFCManager"

object NFCManager {

    fun enableReaderMode(
        context: Context,
        activity: Activity,
        callback: NfcAdapter.ReaderCallback,
        flags: Int,
        extras: Bundle,
    ) {
        try {
            NfcAdapter.getDefaultAdapter(context).enableReaderMode(activity, callback, flags, extras)
            Log.d(TAG, "enableReaderMode")
        } catch (ex: UnsupportedOperationException) {
            Log.e(TAG, "UnsupportedOperationException ${ex.message}", ex)
        }
    }

    fun disableReaderMode(context: Context, activity: Activity) {
        try {
            NfcAdapter.getDefaultAdapter(context).disableReaderMode(activity)
            Log.d(TAG, "disableReaderMode")
        } catch (ex: UnsupportedOperationException) {
            Log.e(TAG, "UnsupportedOperationException ${ex.message}", ex)
        }
    }

    private fun isSupported(context: Context): Boolean {
        val nfcAdapter = NfcAdapter.getDefaultAdapter(context)
        return nfcAdapter != null
    }

    public fun isNotSupported(context: Context): Boolean {
        val nfcAdapter = NfcAdapter.getDefaultAdapter(context)
        return nfcAdapter == null
    }

    private fun isEnabled(context: Context): Boolean {
        val nfcAdapter = NfcAdapter.getDefaultAdapter(context)
        return nfcAdapter?.isEnabled ?: false
    }

    fun isNotEnabled(context: Context): Boolean {
        val nfcAdapter = NfcAdapter.getDefaultAdapter(context)
        return nfcAdapter?.isEnabled?.not() ?: true
    }

    fun isSupportedAndEnabled(context: Context): Boolean {
        return isSupported(context) && isEnabled(context)
    }
}
