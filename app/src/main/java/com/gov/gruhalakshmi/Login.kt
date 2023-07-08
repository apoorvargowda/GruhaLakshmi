package com.gov.gruhalakshmi

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.format.DateUtils
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.gov.gruhalakshmi.common.LanguageHelper
import com.gov.gruhalakshmi.common.PermissionsDelegate
import com.gov.gruhalakshmi.common.PreferenceHelper
import com.gov.gruhalakshmi.common.ProgressDialog
import com.gov.gruhalakshmi.loginData.loginResponse
import com.gov.gruhalakshmi.network.repositories.Status
import com.gov.gruhalakshmi.shared.K
import com.gov.gruhalakshmi.shared.extension.getViewModel
import com.gov.gruhalakshmi.viewModel.AuthViewModel
import org.joda.time.DateTimeComparator
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class Login : AppCompatActivity() {
    private lateinit var loader: Dialog

    private lateinit var permissionsDelegate: PermissionsDelegate

    private val vm: AuthViewModel by lazy {
        getViewModel { AuthViewModel() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val dt = Calendar.getInstance().time


        permissionsDelegate = PermissionsDelegate(this)
        permissionsDelegate.requestCameraPermission()

        val prefs = PreferenceHelper.defaultPrefs(this)
        val languageCode = prefs.getString("LAN","en")
        val engBtn = findViewById<Button>(R.id.englishBtn)
        val knBtn = findViewById<Button>(R.id.kanBtn)
        when(languageCode){
            "en" -> {
                engBtn.setBackgroundColor(Color.parseColor("#87cefa"))
            }
            "kn" -> {
                knBtn.setBackgroundColor(Color.parseColor("#87cefa"))
            }
        }

        val isLoggedIn = prefs.getBoolean("ISLOGIN",false)
        if(isLoggedIn){
            val loggedDtStr = prefs.getString("LOGGEDDATE","")
            if(!loggedDtStr.isNullOrEmpty()){
                val loggedDt = K.strToDate(loggedDtStr)
                val retVal = DateTimeComparator.getDateOnlyInstance().compare(dt,loggedDt)
                if(retVal > 0){
                    prefs.edit().clear().commit()
                }else {
                    finish()
                    startActivity(Intent(this, Dashboard::class.java))
                }
            }else {
                finish()
                startActivity(Intent(this, Dashboard::class.java))
            }
//            val intent = Intent(this,ApplicantDetails::class.java)
//            intent.putExtra("FAMILYDETAILS",loadJSONFromAsset())
//            startActivity(intent)
        }

        loader = ProgressDialog.progressDialog(this)
        setVM()

        findViewById<Button>(R.id.englishBtn).setOnClickListener {
            setAppLanguage("en")
        }
        findViewById<Button>(R.id.kanBtn).setOnClickListener {
            setAppLanguage("kn")
        }

        findViewById<Button>(R.id.btnLogin).setOnClickListener {




//Prepare Chooser (Gallery or Camera)

//Prepare Chooser (Gallery or Camera)
//            Croperino.prepareChooser(
//                this,
//                "Capture photo...",
//                ContextCompat.getColor(this, android.R.color.background_dark)
//            )

//Prepare Camera

//Prepare Camera
//            try {
//                Croperino.prepareCamera(this)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
            val mobile = findViewById<EditText>(R.id.etMobileNumber).text.toString()
            var pos = findViewById<Spinner>(R.id.spinner).selectedItemPosition
            if(mobile.isNullOrEmpty() || pos < 0) return@setOnClickListener
            pos = pos+1

            //startActivity(Intent(this,OtpScreen::class.java))

            val jsonObject = JsonObject()
            jsonObject.addProperty("Mobile", mobile)
            jsonObject.addProperty("Type", pos.toString())

            vm.callAPI(K.CALL_SEND_OTP, jsonObject)
            loader.show()

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

    private fun setAppLanguage(languageCode: String) {
        val prefs = PreferenceHelper.defaultPrefs(this)
        prefs.edit().putString("LAN",languageCode).commit()
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources = resources
        val config: Configuration = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        ActivityCompat.recreate(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val permsResult = permissionsDelegate.resultGranted(requestCode, permissions, grantResults)
        if ( permsResult == 1) {


        }else if(permsResult == 3) {

        }
    }

    fun loadJSONFromAsset(): String? {
        var json: String? = null
        json = try {
            val `is`: InputStream = getAssets().open("testdata.json")
            val size: Int = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            String(buffer, Charsets.UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setVM() {
        vm.result.observe(this, Observer {

            when (it?.status) {
                Status.LOADING -> {
                    loader.show()
                }
                Status.SUCCESS -> {
                    loader.dismiss()
                    it.data?.let { it ->

                        val login = Gson().fromJson(it,loginResponse::class.java)
                        if(!login.ErrorInfo.isNullOrEmpty()){
                            K.showMessageDialog(login.ErrorInfo,"Error",this)
                            return@Observer
                        }
                        if(!login.Mobile.isNullOrEmpty() && !login.Token.isNullOrEmpty()){
                            val prefs = PreferenceHelper.defaultPrefs(this)
                            prefs.edit().putString("LOGINDATA",it).commit()
                            val dt = Calendar.getInstance().time
                            prefs.edit().putString("LOGGEDDATE",K.dateToString(dt)).commit()
                            finish()
                            startActivity(Intent(this,OtpScreen::class.java))
                        }
                    }
                }
                Status.ERROR -> {
                    loader.dismiss()

                    it.message?.let { message ->
                        Toast.makeText(this,message, Toast.LENGTH_LONG).show()
                        //benContainer.showSnackBar(message, Snackbar.LENGTH_SHORT)
                    }

                }
                else -> null
            }

        })
    }



}