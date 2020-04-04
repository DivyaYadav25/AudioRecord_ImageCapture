package com.createbytes.audioimagerecord

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val thread = object : Thread(){
            override fun run() {
                try{
                    sleep(3000)
                }catch (e:Exception){
                    e.printStackTrace()
                }finally {
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                        finish()
                    }

            }
        }
        thread.start()
    }
}
