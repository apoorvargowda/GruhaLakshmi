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
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.gov.gruhalakshmi.R
import com.gov.gruhalakshmi.aadharData.AadharDataResponse
import com.gov.gruhalakshmi.aadharData.Male
import com.gov.gruhalakshmi.common.CustomWebView
import com.gov.gruhalakshmi.common.LanguageHelper
import com.gov.gruhalakshmi.network.repositories.Status
import com.gov.gruhalakshmi.shared.K
import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import org.json.JSONObject
import androidx.lifecycle.Observer
import com.gov.gruhalakshmi.common.PreferenceHelper
import com.gov.gruhalakshmi.common.ProgressDialog
import com.gov.gruhalakshmi.loginData.loginResponse
import com.gov.gruhalakshmi.shared.extension.getViewModel
import com.gov.gruhalakshmi.viewModel.AuthViewModel

class SelectHusband : AppCompatActivity(),MaleAdapter.ItemClickListener {

    private lateinit var rcAdapter:MaleAdapter
    private lateinit var recyclerview: RecyclerView
    private lateinit var rcArray:ArrayList<Male>
    private lateinit var loginObj: loginResponse

    private lateinit var loader: Dialog

    private lateinit var alert:AlertDialog.Builder
    private lateinit var alertdialog:AlertDialog
    private var state = 100

    private val vm: AuthViewModel by lazy {
        getViewModel { AuthViewModel() }
    }


    private var applicantDetails:ApplicationData? = null
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_husband)

        val prefs = PreferenceHelper.defaultPrefs(this)
        val obj = prefs.getString("LOGINDATA","")

        val appData = intent.getStringExtra("APPLICANT_DATA")
        applicantDetails = Gson().fromJson(appData,ApplicationData::class.java)

        loginObj = Gson().fromJson(obj,loginResponse::class.java)


        loader = ProgressDialog.progressDialog(this)
        setVM()


        recyclerview = findViewById(R.id.husRecycler)
        val family = intent.getStringExtra("FAMILYDETAILS")

        findViewById<AppCompatButton>(R.id.btnHusbandSubmit).setOnClickListener {
            //startActivity(Intent(this, UploadFiles::class.java))
            if(!validate()) return@setOnClickListener

            val chk = findViewById<RadioButton>(R.id.husbandalive).isChecked
            if(chk) {
                val jsonObject = JsonObject()
                jsonObject.addProperty("Mobile", loginObj.Mobile)
                jsonObject.addProperty("Token", loginObj.Token)

                vm.callAPI(K.CALL_HSMURL, jsonObject)
                loader.show()
            }else {
                if(applicantDetails?.bank_account_no.isNullOrEmpty()){
                    val appdata = Gson().toJson(applicantDetails).toString()
                    val prefs = PreferenceHelper.defaultPrefs(this)
                    prefs.edit().putString("TEMP_DATA",appdata).commit()

                    val intent = Intent(this, Declaration::class.java)
                    val data = Gson().toJson(applicantDetails).toString()
                    intent.putExtra("APPLICANT_DATA", data)
                    startActivity(intent)
                }else {
                    val intent = Intent(this, UploadFiles::class.java)
                    val data = Gson().toJson(applicantDetails).toString()
                    intent.putExtra("APPLICANT_DATA", data)
                    startActivity(intent)
                }
            }
        }

        findViewById<Button>(R.id.ekycHusband).setOnClickListener {
            val jsonObject = JsonObject()
            jsonObject.addProperty("Mobile", loginObj.Mobile)
            jsonObject.addProperty("Token", loginObj.Token)

            vm.callAPI(K.CALL_HSMURL, jsonObject)
            loader.show()
        }

        findViewById<ImageView>(R.id.ivBackBtn).setOnClickListener {
            finish()
        }

        findViewById<RadioButton>(R.id.husbandalive).setOnClickListener {
            val btn = findViewById<AppCompatButton>(R.id.btnHusbandSubmit)
            btn.text = getString(R.string.ekyc_of_husband)
        }
        findViewById<RadioButton>(R.id.husbanddeceased).setOnClickListener {
            val btn = findViewById<AppCompatButton>(R.id.btnHusbandSubmit)
            btn.text = getString(R.string.next)
        }
        findViewById<RadioButton>(R.id.husbandseperated).setOnClickListener {
            val btn = findViewById<AppCompatButton>(R.id.btnHusbandSubmit)
            btn.text = getString(R.string.next)
        }
        findViewById<RadioButton>(R.id.unmarried).setOnClickListener {
            val btn = findViewById<AppCompatButton>(R.id.btnHusbandSubmit)
            btn.text = getString(R.string.next)
        }

//        val familyDetails = Gson().fromJson(family, AadharDataResponse::class.java)
//        rcArray = ArrayList()
//        rcArray.clear()
//        familyDetails.Male.forEach {
//            rcArray.add(it)
//        }
//        SetListData(rcArray)
    }

    private fun SetListData(data: ArrayList<Male>) {
        if (data.size > 0) {
            rcArray = data
            recyclerview.layoutManager = LinearLayoutManager(this)
            rcAdapter = MaleAdapter(this, data)
            rcAdapter.setClickListener(this)

            //myCaveatsAdapter!!.setClickListener(this)
            recyclerview.adapter = rcAdapter
            //noRecords.visibility = View.GONE
            recyclerview.visibility = View.VISIBLE
        } else {
            //noRecords.visibility = View.VISIBLE
            recyclerview.visibility = View.GONE
        }
    }

    override fun onItemModelClick(view: View?, position: Int) {
      //  TODO("Not yet implemented")
    }

    fun validate():Boolean{
        val unmarried = findViewById<RadioButton>(R.id.unmarried).isChecked
        val seperated = findViewById<RadioButton>(R.id.husbandseperated).isChecked
        val deceased = findViewById<RadioButton>(R.id.husbanddeceased).isChecked
        val addnew = findViewById<RadioButton>(R.id.husbandalive).isChecked

        val name = findViewById<EditText>(R.id.apllicateName).text
        val aadhaar = findViewById<EditText>(R.id.aadhaarNumber).text

        if(!unmarried && !seperated && !deceased && !addnew) {
            K.showMessageDialog("Please fill required details","Error",this)
            return false
        }

        if(addnew){
            if(name.isNullOrEmpty() && aadhaar.isNullOrEmpty()){
                K.showMessageDialog("Husband Aadhaar Details are Mandatory","Error",this)
                return false
            }
        }

        val appData = intent.getStringExtra("APPLICANT_DATA")
        applicantDetails = Gson().fromJson(appData,ApplicationData::class.java)
        if(unmarried) applicantDetails?.husband_status = "unmarried"
        if(seperated) applicantDetails?.husband_status = "seperated"
        if(deceased) applicantDetails?.husband_status = "deceased"
        if(addnew) applicantDetails?.husband_status = "addnew"
        applicantDetails?.husband_name = name.toString()
        val ahhash = String(Hex.encodeHex(DigestUtils.sha256(aadhaar.toString())))
        applicantDetails?.husband_aadhaar = ahhash
        return true
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

                if(applicantDetails?.bank_account_no.isNullOrEmpty()){
                    val appdata = Gson().toJson(applicantDetails).toString()
                    val prefs = PreferenceHelper.defaultPrefs(this)
                    prefs.edit().putString("TEMP_DATA",appdata).commit()

                    val intent = Intent(this, Declaration::class.java)
                    val data = Gson().toJson(applicantDetails).toString()
                    intent.putExtra("APPLICANT_DATA", data)
                    startActivity(intent)
                }else {
                    val intent = Intent(this, UploadFiles::class.java)
                    val data = Gson().toJson(applicantDetails).toString()
                    intent.putExtra("APPLICANT_DATA", data)
                    startActivity(intent)
                }

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