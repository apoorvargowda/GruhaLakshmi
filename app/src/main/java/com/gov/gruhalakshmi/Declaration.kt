package com.gov.gruhalakshmi

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.http.SslError
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.SslErrorHandler
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.gov.gruhalakshmi.R
import com.gov.gruhalakshmi.common.CustomWebView
import com.gov.gruhalakshmi.common.LanguageHelper
import com.gov.gruhalakshmi.common.PreferenceHelper
import com.gov.gruhalakshmi.common.ProgressDialog
import com.gov.gruhalakshmi.loginData.loginResponse
import com.gov.gruhalakshmi.network.repositories.Status
import com.gov.gruhalakshmi.shared.K
import com.gov.gruhalakshmi.shared.extension.getViewModel
import com.gov.gruhalakshmi.viewModel.AuthViewModel
import androidx.lifecycle.Observer
import org.json.JSONObject

class Declaration : AppCompatActivity() {

    private lateinit var loginObj: loginResponse

    private lateinit var loader: Dialog

    private lateinit var alert: AlertDialog.Builder
    private lateinit var alertdialog: AlertDialog
    private var state:Int = 100

    private var applicantDetails:ApplicationData? = null


    private val vm: AuthViewModel by lazy {
        getViewModel { AuthViewModel() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_declaration)

        val appData = intent.getStringExtra("APPLICANT_DATA")
        applicantDetails = Gson().fromJson(appData,ApplicationData::class.java)
        applicantDetails
        val prefs = PreferenceHelper.defaultPrefs(this)
        val obj = prefs.getString("LOGINDATA","")

        loginObj = Gson().fromJson(obj,loginResponse::class.java)


        loader = ProgressDialog.progressDialog(this)
        setVM()

        findViewById<ImageView>(R.id.ivBackBtn).setOnClickListener {
            finish()
        }

        findViewById<RadioButton>(R.id.yesTax).setOnClickListener {
            K.showMessageDialog(getString(R.string.tax_gst),"Error",this)
        }
        findViewById<RadioButton>(R.id.yesGST).setOnClickListener {
            K.showMessageDialog(getString(R.string.tax_gst),"Error",this)
        }

        findViewById<Button>(R.id.btnSubmit).setOnClickListener {
            if(findViewById<CheckBox>(R.id.consentchk).isChecked) {


                val jsonObject = JsonObject()
                jsonObject.addProperty("Mobile", loginObj.Mobile)
                jsonObject.addProperty("Token", loginObj.Token)

                vm.callAPI(K.CALL_HSMURL, jsonObject)
                loader.show()

            }else{
                K.showMessageDialog("Please tick the consent to process","Error",this)
            }
        }

    }

    fun getApplicationIndex():Int {
        val prefs = PreferenceHelper.defaultPrefs(this)
        var str = ""
        var index  = 1
        do {
            str = prefs.getString(index.toString(),"")!!
            if(!str.isNullOrEmpty()) {
                index++
            }
        }while(!str.isNullOrEmpty())
        return index
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

    /// Network calls
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


            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler?,
                error: SslError?
            ) {
                handler?.proceed()
            }
        }
        wv.loadUrl(newurl)
        // alert.setView(wv)

        alertdialog = AlertDialog.Builder(this)
            .setView(wv)
            .setNegativeButton("Cancel"
            ) { dialog, whichButton ->
                dialog.dismiss()
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

                val key = getApplicationIndex()
                val prefs = PreferenceHelper.defaultPrefs(this)
                val appData = prefs.getString("TEMP_DATA","")
                prefs.edit().putString(key.toString(), appData).commit()

                prefs.edit().putString("TEMP_DATA",null)
                startActivity(Intent(this, Dashboard::class.java))
                Toast.makeText(this,"Applicant Information Saved Successfully!!!",Toast.LENGTH_LONG).show()


//                val jsonObject = JsonObject()
//                jsonObject.addProperty("Mobile", loginObj.Mobile)
//                jsonObject.addProperty("Token", loginObj.Token)
//                jsonObject.addProperty("HSMURL",url)
//
//                vm.callAPI(K.CALL_FETCHCOMPLED, jsonObject)
//                loader.show()
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
}