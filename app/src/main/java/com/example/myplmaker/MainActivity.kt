package com.example.myplmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        /*
                val image = findViewById<Button>(R.id.search)
                image.setOnClickListener {
                    val displayIntent = Intent(this, SearchActivity::class.java)
                    startActivity(displayIntent)
                }
        */



        val image = findViewById<Button>(R.id.search)
        val displayIntent = Intent(this, SearchActivity::class.java)
        val imageClickListener: View.OnClickListener = object : View.OnClickListener {

            override fun onClick(v: View?) {
                startActivity(displayIntent)
            }
        }

        image.setOnClickListener(imageClickListener)







        val media = findViewById<Button>(R.id.mmedia)
        media.setOnClickListener {
            val mediaIntent = Intent(this, MediaActivity::class.java)
            startActivity(mediaIntent)
        }

        val setting = findViewById<Button>(R.id.settings)
        setting.setOnClickListener {
            val settingIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingIntent)
        }






    }
}