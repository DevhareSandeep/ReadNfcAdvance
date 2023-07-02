package com.sandeep.readnfc

import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.sandeep.readnfc.Constants.PAN_ID_KEY
import com.sandeep.readnfc.Constants.TAG_KEY
import com.sandeep.readnfc.databinding.ActivityBinder
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val TAG = "NfcTagReadActivity"

class NfcTagReadActivity : AppCompatActivity(), CompoundButton.OnCheckedChangeListener, NfcAdapter.ReaderCallback {
    private var binder: ActivityBinder? = null
    private val viewModel: MainViewModel by lazy { ViewModelProvider(this@NfcTagReadActivity)[MainViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = DataBindingUtil.setContentView(this@NfcTagReadActivity, R.layout.activity_main4)
        binder?.viewModel = viewModel
        val receivedPanId = intent?.getStringExtra(PAN_ID_KEY).orEmpty()
        binder?.toggleButton?.setOnCheckedChangeListener(this@NfcTagReadActivity)
        binder?.toggleButton?.isChecked = true
        Coroutines.main(this@NfcTagReadActivity) { scope ->
            scope.launch(block = {
                binder?.viewModel?.observeNFCStatus()?.collectLatest(action = { status ->
                    val nfcManager = NFCManager
                    if (status == NFCStatus.NoOperation) {
                        Log.d(TAG, "observeNFCStatus NFCStatus.NoOperation")
                        nfcManager.disableReaderMode(this@NfcTagReadActivity, this@NfcTagReadActivity)
                    } else if (status == NFCStatus.Tap) {
                        Log.d(TAG, "observeNFCStatus NFCStatus.Tap")
                        val viewModel = this@NfcTagReadActivity.viewModel
                        nfcManager.enableReaderMode(
                            this@NfcTagReadActivity,
                            this@NfcTagReadActivity,
                            this@NfcTagReadActivity,
                            viewModel.getNFCFlags(),
                            viewModel.getExtras()
                        )
                    }

                })
            })
            scope.launch(block = {
                binder?.viewModel?.observeToast()?.collectLatest(action = { message ->
                    Log.d(TAG, "observeToast $message")
                    Toast.makeText(this@NfcTagReadActivity, message, Toast.LENGTH_LONG).show()
                })
            })
            scope.launch(block = {
                binder?.viewModel?.observeTag()?.collectLatest(action = { tag ->
                    Log.d(TAG, "observeTag $tag")
                    binder?.textViewExplanation?.text = tag
                    binder?.button5?.isEnabled = !tag.isNullOrEmpty()
                })
            })
        }
        binder?.button5?.setOnClickListener {
            val tagData = binder?.textViewExplanation?.text
            val intent = Intent(this@NfcTagReadActivity, DisplayDetailsActivity::class.java)
            intent.putExtra(TAG_KEY, tagData)
            intent.putExtra(PAN_ID_KEY, receivedPanId)
            startActivity(intent)
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        Log.d(TAG, "onCheckedChanged $isChecked")
        if (buttonView == binder?.toggleButton) {
            viewModel.onCheckNFC(isChecked)
        }
    }

    override fun onTagDiscovered(tag: Tag?) {
        Log.d(TAG, "onTagDiscovered $tag")
        binder?.viewModel?.readTag(tag)
    }

    /* private fun launchMainFragment(receivedPanId: String) {
         if (supportFragmentManager.findFragmentByTag(tag) == null) supportFragmentManager.beginTransaction()
             .add(R.id.frame_layout, MainFragment.newInstance(receivedPanId), tag).addToBackStack(tag).commit()
     }*/
}