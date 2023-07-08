package com.gov.gruhalakshmi

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import com.gov.gruhalakshmi.R
import com.gov.gruhalakshmi.common.PreferenceHelper
import com.gov.gruhalakshmi.common.ProgressDialog
import com.gov.gruhalakshmi.loginData.loginResponse
import com.gov.gruhalakshmi.network.repositories.Status
import com.gov.gruhalakshmi.shared.K
import com.gov.gruhalakshmi.shared.extension.getViewModel
import com.gov.gruhalakshmi.viewModel.AuthViewModel
import org.json.JSONObject
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.gov.gruhalakshmi.common.CustomWebView
import com.gov.gruhalakshmi.common.LanguageHelper

class Dashboard : AppCompatActivity() {

    private lateinit var loader: Dialog

    private lateinit var alert: AlertDialog.Builder
    private lateinit var alertdialog: AlertDialog
    private var state = 100
    private lateinit var loginObj:loginResponse

    private val vm: AuthViewModel by lazy {
        getViewModel { AuthViewModel() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        getPendingData()
        loader = ProgressDialog.progressDialog(this)
        setVM()

        val prefs = PreferenceHelper.defaultPrefs(this)
        val obj = prefs.getString("LOGINDATA","")

        loginObj = Gson().fromJson(obj,loginResponse::class.java)


        findViewById<CardView>(R.id.addPrathinidhi).setOnClickListener {
            startActivity(Intent(this,EnterRationNumber::class.java))
        }

        findViewById<CardView>(R.id.newApplication).setOnClickListener {
//            val jsonObject = JsonObject()
//            jsonObject.addProperty("Mobile", loginObj.Mobile)
//            jsonObject.addProperty("Token", loginObj.Token)
//
//            vm.callAPI(K.CALL_HSMURL, jsonObject)
//            loader.show()

           // startActivity(Intent(this, enroller::class.java))

            startActivity(Intent(this,PendingApplications::class.java))

        }

        findViewById<ImageView>(R.id.logoutBtn).setOnClickListener {
            val prefs = PreferenceHelper.defaultPrefs(this)
            prefs.edit().clear().commit()
            finish()
            startActivity(Intent(this,Login::class.java))
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

    fun getPendingData() {
        var data = ArrayList<ApplicationData>()
        val prefs = PreferenceHelper.defaultPrefs(this)
        var str = ""
        var index  = 1
        do {
            str = prefs.getString(index.toString(),"")!!
            if(!str.isNullOrEmpty()) {
                index++
                val obj = Gson().fromJson(str, ApplicationData::class.java)
                data.add(obj)

            }
        }while(!str.isNullOrEmpty())
        if(data.size > 0)
            findViewById<TextView>(R.id.showcountTxt).text = getString(R.string.ready_for_submission)+"\n "+data.size
        else
            findViewById<TextView>(R.id.showcountTxt).text = getString(R.string.ready_for_submission)
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
                        val obj = JSONObject(it)
                        val error = obj.optString("ErrorInfo")
                        if(error.isNullOrEmpty()){
                            if(state == 2) {
                                val intent = Intent(this,ApplicantDetails::class.java)
                                intent.putExtra("FAMILYDETAILS",it)
                                startActivity(intent)
                            }else {
                                showeKYCDialog(true, obj.optString("URL"))
                            }
                        }else{
                            K.showMessageDialog(error,"Error",this)
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun showeKYCDialog(state:Boolean = true,url:String){
        var newurl = url.replace("\"","")
        alert = AlertDialog.Builder(this)
        alert.setCancelable(false)

        val wv = CustomWebView(this)
        wv.focusable = View.FOCUSABLE
        wv.isFocusableInTouchMode = true
        val settings: WebSettings = wv.getSettings()
        settings.javaScriptEnabled = true
        wv.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY)
        wv.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return false
            }
        }
        wv.loadUrl(newurl)
        // alert.setView(wv)

        alertdialog = AlertDialog.Builder(this)
            .setView(wv)
            .setNegativeButton("Cancel"
            ) { dialog, whichButton ->
                dialog.dismiss()
                onBackPressed()
                finish()
                startActivity(Intent(this,SuccessfulVerification::class.java))
                //startActivity(Intent(this,AddBeneficiaryActivity::class.java))
            }
            .setPositiveButton(
                "Continue", null) //Set to null. We override the onclick
            .create()

        //alertdialog.setTitle("Please click on continue post to the ekyc completion..")
        alertdialog.setOnShowListener(DialogInterface.OnShowListener {
            val button: Button = (it as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener(View.OnClickListener {
                alertdialog.dismiss()
                this.state = 2
                //  val aadharTxt = findViewById<EditText>(R.id.editTxtAadhar).text
                val jsonObject = JsonObject()
                jsonObject.addProperty("Mobile", loginObj.Mobile)
                jsonObject.addProperty("Token", loginObj.Token)
                jsonObject.addProperty("HSMURL",url)

                vm.callAPI(K.CALL_FETCHCOMPLED, jsonObject)
                loader.show()
//
//                val intent = Intent(this,SuccessfulVerification::class.java)
//                intent.putExtra("aadhar",aadharTxt.toString())
//                startActivity(intent)
            })
        })

        alertdialog.setView(wv)
        alertdialog.show()
        // alert.show()


        //val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(validateResponse.EKyc_URL))
        //startActivity(browserIntent)
    }

}