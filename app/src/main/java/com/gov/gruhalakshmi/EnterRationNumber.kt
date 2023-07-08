package com.gov.gruhalakshmi

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.gson.Gson
import com.gov.gruhalakshmi.common.LanguageHelper
import com.gov.gruhalakshmi.common.PreferenceHelper
import com.gov.gruhalakshmi.common.ProgressDialog
import com.gov.gruhalakshmi.loginData.loginResponse
import com.gov.gruhalakshmi.network.repositories.Status
import com.gov.gruhalakshmi.shared.K
import com.gov.gruhalakshmi.shared.extension.getViewModel
import com.gov.gruhalakshmi.viewModel.AuthViewModel
import org.json.JSONObject
import java.util.regex.Matcher
import java.util.regex.Pattern
import androidx.lifecycle.Observer
import com.google.gson.JsonObject

class EnterRationNumber : AppCompatActivity() {
    private lateinit var loader: Dialog
    private lateinit var loginObj:loginResponse

    private var approvedStatus:String = "NV"

    private val vm: AuthViewModel by lazy {
        getViewModel { AuthViewModel() }
    }

    private var applicantDetails:ApplicationData? = null
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_ration_number)
        applicantDetails = ApplicationData("","","","",
        "","","","","",
        "","","","","","","",
        "","")

        val prefs = PreferenceHelper.defaultPrefs(this)
        val obj = prefs.getString("LOGINDATA","")

        loginObj = Gson().fromJson(obj,loginResponse::class.java)

        findViewById<AppCompatButton>(R.id.btnNext).setOnClickListener {
            if(!validate()) return@setOnClickListener

            if(approvedStatus.equals("NV",true) ||
                approvedStatus.equals("NF",true)  ){
                K.showMessageDialog(getString(R.string.rc_notverified),"Error",this)
                return@setOnClickListener
            }

            val isOtherBank = findViewById<RadioButton>(R.id.otherbank).isChecked
            if(isOtherBank){
                applicantDetails?.aadharlinked_bank = "no"
                val intent = Intent(this,BankDetails::class.java)
                val data = Gson().toJson(applicantDetails).toString()
                intent.putExtra("APPLICANT_DATA",data)
                intent.putExtra("APPROVED",approvedStatus)
                startActivity(intent)
            }else{
                applicantDetails?.aadharlinked_bank = "yes"
                if(approvedStatus.equals("A",true)){
                    val intent = Intent(this,UploadFiles::class.java)
                    val data = Gson().toJson(applicantDetails).toString()
                    intent.putExtra("APPLICANT_DATA",data)
                    startActivity(intent)
                }else {
                    val intent = Intent(this, SelectHusband::class.java)
                    val data = Gson().toJson(applicantDetails).toString()
                    intent.putExtra("APPLICANT_DATA", data)
                    startActivity(intent)
                }
            }
        }

        findViewById<AppCompatButton>(R.id.btnValidate).setOnClickListener {

            val rcTxt = findViewById<EditText>(R.id.editRc).text.toString()
            if(rcTxt.isNullOrEmpty()) return@setOnClickListener

            val jsonObject = JsonObject()
            jsonObject.addProperty("Mobile", loginObj.Mobile)
            jsonObject.addProperty("Token", loginObj.Token)
            jsonObject.addProperty("RcNo",rcTxt)

            vm.callAPI(K.CALL_RCCHECK, jsonObject)
            loader.show()

        }

        loader = ProgressDialog.progressDialog(this)
        setVM()

    }

    fun validate():Boolean{
        val rc = findViewById<EditText>(R.id.editRc).text
        val mob = findViewById<EditText>(R.id.editMobile).text
        val pos = findViewById<Spinner>(R.id.spinner).selectedItemPosition
        if(rc.isNullOrEmpty() || pos == 0 || mob.isNullOrEmpty()){
            K.showMessageDialog("Please fill required details","Error",this)
            return false
        }
        applicantDetails?.rcNo = rc.toString()

        if(isValid(mob.toString())){
            applicantDetails?.citizen_mobile = mob.toString()
        }else {
            K.showMessageDialog(getString(R.string.invalidMobile),"Validation Failed",this)
            return false
        }
        applicantDetails?.caste = findViewById<Spinner>(R.id.spinner).selectedItem.toString()

        val prefs = PreferenceHelper.defaultPrefs(this)
        val obj = prefs.getString("LOGINDATA","")
        val loginObj = Gson().fromJson(obj, loginResponse::class.java)
        applicantDetails?.Token = loginObj.Token
        applicantDetails?.Mobile = loginObj.Mobile

        return true
    }

    fun isValid(s: String?): Boolean {

        // The given argument to compile() method
        // is regular expression. With the help of
        // regular expression we can validate mobile
        // number.
        // 1) Begins with 0 or 91
        // 2) Then contains 6,7 or 8 or 9.
        // 3) Then contains 9 digits
        val p: Pattern = Pattern.compile("^[6-9][0-9]{9}")

        // Pattern class contains matcher() method
        // to find matching between given number
        // and regular expression
        val m: Matcher = p.matcher(s)
        return m.find() && m.group().equals(s)
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
                        val approved = obj.optString("Approved")
                        if(!approved.isNullOrEmpty()){
                            if(approved.equals("NF",true)){
                                val rcTxt = findViewById<EditText>(R.id.editRc)
                                rcTxt.setText("")
                                approvedStatus = approved
                            K.showMessageDialog(getString(R.string.invalid_RC),"Error",this)
                            return@Observer
                            }
                            approvedStatus = approved
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