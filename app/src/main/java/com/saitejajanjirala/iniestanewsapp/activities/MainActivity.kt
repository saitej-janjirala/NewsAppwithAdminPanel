package com.saitejajanjirala.iniestanewsapp.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import com.saitejajanjirala.iniestanewsapp.R

class MainActivity : AppCompatActivity() {

    lateinit var image:ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        image=findViewById(R.id.loadingimage)
        image.animate().rotation(1440f).duration=1000
        Handler().postDelayed({
            val sharedPreferences=getSharedPreferences("user", Context.MODE_PRIVATE)
            if (sharedPreferences.contains("uid") || sharedPreferences.contains("aid")){
                if(sharedPreferences.contains("uid")){
                    startActivity(Intent(this,Shownews::class.java))
                }
                else if(sharedPreferences.contains("aid")){
                    startActivity(Intent(this,News::class.java))
                }
            }
            else {
                val intent = Intent(
                    this,
                    LoginActivity::class.java
                )
                startActivity(intent)
            }
        },1500)

    }
}
