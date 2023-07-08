package com.gov.gruhalakshmi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import com.gov.gruhalakshmi.R

class enroller : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enroller)
        findViewById<ImageView>(R.id.ivBackBtn).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btnValidate).setOnClickListener {
            startActivity(Intent(this, Dashboard::class.java))
        }

    }
}