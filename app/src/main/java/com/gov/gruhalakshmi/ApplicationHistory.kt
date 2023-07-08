package com.gov.gruhalakshmi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.gov.gruhalakshmi.R

class ApplicationHistory : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_application_history)


        findViewById<ImageView>(R.id.ivBackBtn).setOnClickListener {
            finish()
        }
    }
}