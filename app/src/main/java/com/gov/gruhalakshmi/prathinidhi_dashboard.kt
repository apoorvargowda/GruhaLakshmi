package com.gov.gruhalakshmi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.cardview.widget.CardView
import com.gov.gruhalakshmi.R

class prathinidhi_dashboard : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prathinidhi_dashboard)

        findViewById<ImageView>(R.id.ivBackBtn).setOnClickListener {
            finish()
        }

        findViewById<CardView>(R.id.addPP).setOnClickListener {
           startActivity(Intent(this, EnterAadhaar::class.java))
        }

    }
}