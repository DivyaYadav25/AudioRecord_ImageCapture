package com.createbytes.audioimagerecord

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button_capture_image?.setOnClickListener {
            startActivity(Intent(this,ImageActivity::class.java))
        }
        button_record_audio?.setOnClickListener {
            startActivity(Intent(this,AudioActivity::class.java))
        }
    }
}
