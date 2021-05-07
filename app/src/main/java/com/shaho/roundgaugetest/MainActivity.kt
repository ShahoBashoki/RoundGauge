package com.shaho.roundgaugetest

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.shaho.roundgauge.RoundGauge

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val valueTextView = findViewById<TextView>(R.id.value_textView)
        val roundGauge = findViewById<RoundGauge>(R.id.roundGauge)
        roundGauge.setValue(60F)
        roundGauge.setOnChangeProgressBarListener(object : RoundGauge.OnChangeProgressBarListener {
            override fun onChangeValue(value: Float) {
                valueTextView.text = (value * 100).toInt().toString()
            }
        })

    }
}