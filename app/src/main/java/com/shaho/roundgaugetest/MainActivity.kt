package com.shaho.roundgaugetest

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.shaho.roundgauge.RoundGauge

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val valueTextView = findViewById<TextView>(R.id.value_textView)
        val roundGauge = findViewById<RoundGauge>(R.id.roundGauge)
        roundGauge.setOnChangeProgressBarListener(object : RoundGauge.OnChangeProgressBarListener {
            override fun onChangeValue(time: String) {
                valueTextView.text = time
            }
        })


        val startTime = findViewById<EditText>(R.id.startTime_editText)
        val endTime = findViewById<EditText>(R.id.endTime_editText)
        val currentTime = findViewById<EditText>(R.id.currentTime_editText)
        val showButton = findViewById<Button>(R.id.show_button)
        showButton.setOnClickListener {
            roundGauge.setStartTime(startTime.text.toString().toInt())
            roundGauge.setEndTime(endTime.text.toString().toInt())
            roundGauge.setCurrentTime(currentTime.text.toString().toInt())
        }
    }
}
