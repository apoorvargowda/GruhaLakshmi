package com.gov.gruhalakshmi

import android.app.Dialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.gov.gruhalakshmi.R
import com.gov.gruhalakshmi.common.ProgressDialog
import com.gov.gruhalakshmi.network.repositories.Status
import com.gov.gruhalakshmi.shared.K
import com.gov.gruhalakshmi.shared.extension.getViewModel
import com.gov.gruhalakshmi.viewModel.AuthViewModel
import org.json.JSONObject
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.gov.gruhalakshmi.aadharData.AadharDataResponse

class ApplicantDetails : AppCompatActivity() {
    private lateinit var loader: Dialog
    private lateinit var familyDetails:AadharDataResponse
    private var otpValue:String = ""
    private var familyData:String = ""

    private val vm: AuthViewModel by lazy {
        getViewModel { AuthViewModel() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_applicant_details)

        loader = ProgressDialog.progressDialog(this)
        setVM()

        familyData = intent.getStringExtra("FAMILYDETAILS")!!
        familyDetails = Gson().fromJson(familyData,AadharDataResponse::class.java)

        val name = findViewById<EditText>(R.id.apllicateName)
        name.setText(familyDetails.FamilyHead.get(0).MEMBER_NAME_ENG)
        val adno = findViewById<EditText>(R.id.aadhaarNumber)
        adno.setText(familyDetails.FamilyHead.get(0).MBR_AADHAR_NO)
        val dob = findViewById<EditText>(R.id.DOB)
        dob.setText(familyDetails.FamilyHead.get(0).MBR_DOB)
        val rctype = findViewById<EditText>(R.id.RCType)
        rctype.setText(familyDetails.FamilyHead.get(0).RC_TYPE)
        val gender = findViewById<EditText>(R.id.gender)
        gender.setText(familyDetails.FamilyHead.get(0).MBR_GENDER)
        val category = findViewById<EditText>(R.id.category)
        //category.setText(familyDetails.FamilyHead.get(0).CA)
        val state = findViewById<EditText>(R.id.state)
        //state.setText(familyDetails.FamilyHead.get(0))

        //phonenumber.setText(familyDetails.FamilyHead.get(0).MEMBER_NAME_ENG)


        findViewById<Button>(R.id.btnOTP).setOnClickListener {
            val phonenumber = findViewById<EditText>(R.id.phonenumber)
            if(phonenumber.text.isNullOrEmpty()) return@setOnClickListener

            val jsonObject = JsonObject()
            jsonObject.addProperty("Mobile", phonenumber.text.toString())
            vm.callAPI(K.CALL_VALIDATEOTP, jsonObject)
            loader.show()
//            startActivity(
//                Intent(
//                    this, SelectHusband::class.java
//                )
//            )
        }
        findViewById<Button>(R.id.btnValidate).setOnClickListener {
            val intent = Intent(this, SelectHusband::class.java)
            intent.putExtra("FAMILYDETAILS",familyData)
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.ivBackBtn).setOnClickListener {
            finish()
        }
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
                            otpValue = obj.optString("OTP")
                            val otpview = findViewById<EditText>(R.id.otpvalue)
                            otpview.visibility = View.VISIBLE
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