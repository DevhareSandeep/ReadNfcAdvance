package com.sandeep.readnfc

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.sandeep.readnfc.Constants.PAN_ID_KEY
import com.sandeep.readnfc.Constants.SPINNER_LIST_KEY

class SelectPanIdActivity : AppCompatActivity() {
    private val languages = mutableListOf<String>()
    private var pos = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        val button = findViewById<Button>(R.id.button3)
        val spinnerList = intent.getStringArrayListExtra(SPINNER_LIST_KEY) ?: emptyList()
        languages.clear()
        languages.addAll(spinnerList.map { it.substringBefore(',') })
        button.setOnClickListener {
            if (pos < spinnerList.size) {
                val selectedItem = spinnerList[pos]
                val commaIndex = selectedItem.indexOf(',')
                val panId = if (commaIndex != -1) {
                    selectedItem.substring(commaIndex + 1)
                } else {
                    ""
                }
                println("asd $selectedItem")
                val intent = Intent(this, NfcTagReadActivity::class.java)
                intent.putExtra(PAN_ID_KEY, panId)
                startActivity(intent)
                // launchMainFragment(panId)
            }
        }
        val spinner = findViewById<Spinner>(R.id.spinner)
        val adapter = ArrayAdapter(this@SelectPanIdActivity, android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
              /*  Toast.makeText(
                    this@SelectPanIdActivity,
                    getString(R.string.selected_item) + " " + "" + languages[position],
                    Toast.LENGTH_SHORT
                ).show()*/
                pos = position
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
    }
 /*   private fun launchMainFragment(panId: String) {
        if (supportFragmentManager.findFragmentByTag(TAG) == null) supportFragmentManager.beginTransaction()
            .add(R.id.frame_layout, MainFragment.newInstance(panId), TAG).addToBackStack(TAG).commit()
    }*/
}
