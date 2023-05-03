package com.codelab.printertextview

import android.os.Bundle
import android.view.View
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import kotlin.random.Random

/**
 * @author caosanyang
 * @date 2023/5/4
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tv = findViewById<PrinterTextView>(R.id.tv_printer)
        val scroller = findViewById<ScrollView>(R.id.scroller)

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            lifecycleScope.launch {
                tv.printText(readTongueFromAsset(), PrinterTextView.PrinterSpeed.FAST)
            }
        }
    }

    private suspend fun readTongueFromAsset(): String {
        return withContext(Dispatchers.IO) {
            var ret = ""
            try {
                val input = assets.open("tongue-twister.json")
                val jsonString = input.bufferedReader().use {
                    it.readText()
                }
                val arr = JSONArray(jsonString)
                val random = Random.nextInt(0, arr.length())
                val jsonObj = arr.getJSONObject(random)
                ret = jsonObj["content"].toString()
            } catch (e: Exception) {
                ret = "Something went wrong."
            }
            ret
        }
    }
}