package com.gov.gruhalakshmi

import android.content.Context
import `in`.aabhasjindal.otptextview.OtpTextView
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.google.gson.Gson
import com.gov.gruhalakshmi.common.PreferenceHelper
import com.gov.gruhalakshmi.loginData.loginResponse
import com.gov.gruhalakshmi.R
import com.gov.gruhalakshmi.common.LanguageHelper

class OtpScreen : AppCompatActivity() {
    private lateinit var loginObj:loginResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_screen)

        val prefs = PreferenceHelper.defaultPrefs(this)
        val obj = prefs.getString("LOGINDATA","")

        loginObj = Gson().fromJson(obj,loginResponse::class.java)

        findViewById<EditText>(R.id.editTxtMobile).setText(loginObj.Mobile)

        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            val otpview = findViewById<OtpTextView>(R.id.otp_view).otp
            if(!otpview.isNullOrEmpty() && loginObj.OTP.equals(otpview))
            //if(!otpview.isNullOrEmpty())
            {
                prefs.edit().putBoolean("ISLOGIN",true).commit()
                finish()
                startActivity(Intent(this, Dashboard::class.java))
            }
            //startActivity(Intent(this, Dashboard::class.java))
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        // Retrieve the language code from the base application class or preferences
        val app = newBase?.applicationContext as BaseApplication
        val languageCode: String =
            app.getLanguageCode() // Replace with your implementation

        // Set the language for the activity
        val context: Context = LanguageHelper.wrapContext(newBase!!, languageCode)
        super.attachBaseContext(context)
    }
}