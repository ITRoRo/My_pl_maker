package com.example.myplmaker.main.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.myplmaker.R
import com.example.myplmaker.media.ui.activyty.MediaActivity
import com.example.myplmaker.search.ui.activity.SearchActivity
import com.example.myplmaker.setting.ui.activity.SettingActivity


class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
            val settingIntent = Intent(this, SettingActivity::class.java)
            startActivity(settingIntent)
        }
    }
}