package com.sandeep.readnfc

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.sandeep.readnfc.Constants.SPINNER_LIST_KEY
import com.sandeep.readnfc.Constants.URI_CONTENT_KEY
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {
    private var spinnerlist = ArrayList<String>()
    private lateinit var mTextViewCsvResult: TextView
    private val activityResult: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                mTextViewCsvResult.text = getFileName(this, uri)
                Coroutines.main(this@MainActivity) {
                    readCSV(uri)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mTextViewCsvResult = findViewById(R.id.textView_csvResult)
        val button: Button = findViewById(R.id.btnAddTracker)
        button.setOnClickListener {
            val intent = Intent(this, SelectPanIdActivity::class.java)
            intent.putStringArrayListExtra(SPINNER_LIST_KEY, spinnerlist)
            startActivity(intent)
        }

        findViewById<Button>(R.id.button_loadCsv)?.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "text/*"
            activityResult.launch(intent)
        }
    }

    private fun getFileName(context: Context, uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == URI_CONTENT_KEY) {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                }
            }
        }

        if (result == null) {
            result = uri.path?.substringAfterLast('/')
        }

        return result
    }

    private suspend fun readCSV(uri: Uri) {
        Coroutines.io {
            suspendCoroutine { continuation ->
                try {
                    val csvFile = contentResolver.openInputStream(uri)
                    val isr = InputStreamReader(csvFile)
                    val list = BufferedReader(isr).readLines().toMutableList()
                    list.removeAt(0)
                    spinnerlist.addAll(list)
                    continuation.resume(Unit)
                } catch (e: IOException) {
                    continuation.resumeWithException(e)
                }
            }
        }
    }
}